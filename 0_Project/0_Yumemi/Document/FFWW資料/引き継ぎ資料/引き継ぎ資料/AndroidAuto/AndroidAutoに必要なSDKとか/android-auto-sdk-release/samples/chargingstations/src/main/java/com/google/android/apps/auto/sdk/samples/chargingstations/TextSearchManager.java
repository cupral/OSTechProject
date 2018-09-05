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

package com.google.android.apps.auto.sdk.samples.chargingstations;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import com.google.android.apps.auto.sdk.SearchCallback;
import com.google.android.apps.auto.sdk.SearchController;
import com.google.android.apps.auto.sdk.SearchItem;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStationManager;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.location.UserLocationProvider;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * TextSearchManager provides auto-completion suggestions and search results to full text
 * charging station search field in the app.
 */
public class TextSearchManager extends SearchCallback {
    // Keys used to store charging stations in Bundles (in SearchItems)
    private static final String KEY_STATION_ID = "stationId";
    private static final String KEY_STATION_DISTANCE = "stationDistance";

    private final Context mContext;
    private final ChargingStationManager mChargingStationManager;
    private final SearchController mSearchController;
    private UserLocationProvider mUserLocationProvider;
    private StationSelectionListener mStationSelectionListener;

    private boolean mIsDbReady;
    private boolean mIsGoogleMapReady;
    private boolean mIsBatteryStatusPanelShowing;

    TextSearchManager(Context context, @NonNull SearchController searchController,
            @NonNull ChargingStationManager chargingStationManager) {
        mContext = context;
        mSearchController = searchController;
        mChargingStationManager = chargingStationManager;
    }

    void setStationSelectionListener(StationSelectionListener stationSelectionListener) {
        mStationSelectionListener = stationSelectionListener;
    }

    void setUserLocationProvider(UserLocationProvider userLocationProvider) {
        mUserLocationProvider = userLocationProvider;
    }

    // Implements SearchCallback
    @Override
    public void onSearchTextChanged(String searchTerm) {
        // Only search for suggestions when there's more than 1 character to avoid too many results
        if (searchTerm.length() > 1) {
            // Find suggestions, but don't select even if only one match is found
            new FindSuggestions(false /* autoSelect */).execute(searchTerm);
        }
    }

    // Implements SearchCallback
    @Override
    public boolean onSearchSubmitted(String searchTerm) {
        if (searchTerm.length() > 0) {
            // Perform search, and if only one result matches, select it automatically
            new FindSuggestions(true /* autoSelect */).execute(searchTerm);
        }
        return false;
    }

    // Implements SearchCallback
    @Override
    public void onSearchItemSelected(SearchItem searchItem) {
        // Get charging station information stored in the extras in SearchItem
        Bundle extras = searchItem.getExtras();
        if (mStationSelectionListener != null && extras != null) {
            NearbyStation nearbyStation = nearbyStationFromBundle(extras);
            mStationSelectionListener.onStationSelected(nearbyStation);
        }
    }

    public void setBatteryStatusPanelShowing(boolean batteryStatusPanelShowing) {
        mIsBatteryStatusPanelShowing = batteryStatusPanelShowing;
        if (mIsDbReady && mIsGoogleMapReady && !mIsBatteryStatusPanelShowing) {
            mSearchController.showSearchBox();
        } else {
            mSearchController.hideSearchBox();
        }
    }

    public void onDbReady() {
        mIsDbReady = true;
        if (mIsGoogleMapReady && !mIsBatteryStatusPanelShowing) {
            mSearchController.showSearchBox();
        }
    }

    public void onGoogleMapReady() {
        mIsGoogleMapReady = true;
        if (mIsDbReady && !mIsBatteryStatusPanelShowing) {
            mSearchController.showSearchBox();
        }
    }

    /**
     * Stores NearbyStation object to a Bundle.
     */
    private Bundle nearbyStationToBundle(NearbyStation nearbyStation) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_STATION_ID, nearbyStation.getStation().id);
        bundle.putInt(KEY_STATION_DISTANCE, nearbyStation.getDistanceMeters());
        return bundle;
    }

    /**
     * Restores NearbyStation object from a Bundle that was created using nearbyStationToBundle().
     *
     * @param bundle Bundle created with a call to nearbyStationToBundle().
     * @return NearbyStation object stored in the bundle or null, if no station data is found.
     */
    private NearbyStation nearbyStationFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        long stationId = bundle.getLong(KEY_STATION_ID, -1);
        ChargingStation station = mChargingStationManager.findStationById(stationId);
        if (station == null) {
            return null;
        }
        int distanceMeters = bundle.getInt(KEY_STATION_DISTANCE, NearbyStation.UNKNOWN_DISTANCE);
        return new NearbyStation(station, distanceMeters);
    }

    /**
     * FindSuggestions is a background task that finds matching stations and delivers them
     */
    private class FindSuggestions extends AsyncTask<String, Void, List<SearchItem>> {
        private boolean mAutoSelect;

        FindSuggestions(boolean autoSelect) {
            mAutoSelect = autoSelect;
        }

        @Override
        protected List<SearchItem> doInBackground(String... params) {
            String searchTerm = params[0];
            List<ChargingStation> stations = mChargingStationManager.findMatches(searchTerm);
            // Don't show suggestions if more than 100 matches are found (measuring distances and
            // sorting might take too long). User can type more letters
            if (stations.size() > 100) {
                return Collections.emptyList();
            }
            // Sort found suggestions by distance
            LatLng userLocation = mUserLocationProvider.getUserLocation();
            List<NearbyStation> searchSuggestions =
                    NearbyStation.sortStationsByDistance(stations, userLocation);
            List<SearchItem> results = new ArrayList<>(stations.size());
            for (NearbyStation station : searchSuggestions) {
                results.add(nearbyStationToSearchItem(station));
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<SearchItem> searchResults) {
            mSearchController.setSearchItems(searchResults);
            // If there is only one result and auto selection was requested, select the result
            if (mAutoSelect && searchResults.size() == 1) {
                onSearchItemSelected(searchResults.get(0));
            }
        }

        /**
         * Creates a SearchItem for a NearbyStation object storing the station information in the
         * extras in the SearchItem.
         */
        private SearchItem nearbyStationToSearchItem(NearbyStation nearby) {
            Spannable distance = null;
            if (nearby.isDistanceKnown()) {
                // Show distances in miles, real app should use units based on user's locale
                distance = new SpannableString(
                        String.format(Locale.US, "%.1f mi", nearby.getDistanceMiles()));
                int color = ContextCompat.getColor(mContext, R.color.search_distance_color);
                distance.setSpan(new ForegroundColorSpan(color), 0, distance.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return new SearchItem.Builder()
                    .setType(SearchItem.Type.SEARCH_RESULT)
                    .setExtras(nearbyStationToBundle(nearby))
                    .setTitle(nearby.getStation().name)
                    .setSubtitle(nearby.getStation().getFullAddress())
                    .setDescription(distance)
                    .build();
        }
    }
}
