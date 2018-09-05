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

package com.google.android.apps.auto.sdk.samples.chargingstations.permissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.car.Car;
import android.support.car.CarConnectionCallback;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.apps.auto.sdk.samples.chargingstations.R;

/**
 * PermissionActivity requests location permission from user for the Android Auto Charging
 * Stations sample app. This activity can be opened only when Android Auto is not running.
 * Permissions cannot be requested when Android Auto is active.
 *
 * <p>
 * The permission request logic implemented here goes like this:
 * <ul>
 *     <li>when the user opens the phone app (via notification or launcher icon), the activity
 *     requests for location permission from the system
 *     <li>if permission has already been granted, no permission dialog is displayed, and the
 *     activity shows "all OK" screen
 *     <li>if permission has been permanently denied ("don't ask again"), no permission dialog is
 *     displayed and the activity shows a screen explaining why the location permission is needed
 *     with a button to open system settings for the app where permissions can be adjusted
 *     <li>if the permission dialog is shown, the activity shows either the "all OK" screen (if
 *     permission was granted), or the screen with the permission button
 *     <li>Note: the permission dialog is displayed every time the activity is resumed, unless the
 *     activity is resuming after the permission query has been answered
 * </ul>
 */
public class PermissionActivity extends AppCompatActivity {
    /**
     * Action string used in local broadcast to notify receivers about location permission having
     * been granted to the app.
     */
    public static final String LOCATION_PERMISSION_ACTION = "LocationPermissionGranted";

    private static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    // ID used for location permission request
    private static final int LOCATION_PERMISSION_REQUEST_ID = 1;

    private boolean mIsPermissionRequestReturning = false;
    private Car mCar;
    private boolean mIsCarConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        // Create a car object with a callback object that keeps track of the connection state
        // When location permission is granted and the phone is connected to a car, this activity
        // can be closed automatically.
        mCar = Car.createCar(this, new CarConnectionDetector());

        // Attach a click listener to permission request button that launches "app details"
        // activity in the system settings. From there the user can change the app permissions.
        TextView requestButton = (TextView) findViewById(R.id.request_permission_button);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAppDetailsSystemActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Attempt to connect to a car (starts reporting connectivity state to this activity)
        mCar.connect();
    }

    @Override
    protected void onStop() {
        // Disconnect car connection detector to avoid memory leaks
        mCar.disconnect();

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update display based on whether user has granted location permission to the app
        updateDisplay(hasLocationPermission());

        // Request location permission (unless returning from permission dialog)
        if (!mIsPermissionRequestReturning) {
            requestLocationPermission();
        }
        mIsPermissionRequestReturning = false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_ID) {
            // Some other permission request, pass to super class
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        // Set flag to avoid triggering immediate second request if permission was denied
        mIsPermissionRequestReturning = true;
        // Process response to location permission request
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission was granted, post a local broadcast about it to CarActivity
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(new Intent(LOCATION_PERMISSION_ACTION));

            // If the phone is connected to a car, we can terminate phone activity immediately,
            // otherwise we update the screen and request the user to connect to car.
            if (mIsCarConnected) {
                finish();
            } else {
                updateDisplay(true /* permissionGranted */);
            }
        }
    }

    /**
     * Checks whether the app has been granted access to device's fine location.
     *
     * @return true if access to fine location has been granted
     */
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Triggers request for permission to access fine location.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this, new String[] { LOCATION_PERMISSION }, LOCATION_PERMISSION_REQUEST_ID);
    }

    /**
     * Update the screen based on whether the app has permission to access device's location or
     * not.
     *
     * @param permissionGranted whether the permission to access device's location has been granted
     */
    private void updateDisplay(boolean permissionGranted) {
        // Inflate the appropriate layout into the screen
        ViewGroup contentArea = (ViewGroup) findViewById(R.id.content_panel);
        int viewId = permissionGranted ? R.id.permission_granted : R.id.permission_request;
        contentArea.bringChildToFront(findViewById(viewId));
    }

    /**
     * Launch system settings activity to display details for this app. That page contains a
     * section where the user can provide the app the permission to access device location.
     */
    private void startAppDetailsSystemActivity() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);
    }

    /**
     * Monitors car connection state, and terminates activity when location permission has been
     * granted and the phone is connected to a car.
     */
    private class CarConnectionDetector extends CarConnectionCallback {
        @Override
        public void onConnected(Car car) {
            mIsCarConnected = true;
            if (hasLocationPermission()) {
                finish();
            }
        }

        @Override
        public void onDisconnected(Car car) {
            mIsCarConnected = false;
        }
    }
}
