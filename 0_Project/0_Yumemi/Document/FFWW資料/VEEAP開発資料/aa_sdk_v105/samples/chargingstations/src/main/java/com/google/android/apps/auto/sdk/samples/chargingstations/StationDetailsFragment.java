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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.ChargingStation;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;
import java.util.Locale;

/**
 * StationDetailsFragment displays information about a charging station with buttons that can
 * trigger a phone call or navigation there.
 */
public class StationDetailsFragment extends Fragment {
    /**
     * Interface to receive callbacks when user triggers navigation or dialing actions.
     */
    public interface ChargingStationListener {
        /**
         * Called when user has selected to start navigation to a charging station. Navigation
         * intent should be launched when this callback is triggered.
         *
         * @param chargingStation charging station to navigate to
         */
        void navigateTo(ChargingStation chargingStation);

        /**
         * Called when the user has selected to make a call to a charging station. Dialing intent
         * should be launched when this callback is triggered.
         *
         * @param chargingStation charging station to be dialed
         */
        void dial(ChargingStation chargingStation);

        /**
         * Called when the user wants to close the details card by click the close button.
         */
        void closeDetailsCard();
    }

    private ChargingStationListener mChargingStationListener;
    private NearbyStation mNearbyStation;

    private View mContentView;
    private TextView mNameLabel;
    private TextView mHoursLabel;
    private TextView mCallButton;
    private TextView mNavigateButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup,
            @Nullable Bundle bundle) {
        mContentView = layoutInflater.inflate(R.layout.fragment_station_details, viewGroup, false);
        mNameLabel = (TextView) mContentView.findViewById(R.id.name);
        mHoursLabel = (TextView) mContentView.findViewById(R.id.hours);
        mCallButton = (TextView) mContentView.findViewById(R.id.call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChargingStationListener != null) {
                    mChargingStationListener.dial(mNearbyStation.getStation());
                }
            }
        });
        mNavigateButton = (TextView) mContentView.findViewById(R.id.navigate);
        mNavigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChargingStationListener != null) {
                    mChargingStationListener.navigateTo(mNearbyStation.getStation());
                }
            }
        });
        ImageView closeButton = (ImageView) mContentView.findViewById(R.id.details_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChargingStationListener != null) {
                    mChargingStationListener.closeDetailsCard();
                }
            }
        });
        // Update data display fields if the station data has already been set
        if (mNearbyStation != null) {
            updateContent();
        }
        return mContentView;
    }

    /**
     * Sets the listener to be notified when the user selects to dial or navigate to a charging
     * station.
     */
    public void setChargingStationListener(ChargingStationListener chargingStationListener) {
        mChargingStationListener = chargingStationListener;
    }

    /**
     * Sets the data to be displayed in the card.
     */
    public void setNearbyStation(NearbyStation nearbyStation) {
        mNearbyStation = nearbyStation;
        // If the view has been constructed already, update the data fields
        if (mContentView != null) {
            updateContent();
        }
    }

    /**
     * Populates the data fields with data from the station object
     */
    private void updateContent() {
        // Do nothing if no station has been set, or if the view has not been created
        if (mNearbyStation == null || mContentView == null) {
            return;
        }
        ChargingStation station = mNearbyStation.getStation();
        mNameLabel.setText(station.name);
        mHoursLabel.setText(station.hours);
        mCallButton.setEnabled(station.phone != null && !station.phone.isEmpty());
        if (mNearbyStation.isDistanceKnown()) {
            // Display distance in miles (a real app would use local distance units)
            mNavigateButton.setText(
                    String.format(Locale.US, "%.1f mi", mNearbyStation.getDistanceMiles()));
        } else {
            mNavigateButton.setText(getString(R.string.navigate_to_charging_station));
        }
    }
}
