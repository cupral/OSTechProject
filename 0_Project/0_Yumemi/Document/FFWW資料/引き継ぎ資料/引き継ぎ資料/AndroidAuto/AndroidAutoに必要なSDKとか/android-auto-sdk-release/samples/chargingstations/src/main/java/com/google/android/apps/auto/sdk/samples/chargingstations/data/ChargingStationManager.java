// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.apps.auto.sdk.samples.chargingstations.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Simple class managing EV charging station entries stored in a TSV-file in assets (TSV = Tab
 * Separated Values).
 */
public class ChargingStationManager {
    // TAG used in Log entries
    private static final String TAG = "CSData";

    private static final String ASSET_TSV_FILE = "BayAreaChargingStations.tsv";

    private final Context mContext;
    // AssetManager providing access to charging station data file
    private final AssetManager mAssetManager;
    // Class providing full text search for charging station data
    private FullTextSearch mFullTextSearch;
    // Optional callback to be called when database initialization is completed
    private DbReadyCallback mDbReadyCallback;
    // Flag to be set when database initialization is completed
    private boolean mIsDbReady = false;
    // List of all charging stations. In a real app these would be stored and read from a database,
    // but for sake of simpler code, the small data set used by this example is kept in memory.
    private final List<ChargingStation> mChargingStations = new ArrayList<>();

    /**
     * Creates a charging station data manager that prepares charging station data to be queried
     * both by location and by string matching.
     *
     * @param context current context
     * @param dbReadyCallback callback object to be notified when database is ready for queries
     */
    public ChargingStationManager(
            @NonNull Context context, @Nullable DbReadyCallback dbReadyCallback) {
        mContext = context;
        mAssetManager = context.getAssets();
        mDbReadyCallback = dbReadyCallback;
        // Prepare the database in a background thread
        new PrepareDatabase().execute();
    }

    /**
     * Returns true if the database has been initialized, and ChargingStationManager is ready to
     * receive search requests.
     */
    public boolean isDbReady() {
        return mIsDbReady;
    }

    /**
     * Returns a Collection containing all the ChargingStations in the database in arbitrary order.
     */
    public Collection<ChargingStation> getAllChargingStations() {
        // NOTE: if the charging stations were read from a remote server or from a real database,
        // this method should be called in a background thread to prevent the UI from freezing
        return new ArrayList<>(mChargingStations);
    }

    /**
     * Finds the closest charging stations to a location. The potentially long running search is
     * executed in a background thread to avoid blocking the UI and the results are delivered to
     * the given callback object in main thread once the search is completed.
     *
     * @param place location from which distances to stations are computed
     * @param count maximum number of stations to be returned
     * @param callback object to receive the list of closest stations
     */
    public void findClosestStations(
            @NonNull LatLng place, int count, @NonNull ResultsCallback callback) {
        if (place == null || count < 1 || callback == null) {
            throw new IllegalArgumentException("invalid params");
        }
        // Create a data structure to pass parameters to background task to perform the search
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.place = place;
        searchRequest.count = count;
        searchRequest.callback = callback;
        // Execute the potentially slow search operation in a background thread
        new FindClosest().execute(searchRequest);
    }

    /**
     * Returns a charging station by its ID.
     */
    public ChargingStation findStationById(long id) {
        // Station IDs are assigned to match their index in the list. A real app would most
        // likely read the station data from a database.
        if (id < 0 || mChargingStations.size() <= id) {
            return null;
        }
        return mChargingStations.get((int) id);
    }

    /**
     * Finds full text matches for a given prefix for a word or a phrase. Searches through the
     * names and addresses of the charging stations.
     *
     * @param prefix text starting at a beginning of a word to be found in the database
     * @return List of charging stations containing the prefix
     */
    public List<ChargingStation> findMatches(String prefix) {
        List<Long> stationIds = mFullTextSearch.match(prefix);
        List<ChargingStation> matchingStations = new ArrayList<>(stationIds.size());
        for (Long id : stationIds) {
            ChargingStation chargingStation = findStationById(id);
            if (chargingStation != null) {
                matchingStations.add(chargingStation);
            }
        }
        return matchingStations;
    }

