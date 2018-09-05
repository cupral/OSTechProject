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

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NearbyStation holds a reference to a station and a distance to it. It can be used to sort
 * Stations based on the distance.
 */
public class NearbyStation implements Comparable<NearbyStation> {
    public static final int UNKNOWN_DISTANCE = -1;
    private final ChargingStation mStation;
    private final int mDistanceMeters;

    /**
     * Creates a NearbyStation object for a station, and computes the distance to a given
     * location.
     *
     * @param station EV charging station
     * @param location LatLng coordinates where the distance is computed from
     */
    public NearbyStation(@NonNull ChargingStation station, @Nullable LatLng location) {
        mStation = station;
        if (location == null) {
            mDistanceMeters = UNKNOWN_DISTANCE;
        } else {
            float[] distanceOut = new float[1];
            LatLng stationLocation = station.getLocation();
            Location.distanceBetween(location.latitude, location.longitude,
                    stationLocation.latitude, stationLocation.longitude, distanceOut);
            mDistanceMeters = Math.round(distanceOut[0]);
        }
    }

    /**
     * Creates a NearbyStation object for given station and distance in meters.
     */
    public NearbyStation(@NonNull ChargingStation station, int distanceMeters) {
        mStation = station;
        mDistanceMeters = distanceMeters;
    }

    public ChargingStation getStation() {
        return mStation;
    }

    /**
     * Returns true if the distance to this NearbyStation is known.
     */
    public boolean isDistanceKnown() {
        return mDistanceMeters != UNKNOWN_DISTANCE;
    }

    /**
     * Returns the distance to this NearbyStation in meters. The returned value will be
     * UNKNOWN_DISTANCE, if the distance is not known.
     */
    public int getDistanceMeters() {
        return mDistanceMeters;
    }

    /**
     * Returns the distance as kilometers.
     */
    public float getDistanceKm() {
        return mDistanceMeters / 1000f;
    }

    /**
     * Returns the distance as miles.
     */
    public float getDistanceMiles() {
        return mDistanceMeters / 1609.344f;
    }

    /**
     * Compares two NearbyStation objects by distance (smaller distance first, unknown distances
     * last).
     */
    @Override
    public int compareTo(@NonNull NearbyStation other) {
        // Sort unknown distances always last
        if (!this.isDistanceKnown()) {
            return other.isDistanceKnown() ? 1 : 0;
        } else if (!other.isDistanceKnown()) {
            return -1;
        }
        return this.mDistanceMeters - other.mDistanceMeters;
    }


    /**
     * Converts a List of Stations to a List of NearbyStations sorted by distance to a given point.
     *
     * @param stations List of charging stations to convert and sort
     * @param location location to which distance is measured
     * @return List of NearbyStations sorted by distance to location (closest first)
     */
    public static List<NearbyStation> sortStationsByDistance(
            @NonNull List<ChargingStation> stations, @NonNull LatLng location) {
        List<NearbyStation> results = new ArrayList<>();
        for (ChargingStation station : stations) {
            results.add(new NearbyStation(station, location));
        }
        Collections.sort(results);
        return results;
    }
}
