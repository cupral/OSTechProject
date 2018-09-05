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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.apps.auto.sdk.MenuAdapter;
import com.google.android.apps.auto.sdk.MenuItem;
import com.google.android.apps.auto.sdk.samples.chargingstations.R;
import com.google.android.apps.auto.sdk.samples.chargingstations.RecentStationsManager;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

/**
 * ChargingStationMenuAdapter implements top level menu for the charging station example app. It
 * contains three menu items: a submenu to select a recently used charging station, a checkbox to
 * enable or disable use of fake location, and a menu item to post a (fake) battery notification
 * to the system.
 */
public class ChargingStationMenuAdapter extends MenuAdapter {
    private static final String KEY_USE_FAKE_LOCATION =
            ChargingStationMenuAdapter.class.getCanonicalName() + ".fakeLocation";
    private static final String KEY_ID = "id";
    private static final int ID_RECENT_STATIONS = 1;
    private static final int ID_ENABLE_FAKE_LOCATION = 2;
    private static final int ID_CREATE_NOTIFICATION = 3;

    private final List<MenuItem> mMenuItems = new ArrayList<>();
    private RecentStationsMenuAdapter mRecentStationsMenuAdapter;
    private MenuCallbacks mMenuCallbacks;

    /**
     * Creates a MenuAdapter for top level menu.
     *
     * @param context context of the activity
     * @param savedState Bundle containing the saved state of the menu (see
     * {@link #saveMenuState(Bundle)})
     * @param recentStationsManager RecentStationsManager for populating recent stations submenu
     * @param menuCallbacks object to be notified of menu selections and one that can provide user
     * location when needed (to update distances to recent stations).
     */
    public ChargingStationMenuAdapter(@NonNull Context context, @Nullable Bundle savedState,
            @NonNull RecentStationsManager recentStationsManager,
            @NonNull MenuCallbacks menuCallbacks) {
        mMenuCallbacks = menuCallbacks;
        Resources resources = context.getResources();
        // Add a dynamically populated submenu for recent stations
        Bundle extras = new Bundle();
        extras.putInt(KEY_ID, ID_RECENT_STATIONS);
        mMenuItems.add(new MenuItem.Builder()
                .setExtras(extras)
                .setType(MenuItem.Type.SUBMENU)
                .setTitle(resources.getString(R.string.menu_recents))
                .build());
        // Add a checkbox to enable/disable creation of (fake) notifications (and restore its state
        // from savedState if provided)
        boolean useFakeLocation =
                savedState != null && savedState.getBoolean(KEY_USE_FAKE_LOCATION);
        extras = new Bundle();
        extras.putInt(KEY_ID, ID_ENABLE_FAKE_LOCATION);
        mMenuItems.add(new MenuItem.Builder()
                .setExtras(extras)
                .setType(MenuItem.Type.CHECKBOX)
                .setTitle(resources.getString(R.string.menu_fake_location))
                .setChecked(useFakeLocation)
                .build());
        // Add a menu item to create a (fake) low battery notification
        extras = new Bundle();
        extras.putInt(KEY_ID, ID_CREATE_NOTIFICATION);
        mMenuItems.add(new MenuItem.Builder()
                .setExtras(extras)
                .setType(MenuItem.Type.ITEM)
                .setTitle(resources.getString(R.string.menu_create_notification))
                .build());
        // Creates a menu adapter for recent stations submenu
        mRecentStationsMenuAdapter =
                new RecentStationsMenuAdapter(resources, recentStationsManager, menuCallbacks);
    }

    /**
     * Saves the current menu state into a bundle. Activity should call this method in its
     * onSaveInstanceState() method, and pass the same Bundle to the constructor when the menu is
     * being rebuilt).
     *
     * @param bundle Bundle to store the menu state in
     */
    public void saveMenuState(Bundle bundle) {
        bundle.putBoolean(KEY_USE_FAKE_LOCATION, isFakeLocationChecked());
    }

    /**
     * Returns true if use of fake location is selected in the menu.
     */
    public boolean isFakeLocationChecked() {
        MenuItem fakeLocationItem = findMenuItemById(ID_ENABLE_FAKE_LOCATION);
        return fakeLocationItem.isChecked();
    }

    @Override
    public void onEnter() {
        super.onEnter();
        // Update recent stations submenu every time the menu is displayed, as new entries may
        // have been added and/or the user distance to them may have changed
        updateRecentStationsMenu();
    }

    @Override
    public MenuAdapter onLoadSubmenu(int position) {
        MenuItem menuItem = getMenuItem(position);
        // The only submenu is the one listing recent stations
        if (menuItem.getExtras().getInt(KEY_ID) == ID_RECENT_STATIONS) {
            return mRecentStationsMenuAdapter;
        }
        // This should never happen
        throw new IllegalArgumentException("Unrecognized submenu requested, position: " + position);
    }

    @Override
    public void onMenuItemClicked(int position) {
        MenuItem menuItem = getMenuItem(position);
        if (menuItem.getExtras().getInt(KEY_ID) == ID_ENABLE_FAKE_LOCATION) {
            mMenuCallbacks.onUseFakeLocationChanged(menuItem.isChecked());
        } else if (menuItem.getExtras().getInt(KEY_ID) == ID_CREATE_NOTIFICATION) {
            mMenuCallbacks.postBatteryNotification();
        }
    }

    @Override
    public int getMenuItemCount() {
        return mMenuItems.size();
    }

    @Override
    public MenuItem getMenuItem(int position) {
        return mMenuItems.get(position);
    }

    /**
     * Updates recent stations menu reading the current items that should be displayed there and
     * computes distances to them.
     */
    public void updateRecentStationsMenu() {
        mRecentStationsMenuAdapter.updateRecentStations();
    }

    /**
     * Finds a menu item based on the ID assigned to it.
     *
     * @param id ID to be matched with a MenuItem
     * @return MenuItem matching the ID or null if no MenuItem matches the ID.
     */
    private MenuItem findMenuItemById(int id) {
        for (int i = 0; i < getMenuItemCount(); i++) {
            MenuItem menuItem = getMenuItem(i);
            if (menuItem.getExtras().getInt(KEY_ID) == id) {
                return menuItem;
            }
        }
        return null;
    }

    /**
     * Interface for receiving menu selection notifications, and to provide user location for the
     * MenuAdapter.
     */
    public interface MenuCallbacks {
        /**
         * Called to find out user's location to include distance to recent stations in the menus.
         * If user location is not known, should return null.
         *
         * @return the latest available user's location
         */
        @Nullable LatLng getUserLocation();

        /**
         * Called to notify that the user selected a specific recent charging station.
         *
         * @param station station that was selected
         */
        void onRecentChargingStationSelected(NearbyStation station);

        /**
         * Called to notify when the app should switch between using real and fake location.
         */
        void onUseFakeLocationChanged(boolean fakeLocationEnabled);

        /**
         * Called to notify that the user wants to post a (fake) battery notification to the system.
         */
        void postBatteryNotification();
    }
}