    /**
     * Reads the charging station data from a TSV file stored in assets. Ignores any stations with
     * invalid or missing lat/lon coordinates.
     *
     * <p>
     * A real implementation should not read a whole database in memory all at once as it could
     * exceed the memory available for apps. Our example dataset contains only ~100 records so it
     * is loaded all at once to simplify the example code.
     *
     * @return List of charging stations, may return an empty list, but never returns null
     * @throws IOException if file operations (open/read) fail
     */
    private List<ChargingStation> readStations() throws IOException {
        InputStream in = null;
        BufferedReader reader = null;
        List<ChargingStation> stations = new ArrayList<>();
        try {
            in = mAssetManager.open(ASSET_TSV_FILE);
            reader = new BufferedReader(new InputStreamReader(in));
            // Skip the header line
            reader.readLine();
            // Each one of the rest of the lines contain a single station definition in TSV format
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty and comment lines
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                try {
                    // Use the index of the station in the result list as its ID
                    int id = stations.size();
                    ChargingStation station = new ChargingStation(id, line);
                    stations.add(station);
                } catch (NumberFormatException ex) {
                    // Skip entries with invalid lat/lon values
                    Log.w(TAG, "Skipping a station definition with invalid lat/lon values", ex);
                }
            }
            return stations;
        } finally {
            closeQuietly(reader);
            closeQuietly(in);
        }
    }

    /**
     * Attempts to close a Closeable object and ignores any IOExceptions thrown while closing.
     */
    private void closeQuietly(@Nullable Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
                // Ignore exceptions
            }
        }
    }

    /**
     * Interface used for notifying the app when the database is ready for queries.
     */
    public interface DbReadyCallback {
        /**
         * Called when charging station database has been prepared for queries. This method is
         * called in the main UI thread.
         */
        @UiThread
        void onDbReady();
    }

    /**
     * Interface used for returning back search results for closest charging stations.
     */
    public interface ResultsCallback {
        /**
         * Called to return results for a search for closest charging stations.
         *
         * @param stations List containing at most requested number of closest charging stations.
         * The stations are listed from closest to furthest. If there is an error accessing the
         * station data, the list is null. Otherwise the list contains always at least one station.
         */
        void closestStations(@Nullable List<NearbyStation> stations);
    }

    /**
     * Internal data structure for passing the closest station search parameters to background task
     */
    private static class SearchRequest {
        LatLng place;
        int count;
        ResultsCallback callback;
    }

    /**
     * Background task to read all charging station data into memory.
     */
    private class PrepareDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Read all the stations from an asset file
                mChargingStations.addAll(readStations());
                // Prepare a full text search database
                mFullTextSearch = new FullTextSearch(mContext);
                if (!mFullTextSearch.isPopulated()) {
                    mFullTextSearch.populateTable(mChargingStations);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Failed to read station list from assets", ex);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mIsDbReady = true;
            if (mDbReadyCallback != null) {
                mDbReadyCallback.onDbReady();
                // Clear the now unnecessary callback reference
                mDbReadyCallback = null;
            }
        }
    }

    /**
     * Background task that will find up to a specified number of closest stations to a location.
     */
    private class FindClosest extends AsyncTask<SearchRequest, Void, List<NearbyStation>> {
        private ResultsCallback mResultsCallback;

        @Override
        protected List<NearbyStation> doInBackground(SearchRequest... params) {
            mResultsCallback = params[0].callback;
            LatLng place = params[0].place;
            int count = params[0].count;

            ArrayList<NearbyStation> closest = new ArrayList<>(count);
            for (ChargingStation station : mChargingStations) {
                NearbyStation nearbyStation = new NearbyStation(station, place);
                // Add station to list of closest ones if the list is not full, or if it is closer
                // than last item in the list.
                if (closest.size() < count || nearbyStation.compareTo(closest.get(count - 1)) < 0) {
                    // Remove last item from the list if the list is full
                    if (closest.size() == count) {
                        closest.remove(count - 1);
                    }
                    // Find the sorted location for the station to be inserted and insert it
                    int index = Collections.binarySearch(closest, nearbyStation);
                    if (index < 0) {
                        index = -(index + 1);
                    }
                    closest.add(index, nearbyStation);
                }
            }
            return closest;
        }

        @Override
        protected void onPostExecute(List<NearbyStation> nearbyStations) {
            // Deliver results to callback object
            mResultsCallback.closestStations(nearbyStations);
        }
    }
}
