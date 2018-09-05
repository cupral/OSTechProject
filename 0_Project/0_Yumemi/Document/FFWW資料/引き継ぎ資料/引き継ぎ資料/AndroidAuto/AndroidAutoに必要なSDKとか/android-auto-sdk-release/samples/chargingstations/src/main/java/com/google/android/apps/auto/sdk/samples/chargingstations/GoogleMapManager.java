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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;
import android.view.View;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.location.UserLocationProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * GoogleMapManager implements the logic of displaying markers on a GoogleMap, and when a user
 * selects a marker, highlighting the marker and notifying a listener about the selection.
 */
public class GoogleMapManager implements GoogleMap.OnMarkerClickListener {
    private static final String TAG = "GoogleMapManager";

    private final GoogleMap mGoogleMap;
    private View mZoomInButton;
    private View mZoomOutButton;
    private View mMyLocationButton;
    private LongSparseArray<Marker> mStationMarkersById;
    private Marker mSelectedMarker;
    private BitmapDescriptor mMapPinNormal;
    private BitmapDescriptor mMapPinSelected;
    private boolean mIsUserLocationEnabled;
    private UserLocationProvider mUserLocationProvider;
    private Marker mUserLocationDot;
    private StationSelectionListener mStationSelectionListener;
    private int mMarkerSizePx;

    private UserLocationProvider.LocationCallback mFirstLocationApplier =
            new UserLocationProvider.LocationCallback() {
                @Override
                public void locationReceived(LatLng location) {
                    mUserLocationDot.setPosition(location);
                    mUserLocationDot.setVisible(true);
                }
            };
    private UserLocationProvider.LocationCallback mUserLocationUpdater =
            new UserLocationProvider.LocationCallback() {
                @Override
                public void locationReceived(LatLng location) {
                    mUserLocationDot.setPosition(location);
                }
            };

    /**
     * Creates a new instance for managing a given GoogleMap. Replaces the current
     * OnMarkerClickListener attached to the map.
     *
     * @param context Context for the GoogleMap
     * @param googleMap GoogleMap created by using getMapAsync() call
     */
    public GoogleMapManager(@NonNull Context context, @NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        initializeMapPinIcon(context.getResources());
        initializeUserLocationDotMarker(context.getResources());
        mIsUserLocationEnabled = false;
        mStationMarkersById = new LongSparseArray<>();
        // Initialize map in day or night mode to match configuration
        Configuration config = context.getResources().getConfiguration();
        setNightMode(context, isNightMode(config));
    }

