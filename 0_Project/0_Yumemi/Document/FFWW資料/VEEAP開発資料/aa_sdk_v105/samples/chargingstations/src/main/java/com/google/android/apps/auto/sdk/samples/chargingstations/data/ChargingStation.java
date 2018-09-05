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

import com.google.android.gms.maps.model.LatLng;

/**
 * ChargingStation defines all data fields of a single EV charging station.
 */
public class ChargingStation {
    private static final String TAB = "\t";

    public final long id;
    public final String name;
    public final String streetAddress;
    public final String city;
    public final String state;
    public final String zip;
    public final String phone;
    public final String hours;
    public final String network;
    public final String url;
    public final String connector;
    private final double mLatitude;
    private final double mLongitude;

    /**
     * Creates a ChargingStation object by parsing a TSV string. Exception is thrown if the string
     * doesn't have the correct number of fields, or if the latitude or longitude fields contain
     * invalid (non-numeric) values.
     *
     * @param id id of the charging station
     * @param tsvString TSV (Tab Separated Values) string containing full charging station
     * definition
     */
    public ChargingStation(long id, String tsvString) {
        this.id = id;
        String[] fields = tsvString.split(TAB);
        name = fields[0];
        streetAddress = fields[1];
        city = fields[2];
        state = fields[3];
        zip = fields[4];
        phone = fields[5];
        hours = fields[6];
        network = fields[7];
        url = fields[8];
        mLatitude = Double.parseDouble(fields[9]);
        mLongitude = Double.parseDouble(fields[10]);
        connector = fields.length > 11 ? fields[11] : "";
    }

    /**
     * Returns a String containing the full address (street, city, state, and zip)
     */
    public String getFullAddress() {
        return streetAddress + ", " + city + ", " + state + " " + zip;
    }

    /**
     * Returns the geographic coordinates of the ChargingStation as LatLng.
     */
    public LatLng getLocation() {
        return new LatLng(mLatitude, mLongitude);
    }

    /**
     * Two ChargingStations are considered equal if their TSV presentation Strings are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChargingStation)) {
            return false;
        }
        ChargingStation other = (ChargingStation) obj;
        return this.toTsv().equals(other.toTsv());
    }

    // Keep hashCode() in sync with equals().
    @Override
    public int hashCode() {
        return this.toTsv().hashCode();
    }

    /**
     * Converts this ChargingStation object to a TSV string.
     */
    public String toTsv() {
        return name + TAB
                + streetAddress + TAB
                + city + TAB
                + state + TAB
                + zip + TAB
                + phone + TAB
                + hours + TAB
                + network + TAB
                + url + TAB
                + Double.toString(mLatitude) + TAB
                + Double.toString(mLongitude) + TAB
                + connector;
    }
}
