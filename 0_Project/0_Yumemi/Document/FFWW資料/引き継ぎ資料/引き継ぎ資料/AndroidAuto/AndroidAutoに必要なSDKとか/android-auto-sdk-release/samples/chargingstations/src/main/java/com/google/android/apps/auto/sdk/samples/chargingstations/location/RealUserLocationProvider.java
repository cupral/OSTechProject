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

package com.google.android.apps.auto.sdk.samples.chargingstations.location;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import java.util.concurrent.TimeUnit;

/**
 * RealUserLocationProvider provides the actual user location.
 */
public class RealUserLocationProvider extends UserLocationProvider implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "UserLocationProvider";

    // Request location updates once a second
    private static final long LOCATION_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    // Minimum distance the user location needs to change before we are interested in updating it
    private static final float MINIMUM_LOCATION_CHANGE_METERS = 2f;

    private GoogleApiClient mApiClient;
    private LatLng mLatestLocation;

    /**
     * Constructs a new RealUserLocationProvider object.
     *
     * @param context Context for the RealUserLocationProvider
     */
    public RealUserLocationProvider(Context context) {
        super(context);
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    // Implements UserLocationProvider
    @Nullable
    @Override
    public LatLng getUserLocation() {
        return mLatestLocation;
    }

    // Implements UserLocationProvider
    @Override
    public void connect() {
        super.connect();
        // Start connecting api client
        mApiClient.connect();
    }

    // Implements UserLocationProvider
    @Override
    public void disconnect() {
        super.disconnect();
        mApiClient.disconnect();
    }

    // Implements UserLocationProvider
    @Override
    public void startLocationUpdates() {
        super.startLocationUpdates();
        // Do nothing if api client is not connected or if location access permission has
        // not been granted. If permission is granted but api client is not ready, location
        // request will be posted once api client is connected
        if (!mApiClient.isConnected() || !isUserLocationPermissionGranted()) {
            return;
        }
        // Start listening to location updates from FusedLocationApi
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(LOCATION_INTERVAL_MS)
                .setSmallestDisplacement(MINIMUM_LOCATION_CHANGE_METERS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, this);
    }

    // Implements UserLocationProvider
    @Override
    public void stopLocationUpdates() {
        super.stopLocationUpdates();
        if (mApiClient.isConnected()) {
            FusedLocationApi.removeLocationUpdates(mApiClient, this);
        }
    }

    // Implements GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        // If location updates were already requested before connection was ready, start them now
        if (isLocationUpdating()) {
            startLocationUpdates();
        }
    }

    // Implements GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "GoogleApiClient connection suspended");
    }

    // Implements GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "GoogleApiClient connection failed");
    }

    // Implements LocationListener
    @Override
    public void onLocationChanged(Location location) {
        // Save only the geographic coordinates to latest location
        mLatestLocation = new LatLng(location.getLatitude(), location.getLongitude());
        deliverLocation(mLatestLocation);
    }
}