    /**
     * Activates map control buttons for zooming and re-centering the map on the user's location.
     */
    public void setMapControlButtons(View zoomInButton, View zoomOutButton, View myLocationButton) {
        mZoomInButton = zoomInButton;
        mZoomOutButton = zoomOutButton;
        mMyLocationButton = myLocationButton;

        mZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.zoomBy(1);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        });
        mZoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.zoomBy(-1);
                mGoogleMap.animateCamera(cameraUpdate);
            }
        });
        mMyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng userLocation = mUserLocationProvider.getUserLocation();
                if (userLocation != null) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(userLocation));
                }
            }
        });
    }

    /**
     * Sets the UserLocationProvider used for finding out user's location and the LocationSource
     * for GoogleMap.
     */
    public void setUserLocationProvider(UserLocationProvider userLocationProvider) {
        if (mUserLocationProvider != null) {
            // Unsubscribe for location updates from previous provider
            mUserLocationProvider.removeUserLocationSubscriber(mUserLocationUpdater);
        }
        mUserLocationProvider = userLocationProvider;
        if (mUserLocationProvider != null) {
            // Display user location as soon as possible, and subscribe to future location updates
            mUserLocationProvider.getUserLocationAsync(mFirstLocationApplier);
            mUserLocationProvider.addUserLocationSubscriber(mUserLocationUpdater);
        }
    }

    /**
     * Sets the listener that is going to be notified when user selects charging station markers.
     */
    public void setStationSelectionListener(StationSelectionListener listener) {
        mStationSelectionListener = listener;
    }

    /**
     * Returns CameraPosition describing current view in the map.
     */
    public CameraPosition getCameraPosition() {
        return mGoogleMap.getCameraPosition();
    }

    /**
     * Sets the current view in the map to match given CameraPosition.
     */
    public void setCameraPosition(CameraPosition cameraPosition) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Enables user location dot on the map, if app has permission to access user's fine location.
     *
     * @return true if the user location dot was successfully enabled
     */
    public boolean enableMyLocation() {
        if (mIsUserLocationEnabled) {
            return true;
        }
        // Make sure we have permission to access user location
        if (mUserLocationProvider == null
                || !mUserLocationProvider.isUserLocationPermissionGranted()) {
            return false;
        }

        mIsUserLocationEnabled = true;
        if (!mUserLocationDot.isVisible()) {
            mUserLocationProvider.getUserLocationAsync(mFirstLocationApplier);
            mUserLocationProvider.addUserLocationSubscriber(mUserLocationUpdater);
        }
        if (mMyLocationButton != null && mMyLocationButton.getVisibility() != View.VISIBLE) {
            mMyLocationButton.setVisibility(View.VISIBLE);
        }
        return true;
    }

    /**
     * Switches between day and night mode rendering for the map.
     *
     * @param context Context of the map activity
     * @param nightMode flag to indicate whether night mode should be active
     */
    public void setNightMode(Context context, boolean nightMode) {
        MapStyleOptions mapStyleOptions;
        if (nightMode) {
            // Load night mode definition from resources
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.nightmode);
        } else {
            // Null MapStyleOptions restores default map style (day mode)
            mapStyleOptions = null;
        }
        mGoogleMap.setMapStyle(mapStyleOptions);
    }

    /**
     * Adjusts map padding based on whether an information card is being displayed
     *
     * @param resources Resources object for accessing padding constants
     * @param cardIsShowing flag indicating whether an information card is being displayed
     */
    public void adjustMapPadding(@NonNull Resources resources, boolean cardIsShowing) {
        int left = resources.getDimensionPixelOffset(
                cardIsShowing ? R.dimen.map_card_side_padding : R.dimen.map_side_padding_left);
        int top = resources.getDimensionPixelOffset(R.dimen.map_top_padding);
        int right = 0;
        int bottom = cardIsShowing || mIsUserLocationEnabled ? 0
                : resources.getDimensionPixelSize(R.dimen.missing_permission_height);
        mGoogleMap.setPadding(left, top, right, bottom);

        // Hide map controls when a station details card is displayed so that they don't "peek out"
        // from underneath
        int controlVisibility = cardIsShowing ? View.GONE : View.VISIBLE;
        mZoomInButton.setVisibility(controlVisibility);
        mZoomOutButton.setVisibility(controlVisibility);
        if (!mIsUserLocationEnabled) {
            // App has no permission to access user location, don't show "my location" button
            mMyLocationButton.setVisibility(View.GONE);
        } else {
            // Location permission has been granted, match visibility of zoom buttons
            mMyLocationButton.setVisibility(controlVisibility);
        }
    }

    /**
     * Adds markers for list of charging stations to the map. Does not remove any existing markers
     * on the map.
     *
     * @param stations Non-null list of charging stations
     */
    public void addStationMarkers(@NonNull Collection<ChargingStation> stations) {
        for (ChargingStation chargingStation : stations) {
            addStationMarker(chargingStation);
        }
    }

    /**
     * Adds a marker for a single charging station. Does not remove any existing markers on the
     * map.
     */
    public void addStationMarker(@NonNull ChargingStation chargingStation) {
        MarkerOptions options = new MarkerOptions()
                .icon(mMapPinNormal)
                .anchor(0.5f, 1.0f)
                .draggable(false)
                .flat(true);
        options.position(chargingStation.getLocation());
        Marker stationMarker = mGoogleMap.addMarker(options);
        stationMarker.setTag(chargingStation);
        mStationMarkersById.put(chargingStation.id, stationMarker);
    }

    /**
     * Makes the marker for a specific station selected. Any selection listeners will be notified
     * about the change in the selected station. If no existing marker matches the station, the
     * selection will not be changed.
     *
     * @param station charging station that should be selected
     * @return true, if a marker for the station was found and selected
     */
    public boolean selectStationMarkerFor(@NonNull ChargingStation station) {
        Marker marker = mStationMarkersById.get(station.id);
        if (marker != null) {
            selectMarker(marker);
            return true;
        }
        return false;
    }

    /**
     * Animates the viewport of the map to include a collection of charging stations. Optionally
     * includes user's location within the viewport. If the collection does not contain any
     * stations, does not do anything.
     */
    public void zoomToStations(boolean includeUser, @NonNull List<NearbyStation> nearbyStations) {
        if (nearbyStations.isEmpty()) {
            return;
        }
        // Compute minimum bounds that fit all the markers (and optionally user as well)
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (NearbyStation nearbyStation : nearbyStations) {
            boundsBuilder.include(nearbyStation.getStation().getLocation());
        }
        boolean isSinglePoint = nearbyStations.size() == 1;
        if (includeUser) {
            // TODO(markot): if the closest station is over 1000km (~600mi) away, do not include
            // the user location in bounds (not useful anymore)
            LatLng userLocation = mUserLocationProvider.getUserLocation();
            if (userLocation != null) {
                boundsBuilder.include(userLocation);
                isSinglePoint = false;
            }
        }
        if (isSinglePoint) {
            // User location was not available or not included, list contained only one marker
            // Zoom to the single station
            zoomToStation(nearbyStations.get(0).getStation());
        } else {
            // Animate camera to computed bounds (add padding to keep markers off the edges)
            mGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), mMarkerSizePx));
        }
    }

    /**
     * Animates the viewport of the map to center the given station zoomed at "comfortable" level
     * to provide context of the station location.
     */
    public void zoomToStation(@NonNull ChargingStation station) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(station.getLocation(), 14));
    }

    /**
     * Adds selection highlight to a marker. If another marker is already selected, deselects the
     * previously selected marker first.
     */
    private void selectMarker(@Nullable Marker marker) {
        // Return if the marker is already selected (or both are null)
        if (Objects.equals(mSelectedMarker, marker)) {
            return;
        }
        deselectMarker();
        if (marker != null) {
            mSelectedMarker = marker;
            mSelectedMarker.setIcon(mMapPinSelected);
            // Notify selection listener about the change in selection
            if (mStationSelectionListener != null) {
                ChargingStation chargingStation = (ChargingStation) marker.getTag();
                NearbyStation nearbyStation =
                        new NearbyStation(chargingStation, mUserLocationProvider.getUserLocation());
                mStationSelectionListener.onStationSelected(nearbyStation);
            }
        }
    }

    /**
     * Removes selection highlighting from the currently selected marker.
     */
    public void deselectMarker() {
        if (mSelectedMarker != null) {
            mSelectedMarker.setIcon(mMapPinNormal);
            mSelectedMarker = null;
            // Notify selection listener about the change in selection
            if (mStationSelectionListener != null) {
                mStationSelectionListener.onStationSelected(null);
            }
        }
    }

    // Callback method implementing GoogleMap.OnMarkerClickListener
    @Override
    public boolean onMarkerClick(Marker marker) {
        // Ignore repeated touches on selected marker
        if (marker.equals(mSelectedMarker)) {
            return true;
        }
        // Select the marker that was touched (first deselects any previous one)
        selectMarker(marker);
        // Center map view on the selected marker
        ChargingStation station = (ChargingStation) marker.getTag();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(station.getLocation()));
        return true;
    }

    /**
     * Checks whether a given Configuration specifies that night mode is active.
     *
     * @param config Configuration to be checked
     * @return true if night mode is active, false if day mode is active or if night mode state
     * is not defined in the configuration
     */
    private boolean isNightMode(Configuration config) {
        int nightModeValue = (config.uiMode & Configuration.UI_MODE_NIGHT_MASK);
        return nightModeValue == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Loads the vector resources used to mark charging stations on the map.
     */
    private void initializeMapPinIcon(Resources resources) {
        Drawable vectorPin = resources.getDrawable(R.drawable.ic_map_pin_normal, null);
        // Create a bitmap canvas to draw the vector icon on
        int width = resources.getDimensionPixelSize(R.dimen.map_marker_width);
        int height = resources.getDimensionPixelSize(R.dimen.map_marker_height);
        Bitmap bitmapPin = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Draw the vector icon to fill the bitmap
        Canvas bitmapCanvas = new Canvas(bitmapPin);
        vectorPin.setBounds(0, 0, width, height);
        vectorPin.draw(bitmapCanvas);
        // Convert the bitmap to a descriptor as needed by Maps API
        mMapPinNormal = BitmapDescriptorFactory.fromBitmap(bitmapPin);
        // Save icon size for use as padding to avoid markers too close to map edges
        mMarkerSizePx = Math.max(width, height);

        // Now load the icon used for selected marker
        vectorPin = resources.getDrawable(R.drawable.ic_map_pin_selected, null);
        Bitmap selectedPin = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(selectedPin);
        vectorPin.setBounds(0, 0, width, height);
        vectorPin.draw(bitmapCanvas);
        mMapPinSelected = BitmapDescriptorFactory.fromBitmap(selectedPin);
    }

    /**
     * Creates a user location dot map marker to be used to show user's location on the map. The
     * marker is created as invisible, and it should be made visible once user location is
     * applied to it.
     */
    private void initializeUserLocationDotMarker(Resources resources) {
        int dotSize = resources.getDimensionPixelSize(R.dimen.map_user_location_dot);
        Bitmap userDotBitmap = Bitmap.createBitmap(dotSize, dotSize, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(userDotBitmap);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        int dotColor = resources.getColor(R.color.user_location_dot);
        float dotRadius = resources.getDimension(R.dimen.map_user_location_dot_radius_inner);
        int edgeColor = resources.getColor(android.R.color.white);
        float edgeRadius = resources.getDimension(R.dimen.map_user_location_dot_radius_outer);

        paint.setColor(edgeColor);
        bitmapCanvas.drawCircle(dotSize / 2f, dotSize / 2f, edgeRadius, paint);
        paint.setColor(dotColor);
        bitmapCanvas.drawCircle(dotSize / 2f, dotSize / 2f, dotRadius, paint);

        // Create the marker, but keep it invisible until we have valid user location
        BitmapDescriptor userDot = BitmapDescriptorFactory.fromBitmap(userDotBitmap);
        mUserLocationDot = mGoogleMap.addMarker(new MarkerOptions()
                .icon(userDot)
                .zIndex(10)
                .anchor(0.5f, 0.5f)
                .position(new LatLng(0, 0))
                .flat(true)
                .draggable(false)
                .visible(false));
    }
}
