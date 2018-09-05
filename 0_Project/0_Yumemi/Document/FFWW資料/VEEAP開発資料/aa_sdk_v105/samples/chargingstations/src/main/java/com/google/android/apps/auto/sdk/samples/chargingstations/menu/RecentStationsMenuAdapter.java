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

package com.google.android.apps.auto.sdk.samples.chargingstations.menu;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.google.android.apps.auto.sdk.MenuAdapter;
import com.google.android.apps.auto.sdk.MenuItem;
import com.google.android.apps.auto.sdk.samples.chargingstations.R;
import com.google.android.apps.auto.sdk.samples.chargingstations.RecentStationsManager;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuAdapter for recent stations submenu.
 */
class RecentStationsMenuAdapter extends MenuAdapter {
    private final RecentStationsManager mRecentStationsManager;
    private final List<NearbyStation> mRecentStations = new ArrayList<>();
    private final List<MenuItem> mMenuItems = new ArrayList<>();
    private ChargingStationMenuAdapter.MenuCallbacks mMenuCallbacks;
    private MenuItem mNoRecentStationsMenuItem;
    private Resources mResources;

    /**
     * Creates a new recent stations submenu.
     *
     * @param resources resources used to look up message to be displayed when no stations are
     * available
     * @param recentStationsManager RecentStationsManager to provide contents for this menu
     * @param menuCallbacks object to be notified about selected charging station
     */
    RecentStationsMenuAdapter(@NonNull Resources resources,
            @NonNull RecentStationsManager recentStationsManager,
            @NonNull ChargingStationMenuAdapter.MenuCallbacks menuCallbacks) {
        mResources = resources;
        mRecentStationsManager = recentStationsManager;
        mMenuCallbacks = menuCallbacks;
        // Create special menu item to be displayed if there are no recent stations
        mNoRecentStationsMenuItem = new MenuItem.Builder()
                .setTitle(resources.getString(R.string.menu_no_recent_stations))
                .build();
    }

    /**
     * Updates the list of recent stations to be displayed in the menu.
     */
    void updateRecentStations() {
        mMenuItems.clear();
        mRecentStations.clear();
        // Get list of most recently used stations and compute distances to them
        LatLng userLocation = mMenuCallbacks.getUserLocation();
        for (ChargingStation station : mRecentStationsManager.getRecentStations()) {
            mRecentStations.add(new NearbyStation(station, userLocation));
        }
        // Generate the menu items
        for (NearbyStation nearbyStation : mRecentStations) {
            String subtitle;
            if (nearbyStation.isDistanceKnown()) {
                // Show distances in miles, real app should use units based on user's locale
                subtitle = mResources.getString(R.string.menu_station_subtitle,
                        nearbyStation.getDistanceMiles(),
                        nearbyStation.getStation().getFullAddress());
            } else {
                subtitle = nearbyStation.getStation().getFullAddress();
            }
            mMenuItems.add(new MenuItem.Builder()
                    .setTitle(nearbyStation.getStation().name)
                    .setSubtitle(subtitle)
                    .build());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getMenuItemCount() {
        // There will always be at least one entry (for "no recent stations" if nothing else)
        return Math.max(1, mMenuItems.size());
    }

    @Override
    public MenuItem getMenuItem(int position) {
        // If menu items list is empty, return special "no recent stations" entry
        if (mMenuItems.isEmpty()) {
            return mNoRecentStationsMenuItem;
        }
        return mMenuItems.get(position);
    }

    @Override
    public void onMenuItemClicked(int position) {
        // If there are no recent stations, the "no recent stations" was clicked, ignore
        if (mMenuItems.isEmpty()) {
            return;
        }
        // Notify listener about selected station
        NearbyStation nearbyStation = mRecentStations.get(position);
        mMenuCallbacks.onRecentChargingStationSelected(nearbyStation);
    }
}
