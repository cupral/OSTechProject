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

import android.content.Context;
import android.location.Location;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

/**
 * FakeLocationProvider provides only a single location inside Golden Gate Park to all location
 * listeners. Uses provider name "fake" in Location objects it creates. It will not provide user
 * locations unless the the app has been given permission to access (fine) user location.
 */
public class FakeLocationProvider extends UserLocationProvider implements LocationSource {

    private static final LatLng GOOGLE_HQ = new LatLng(37.420962, -122.082920);

    public FakeLocationProvider(Context context) {
        super(context);
    }

    // Implements UserLocationProvider
    @Override
    public LatLng getUserLocation() {
        return isUserLocationPermissionGranted() ? GOOGLE_HQ : null;
    }

    // Implements LocationSource
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (!isUserLocationPermissionGranted()) {
            return;
        }
        Location location = new Location("fake");
        location.setLatitude(GOOGLE_HQ.latitude);
        location.setLongitude(GOOGLE_HQ.longitude);
        onLocationChangedListener.onLocationChanged(location);
    }

    // Implements LocationSource
    @Override
    public void deactivate() {
        // Do nothing
    }
}
