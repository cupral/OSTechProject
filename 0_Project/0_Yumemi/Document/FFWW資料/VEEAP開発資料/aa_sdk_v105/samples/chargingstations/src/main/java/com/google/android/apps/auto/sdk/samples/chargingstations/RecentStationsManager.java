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
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStationManager;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * RecentStationsManager keeps track of the most recent stations selected by the user.
 */
public class RecentStationsManager {
    private static final String TAG = "RecentStationsManager";

    // Maximum number of recent stations to store
    private static final int MAX_RECENT_STATIONS_COUNT = 5;

    private static final String PREFS_NAME = "recent_stations.prefs";
    private static final String RECENTS_KEY = "jsonRecents";

    private final Context mContext;
    private final ChargingStationManager mChargingStationManager;
    private final Deque<ChargingStation> mRecentStations = new LinkedList<>();

    public RecentStationsManager(
            @NonNull Context context, @NonNull ChargingStationManager chargingStationManager) {
        mContext = context;
        mChargingStationManager = chargingStationManager;
    }

    /**
     * Returns the list of most recently selected stations. The most recently selected station is
     * the first one in the returned list. If no recently selected stations are saved, returns an
     * empty list. Never returns null.
     */
    public List<ChargingStation> getRecentStations() {
        return new ArrayList<>(mRecentStations);
    }

    /**
     * Loads the list of recent stations from storage into memory.
     */
    public void load() {
        mRecentStations.clear();
        mRecentStations.addAll(readStations());
    }

    /**
     * Saves the list of recent station to storage.
     */
    public void save() {
        writeStations(getRecentStations());
    }

    /**
     * Adds a station to the most recently selected stations. If the station already exists in the
     * list, it is moved to the top of the list. Otherwise the station is added to the top of the
     * list.
     */
    public void addRecentStation(@NonNull ChargingStation station) {
        // Remove any existing entry for the station to be added to top
        mRecentStations.remove(station);
        // If list is full, remove the least recent (last) entry
        if (mRecentStations.size() == MAX_RECENT_STATIONS_COUNT) {
            mRecentStations.pollLast();
        }
        // Insert the new entry on top
        mRecentStations.addFirst(station);
    }

    /**
     * Returns the list of most recently selected stations. If no recently selected stations are
     * saved, returns an empty list. Never returns null.
     */
    @NonNull
    private List<ChargingStation> readStations() {
        // Recent stations are stored in a SharedPreferences file under RECENTS_KEY
        // The value of RECENTS_KEY is space separated list of stations ids
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String recentStationsString = prefs.getString(RECENTS_KEY, "");
        List<ChargingStation> stations = new ArrayList<>();
        if (!recentStationsString.isEmpty()) {
            String[] recentStationIds = recentStationsString.split("\\s");
            for (String idString : recentStationIds) {
                try {
                    ChargingStation station =
                            mChargingStationManager.findStationById(Long.parseLong(idString));
                    if (station != null) {
                        stations.add(station);
                    }
                } catch (NumberFormatException ex) {
                    Log.e(TAG, "Invalid charging station ID: " + idString, ex);
                }
            }
        }
        return stations;
    }

    /**
     * Saves the list of (most recently selected) stations.
     */
    private void writeStations(@NonNull List<ChargingStation> stations) {
        // Store the station ids as a space separated string into a SharedPreferences file
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Generate space separated string of
        List<Long> stationIds  = new ArrayList<>();
        for (ChargingStation station : stations) {
            stationIds.add(station.id);
        }
        prefs.edit().putString(RECENTS_KEY, TextUtils.join(" ", stationIds)).apply();
    }
}
