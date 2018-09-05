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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.DayNightStyle;
import com.google.android.apps.auto.sdk.DrawerController;
import com.google.android.apps.auto.sdk.SearchController;
import com.google.android.apps.auto.sdk.StatusBarController;
import com.google.android.apps.auto.sdk.notification.CarNotificationExtender;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStationManager;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.location.FakeLocationProvider;
import com.google.android.apps.auto.sdk.samples.chargingstations.location.RealUserLocationProvider;
import com.google.android.apps.auto.sdk.samples.chargingstations.location.UserLocationProvider;
import com.google.android.apps.auto.sdk.samples.chargingstations.menu.ChargingStationMenuAdapter;
import com.google.android.apps.auto.sdk.samples.chargingstations.permissions.PermissionActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import java.util.Collection;
import java.util.List;

/**
 * Android Auto sample CarActivity for finding EV charging stations closest to user.
 *
 * <p>
 * The example app has been created to demonstrate the use of Android Auto SDK only. Use at your
 * own risk.
 */
public class ChargingStationMapActivity extends CarActivity
        implements ChargingStationManager.ResultsCallback,
        ChargingStationManager.DbReadyCallback,
        StationDetailsFragment.ChargingStationListener,
        ChargingStationMenuAdapter.MenuCallbacks {
    // Use class name as a key in Bundle to save activity state
    private static final String KEY_SAVED_STATE = ChargingStationMapActivity.class.getName();
    // Key added to battery notification intents to detect them in onNewIntent() method
    private static final String KEY_BATTERY_NOTIFICATION_INTENT = "batteryNotification";
    // Arbitrary ID of the example battery notification (unique within app)
    private static final int BATTERY_NOTIFICATION_ID = 1;
    // Arbitrary ID of the location permission notification (unique within app)
    private static final int LOCATION_PERMISSION_NOTIFICATION_ID = 2;
    // Keys used in the bundle storing the activity state
    private static final String KEY_BATTERY_STATUS_VISIBLE = "battery.status.visible";
    private static final String KEY_MAP_CAMERA = "map.camera";
    private static final String KEY_USER_LOCATION = "user.location";
    private static final String KEY_SELECTED_STATION_ID = "station.details.id";

    private MapView mMapView;
    private GoogleMapManager mGoogleMapManager;
    private UserLocationProvider mUserLocationProvider;
    private ChargingStationManager mChargingStationManager;
    private ChargingStationMenuAdapter mChargingStationMenuAdapter;
    private RecentStationsManager mRecentStationsManager;
    private TextSearchManager mTextSearchManager;
    private boolean mIsBatteryStatusPanelVisible = true;
    // Flag to prevent triggering second search while previous one has not completed
    private boolean mIsSearchInProgress = false;
    // ID of the station displayed in station details card, or null if no card is being displayed
    private Long mCurrentStationId = null;
    // State of app being restored when activity is restarted. Null when state is fully restored.
    private Bundle mSavedState;
    // StationSelectionListener for GoogleMap (charging station markers selected)
    private StationSelectionListener mMapSelectionListener = new StationSelectionListener() {
        @Override
        public void onStationSelected(@Nullable NearbyStation station) {
            mapStationSelected(station);
        }
    };
    // StationSelectionListener for text searches (by name or address)
    private StationSelectionListener mTextSearchSelectionListener = new StationSelectionListener() {
        @Override
        public void onStationSelected(@Nullable NearbyStation station) {
            textSearchStationSelected(station);
        }
    };
    // Receiver for detecting when location permission has been granted to this app
    private BroadcastReceiver mLocationPermissionGrantedDetector = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Stop immediately if we receive unexpected intent or if we have no location permission
            if (!PermissionActivity.LOCATION_PERMISSION_ACTION.equals(intent.getAction())
                    || !mUserLocationProvider.isUserLocationPermissionGranted()) {
                return;
            }
            mUserLocationProvider.startLocationUpdates();
            if (mGoogleMapManager != null) {
                mGoogleMapManager.enableMyLocation();
            }
            // Location permission was granted. Search for charging stations only if the location
            // permission panel is visible (not if station details are being displayed)
            View view = findViewById(R.id.location_permission_missing_note);
            if (view.getVisibility() == View.VISIBLE) {
                showLocationPermissionMissingPanel(false);
                searchForChargingStations(false /* forceSearch */);
            }
            // Stop receiving any further broadcasts
            removeLocationPermissionReceiver();
        }
    };

    // Callback method implementing ChargingStationManager.DbReadyCallback
    @Override
    public void onDbReady() {
        // Initialize map content (this is no op if map is not ready)
        showInitialMapContent();
        mTextSearchManager.onDbReady();
    }

    // Callback method implementing ChargingStationManager.ResultsCallback
    @Override
    public void closestStations(@Nullable List<NearbyStation> stations) {
        if (stations != null) {
            // Zoom map view to include user and the search results
            mGoogleMapManager.zoomToStations(true /* includeUser */, stations);
        }
        mIsSearchInProgress = false;
    }

    // Callback method implementing StationDetailsFragment.ChargingStationListener
    @Override
    public void navigateTo(ChargingStation chargingStation) {
        // Add the selected station to list of recently used stations
        mRecentStationsManager.addRecentStation(chargingStation);

        // NOTE: when navigation is launched, it will always start from the real location of the
        // device, even if fake location used by this example app
        String navigationUriString =
                "google.navigation:q=" + Uri.encode(chargingStation.getFullAddress());
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUriString));
        startCarActivity(navigationIntent);
    }

    // Callback method implementing StationDetailsFragment.ChargingStationListener
    @Override
    public void dial(ChargingStation chargingStation) {
        // Avoid accidental calls to real charging stations by using a fake unused number
        String phoneNumber = "+1-408-555-0101";
        // Note: ACTION_DIAL shows dialer with number, user has to press a button to call.
        //       ACTION_CALL would start the call to the specified number (requires permission)
        Intent dialingIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startCarActivity(dialingIntent);
    }

    // Callback method implementing StationDetailsFragment.ChargingStationListener
    @Override
    public void closeDetailsCard() {
        // Deselecting a marker will trigger a callback that hides the details card
        mGoogleMapManager.deselectMarker();
    }

    // Callback method implementing ChargingStationMenuAdapter.MenuCallbacks
    @Nullable
    @Override
    public LatLng getUserLocation() {
        return mUserLocationProvider != null ? mUserLocationProvider.getUserLocation() : null;
    }

    // Callback method implementing ChargingStationMenuAdapter.MenuCallbacks
    @Override
    public void onRecentChargingStationSelected(NearbyStation nearbyStation) {
        if (mIsBatteryStatusPanelVisible) {
            updateBatteryPanelVisibility(false);
        }
        // Select the recent station on the map (triggers display of station details card)
        mGoogleMapManager.selectStationMarkerFor(nearbyStation.getStation());
        // Center the station in map view, and zoom to comfortable level
        mGoogleMapManager.zoomToStation(nearbyStation.getStation());
    }

    // Callback method implementing ChargingStationMenuAdapter.MenuCallbacks
    @Override
    public void onUseFakeLocationChanged(boolean fakeLocationEnabled) {
        // Stop location updates to previous provider
        mUserLocationProvider.stopLocationUpdates();
        mUserLocationProvider.disconnect();
        // Create a new location provider and request location updates
        mUserLocationProvider = fakeLocationEnabled
                ? new FakeLocationProvider(this)
                : new RealUserLocationProvider(this);
        mUserLocationProvider.connect();
        mUserLocationProvider.startLocationUpdates();
        // Pass the new location provider to classes that depend on it
        if (mTextSearchManager != null) {
            mTextSearchManager.setUserLocationProvider(mUserLocationProvider);
        }
        if (mGoogleMapManager != null) {
            mGoogleMapManager.setUserLocationProvider(mUserLocationProvider);
            searchForChargingStations(true /* forceSearch */);
        }
        // Close the drawer
        getCarUiController().getDrawerController().closeDrawer();
    }

    // Callback method implementing ChargingStationMenuAdapter.MenuCallbacks
    @Override
    public void postBatteryNotification() {
        // Create intent to be launched when notification is tapped (must use CarActivityService
        // intent, not CarActivity itself)
        Intent intent = new Intent(this, ChargingStationMapService.class);
        intent.putExtra(KEY_BATTERY_NOTIFICATION_INTENT, true);

        // Generate thumbnail bitmap for notification
        int thumbnailSize = getResources().getDimensionPixelSize(R.dimen.thumbnail_size);
        int margin = getResources().getDimensionPixelSize(R.dimen.thumbnail_margin);
        Bitmap thumbnail =
                Bitmap.createBitmap(thumbnailSize, thumbnailSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumbnail);
        VectorDrawable vectorBattery =
                (VectorDrawable) getDrawable(R.drawable.ic_battery_notification);
        vectorBattery.setBounds(margin, margin, thumbnailSize - margin, thumbnailSize - margin);
        vectorBattery.draw(canvas);

        CarNotificationExtender extender = new CarNotificationExtender.Builder()
                .setTitle(getResources().getString(R.string.battery_notification_title, 30))
                .setSubtitle(getResources().getString(R.string.app_name))
                .setThumbnail(thumbnail)
                .setActionIntent(intent)
                .setActionIconResId(R.drawable.ic_my_location)
                .setShouldShowAsHeadsUp(true)
                .build();
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pin_black)
                .setContentText(getResources().getString(R.string.battery_notification_content))
                .extend(extender)
                .build();
        NotificationManagerCompat.from(this).notify(BATTERY_NOTIFICATION_ID, notification);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // If bundle contains saved activity state, save it to be processed as activity
        // initialization progresses
        mSavedState = bundle == null ? null : bundle.getBundle(KEY_SAVED_STATE);

        View view =
                getLayoutInflater().inflate(R.layout.activity_charging_station_map, null, false);
        setContentView(view);

        StatusBarController statusBarController = getCarUiController().getStatusBarController();
        statusBarController.setTitle(getResources().getString(R.string.app_name));
        statusBarController.setDayNightStyle(DayNightStyle.FORCE_NIGHT);

        mChargingStationManager = new ChargingStationManager(this, this);
        mRecentStationsManager = new RecentStationsManager(this, mChargingStationManager);

        mChargingStationMenuAdapter =
                new ChargingStationMenuAdapter(this, mSavedState, mRecentStationsManager, this);
        getCarUiController().getMenuController().setRootMenuAdapter(mChargingStationMenuAdapter);
        getCarUiController().getMenuController().showMenuButton();

        if (mChargingStationMenuAdapter.isFakeLocationChecked()) {
            mUserLocationProvider = new FakeLocationProvider(this);
        } else {
            mUserLocationProvider = new RealUserLocationProvider(this);
        }

        View findStationsButton = findViewById(R.id.find_stations_button);
        findStationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Center map view on user's location with some stations visible
                searchForChargingStations(false /* forceSearch */);
                // Make map visible (it is invisible at startup)
                mMapView.setVisibility(View.VISIBLE);
                // Animate battery status panel out
                updateBatteryPanelVisibility(false);
            }
        });

        View showBatteryStatus = findViewById(R.id.battery_status_fab);
        showBatteryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Animate battery status panel in
                updateBatteryPanelVisibility(true);
            }
        });

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(bundle);
        MapsInitializer.initialize(this);

        initializeTextSearchManager();
        initializeDrawer();

        if (mSavedState != null) {
            boolean batteryPanelVisible = mSavedState.getBoolean(KEY_BATTERY_STATUS_VISIBLE, true);
            // If battery status panel should not be visible, hide it without animations
            if (!batteryPanelVisible) {
                mMapView.setVisibility(View.VISIBLE);
                View batteryStatusPanel = findViewById(R.id.battery_status_panel);
                batteryStatusPanel.setVisibility(View.GONE);
                mIsBatteryStatusPanelVisible = false;
                mTextSearchManager.setBatteryStatusPanelShowing(false);
                getCarUiController().getStatusBarController().setDayNightStyle(DayNightStyle.AUTO);
            }
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Disable "My Location" button provided by the maps API
                UiSettings uiSettings = googleMap.getUiSettings();
                uiSettings.setMyLocationButtonEnabled(false);

                mGoogleMapManager =
                        new GoogleMapManager(ChargingStationMapActivity.this, googleMap);
                mGoogleMapManager.setMapControlButtons(
                        findViewById(R.id.zoom_in_button),
                        findViewById(R.id.zoom_out_button),
                        findViewById(R.id.my_location_button));
                mGoogleMapManager.setStationSelectionListener(mMapSelectionListener);
                mGoogleMapManager.adjustMapPadding(getResources(), false /* cardIsShowing */);
                mGoogleMapManager.setUserLocationProvider(mUserLocationProvider);
                mGoogleMapManager.enableMyLocation();

                // Initialize visible map area
                if (mSavedState == null) {
                    // Center the map on user's current location
                    LatLng userLocation = mUserLocationProvider.getUserLocation();
                    CameraUpdate cameraUpdate;
                    if (userLocation != null) {
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 12);
                    } else {
                        // User location unknown, show USA map
                        LatLng usaCenter = new LatLng(39.8, -98.6);
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(usaCenter, 2);
                    }
                    googleMap.moveCamera(cameraUpdate);
                } else {
                    // Saved state exists, restore map view as it was
                    if (mSavedState.containsKey(KEY_SELECTED_STATION_ID)) {
                        // Selection exists so station details card is visible, adjust map
                        mGoogleMapManager.adjustMapPadding(getResources(), true);
                    }
                    CameraPosition camera = mSavedState.getParcelable(KEY_MAP_CAMERA);
                    mGoogleMapManager.setCameraPosition(camera);
                }

                showInitialMapContent();
                mTextSearchManager.onGoogleMapReady();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mUserLocationProvider.connect();
        if (findViewById(R.id.battery_status_panel).getVisibility() == View.VISIBLE) {
            getCarUiController().getSearchController().hideSearchBox();
        } else {
            getCarUiController().getSearchController().showSearchBox();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mUserLocationProvider.isUserLocationPermissionGranted()) {
            postLocationPermissionNotification();
            if (mCurrentStationId == null) {
                showLocationPermissionMissingPanel(true);
            }
        } else {
            // Make sure "permission missing" panel is not visible
            showLocationPermissionMissingPanel(false);
            mUserLocationProvider.startLocationUpdates();
        }
        mMapView.onResume();
        mRecentStationsManager.load();
    }

    @Override
    public void onPause() {
        mRecentStationsManager.save();
        mMapView.onPause();
        mUserLocationProvider.stopLocationUpdates();
        removeLocationPermissionReceiver();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onStop() {
        mUserLocationProvider.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(KEY_BATTERY_NOTIFICATION_INTENT)) {
            clearBatteryNotification();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle savedState = new Bundle();
        // Save whether battery status panel is visible or not
        savedState.putBoolean(KEY_BATTERY_STATUS_VISIBLE, mIsBatteryStatusPanelVisible);
        // Save visible map area, if map has been initialized
        if (mGoogleMapManager != null) {
            savedState.putParcelable(KEY_MAP_CAMERA, mGoogleMapManager.getCameraPosition());
        }
        // Save user location, if known
        LatLng userLocation = mUserLocationProvider.getUserLocation();
        if (userLocation != null) {
            savedState.putParcelable(KEY_USER_LOCATION, userLocation);
        }
        // Save the menu state
        mChargingStationMenuAdapter.saveMenuState(savedState);
        // Save state of markers: selection and whether only single one is being shown
        if (mCurrentStationId != null) {
            savedState.putLong(KEY_SELECTED_STATION_ID, mCurrentStationId);
        }

        outState.putBundle(KEY_SAVED_STATE, savedState);
    }

    /**
     * Adds initial map content. For a newly launched activity, shows nearest charging stations.
     * When restoring activity state (for example, day/night mode change), restores the contents
     * that were on screen before activity restarted.
     */
    private void showInitialMapContent() {
        // Initial map content cannot be displayed until both map and charging station managers
        // have been initialized
        if (mGoogleMapManager == null || !mChargingStationManager.isDbReady()) {
            return;
        }
        // Show markers for all charging stations on the map at once. If the database contained
        // thousands of charging stations, the app should show only the stations within the area
        // shown inside map view. Also, if fetching the data would be expected to take a
        // non-trivial amount of time, this should be done in a background thread.
        Collection<ChargingStation> allStations = mChargingStationManager.getAllChargingStations();
        mGoogleMapManager.addStationMarkers(allStations);

        if (mSavedState == null) {
            // No saved state to restore, zoom map initially to show user and nearest charging
            // stations
            searchForChargingStations(false /* forceSearch */);
        } else {
            // Restoring saved state, do not modify map view anymore (it's already restored)
            // Just restore display of station details if they were being displayed
            ChargingStation chargingStation = getSavedChargingStation();
            if (chargingStation != null) {
                mGoogleMapManager.selectStationMarkerFor(chargingStation);
            }
            // Saved state has been fully restored, clear the reference to saved state bundle
            mSavedState = null;
        }
    }

    /**
     * Returns a NearbyStation from saved state, or null if no saved state exists or if saved
     * state contains no selected station information.
     */
    private @Nullable ChargingStation getSavedChargingStation() {
        if (mSavedState == null || !mSavedState.containsKey(KEY_SELECTED_STATION_ID)) {
            // Either no saved state exists, or it contains no selection
            return null;
        }
        // Get the charging station object from ChargingStationManager
        mCurrentStationId = mSavedState.getLong(KEY_SELECTED_STATION_ID);
        return mChargingStationManager.findStationById(mCurrentStationId);
    }

    /**
     * Changes the visibility of the panel showing notification to user about missing location
     * permission.
     */
    private void showLocationPermissionMissingPanel(boolean makeVisible) {
        if (mGoogleMapManager != null) {
            mGoogleMapManager.adjustMapPadding(getResources(), mCurrentStationId != null);
        }
        // Return if the permission message panel is already in requested state
        View permissionMessage = findViewById(R.id.location_permission_missing_note);
        if ((permissionMessage.getVisibility() == View.VISIBLE) == makeVisible) {
            return;
        }
        View myLocation = findViewById(R.id.my_location_button);
        View zoomOut = findViewById(R.id.zoom_out_button);
        RelativeLayout.LayoutParams zoomOutParams =
                (RelativeLayout.LayoutParams) zoomOut.getLayoutParams();
        View fabBacking = findViewById(R.id.battery_status_fab_backing);
        RelativeLayout.LayoutParams fabParams =
                (RelativeLayout.LayoutParams) fabBacking.getLayoutParams();
        if (makeVisible) {
            // Hide "my location" button
            myLocation.setVisibility(View.GONE);
            // Attach zoom out button to permission message panel
            zoomOutParams.removeRule(RelativeLayout.ABOVE);
            zoomOutParams.addRule(RelativeLayout.ABOVE, R.id.location_permission_missing_note);
            zoomOut.setLayoutParams(zoomOutParams);
            // Attach fab to missing permission message panel
            fabParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            fabParams.addRule(RelativeLayout.ABOVE, R.id.location_permission_missing_note);
            fabBacking.setLayoutParams(fabParams);
            // Show permission message panel
            permissionMessage.setVisibility(View.VISIBLE);
        } else {
            // Hide permission message panel
            permissionMessage.setVisibility(View.GONE);
            // Attach fab back to the bottom of its parent
            fabParams.removeRule(RelativeLayout.ABOVE);
            fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            fabBacking.setLayoutParams(fabParams);
            // Attach zoom out button to my location button
            zoomOutParams.removeRule(RelativeLayout.ABOVE);
            zoomOutParams.addRule(RelativeLayout.ABOVE, R.id.my_location_button);
            zoomOut.setLayoutParams(zoomOutParams);
            // Show my location button
            myLocation.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Posts a standard phone notification that opens an activity that requests location access
     * permission from the user.
     */
    private void postLocationPermissionNotification() {
        Intent intent = new Intent(this, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                LOCATION_PERMISSION_NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pin_black)
                .setContentTitle(
                        getResources().getString(R.string.location_permission_notification_title))
                .setContentText(
                        getResources().getString(R.string.location_permission_notification_short))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        getResources().getString(R.string.location_permission_notification_long)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat.from(this)
                .notify(LOCATION_PERMISSION_NOTIFICATION_ID, notification);
        // Get notified when user grants the location permission
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mLocationPermissionGrantedDetector,
                new IntentFilter(PermissionActivity.LOCATION_PERMISSION_ACTION));
    }

    /**
     * Stops listening to local broadcasts for granting of location permission.
     */
    private void removeLocationPermissionReceiver() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mLocationPermissionGrantedDetector);
    }

    /**
     * Prepares a class to handle interactions with the text search bar in the UI.
     */
    private void initializeTextSearchManager() {
        SearchController searchController = getCarUiController().getSearchController();
        mTextSearchManager = new TextSearchManager(this, searchController, mChargingStationManager);
        mTextSearchManager.setUserLocationProvider(mUserLocationProvider);
        mTextSearchManager.setStationSelectionListener(mTextSearchSelectionListener);
        searchController.setSearchCallback(mTextSearchManager);
        mTextSearchManager.setBatteryStatusPanelShowing(true);
    }

    /**
     * Initialize drawer to use a brand specific scrim color.
     */
    private void initializeDrawer() {
        DrawerController drawerController = getCarUiController().getDrawerController();
        int scrimColorArgb = ContextCompat.getColor(this, R.color.drawer_scrim);
        drawerController.setScrimColor(scrimColorArgb);
    }

    /**
     * Launches an asynchronous search for nearby charging stations.
     *
     * @param forceSearch if true forces a new search to be started even if one is already in
     * progress, otherwise a new search is started only if no search is in progress
     */
    private synchronized void searchForChargingStations(boolean forceSearch) {
        // Do nothing if the map manager or the database is not ready, or if search is already in
        // progress
        if (mGoogleMapManager == null || !mChargingStationManager.isDbReady()
                || (mIsSearchInProgress && !forceSearch)) {
            return;
        }
        UserLocationProvider.LocationCallback callback =
                new UserLocationProvider.LocationCallback() {
                    @Override
                    public void locationReceived(LatLng location) {
                        if (location != null) {
                            mChargingStationManager.findClosestStations(
                                    location, 5, ChargingStationMapActivity.this);
                        }
                    }
                };
        mIsSearchInProgress = mUserLocationProvider.getUserLocationAsync(callback);
    }

    /**
     * Handles station (marker) selection on map by displaying station details. If the selection
     * was cleared, hides the station details.
     *
     * @param nearbyStation the selected charging station
     */
    private void mapStationSelected(@Nullable NearbyStation nearbyStation) {
        if (nearbyStation != null) {
            displayStationDetails(nearbyStation);
        } else {
            // No station is selected, close station details
            hideStationDetails();
        }
    }

    /**
     * Handles station selection based on text search.
     */
    private void textSearchStationSelected(NearbyStation nearbyStation) {
        // Select the station on the map (triggers display of station details card)
        mGoogleMapManager.selectStationMarkerFor(nearbyStation.getStation());
        // Center the station in map view, and zoom to comfortable level
        mGoogleMapManager.zoomToStation(nearbyStation.getStation());
    }

    /**
     * Displays an information card for a charging station.
     */
    private void displayStationDetails(@NonNull NearbyStation nearbyStation) {
        mCurrentStationId = nearbyStation.getStation().id;
        // Hide "no location permission" notification card if visible
        showLocationPermissionMissingPanel(false);
        // Prepare station details fragment
        StationDetailsFragment stationDetailsFragment = new StationDetailsFragment();
        stationDetailsFragment.setNearbyStation(nearbyStation);
        stationDetailsFragment.setChargingStationListener(this);

        // Add station details fragment to screen
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.station_details_card_area, stationDetailsFragment, "stationDetails")
                .commit();

        // Adjust the visible map area
        mGoogleMapManager.adjustMapPadding(getResources(), true /* cardIsShowing */);
    }

    /**
     * Hides the station information card.
     */
    private void hideStationDetails() {
        mCurrentStationId = null;
        Fragment stationDetailsFragment =
                getSupportFragmentManager().findFragmentByTag("stationDetails");
        if (stationDetailsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(stationDetailsFragment)
                    .commit();
        }
        mGoogleMapManager.adjustMapPadding(getResources(), false /* cardIsShowing */);

        // Show "no location permission" notification card again if necessary
        if (!mUserLocationProvider.isUserLocationPermissionGranted()) {
            postLocationPermissionNotification();
            showLocationPermissionMissingPanel(true);
        }
    }

    private void clearBatteryNotification() {
        // Removes the posted low battery notification
        NotificationManagerCompat.from(this).cancel(BATTERY_NOTIFICATION_ID);
    }

    /**
     * Shows or hides battery status panel.
     */
    private void updateBatteryPanelVisibility(boolean visible) {
        mIsBatteryStatusPanelVisible = visible;
        BatteryPanelAnimation animation = new BatteryPanelAnimation(this, mTextSearchManager);
        if (visible) {
            animation.showBatteryPanel();
        } else {
            animation.hideBatteryPanel();
        }
    }
}
