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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides the latest known user location when requested.
 */
public abstract class UserLocationProvider {
    /**
     * Interface to receive non-null location.
     */
    public interface LocationCallback {
        /**
         * Called when the user location is available.
         */
        void locationReceived(LatLng location);
    }

    /** Flag indicating whether user location should be tracked */
    private boolean mIsRequestingUserLocations = false;
    /** Set of objects waiting for single location */
    private final Set<LocationCallback> mSingleCallbacks;
    /** Set of objects subscribed to location updates */
    private final Set<LocationCallback> mSubscriptionCallbacks;
    /** Handler used for posting callbacks to main UI thread */
    private final Handler mMainThreadHandler;
    /** Context this UserLocationProvider is running in */
    protected Context mContext;

    protected UserLocationProvider(Context context) {
        mContext = context;
        mSingleCallbacks = new HashSet<>();
        mSubscriptionCallbacks = new HashSet<>();
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Returns the last known user location. This location may be stale or null.
     */
    @Nullable
    public abstract LatLng getUserLocation();

    /**
     * Requests the next available location to be sent to given callback. If the location is
     * already available, the callback is triggered immediately. The callback is always
     * invoked in the main thread.
     *
     * @param callback Callback to be notified once when location has been acquired
     * @return False if the app does not have permission to access user location (in that case the
     * callback is never notified of location). True if the permission has been granted and the
     * callback will receive the location as soon as it is available.
     */
    public boolean getUserLocationAsync(@NonNull LocationCallback callback) {
        boolean success = addLocationCallback(callback, mSingleCallbacks);
        if (success) {
            // If user location is already known, send location immediately
            LatLng userLocation = getUserLocation();
            if (userLocation != null) {
                deliverLocation(userLocation);
            }
        }
        return success;
    }

    /**
     * Requests future user location updates to be delivered to given callback. The callback is
     * always invoked in the main thread. Note that the next location update may take a long time
     * if the user is not moving. To find user's location as quickly as possible, call
     * getUserLocationAsync().
     *
     * @param callback Callback to be notified of future location updates.
     * @return False if the app does not have permission to access user location (in that case the
     * callback will not be notified of location updates later). True if the permission has been
     * granted and the callback will start receiving user location updates when they occur.
     */
    public boolean addUserLocationSubscriber(@NonNull LocationCallback callback) {
        return addLocationCallback(callback, mSubscriptionCallbacks);
    }

    /**
     * Removes a location update subscriber.
     *
     * @param callback Callback that should no longer be notitified of location updates
     */
    public void removeUserLocationSubscriber(@NonNull LocationCallback callback) {
        synchronized (mSubscriptionCallbacks) {
            mSubscriptionCallbacks.remove(callback);
        }
    }

    /**
     * Adds a location callback to a given set of callbacks if user location permission has been
     * granted.
     *
     * @param callback Callback to be added
     * @param listenerSet Callback set to which the callback will be added
     * @return True if the app has access to user location and the callback was added to the
     * listener set. False, if the app does not have peremission to access user location.
     */
    private boolean addLocationCallback(@NonNull LocationCallback callback,
            @NonNull Set<LocationCallback> listenerSet) {
        if (!isUserLocationPermissionGranted()) {
            return false;
        }
        synchronized (listenerSet) {
            listenerSet.add(callback);
        }
        return true;
    }

    /**
     * Performs necessary preparations to acquire user location. In some cases these preparations
     * may require some time or resources. This method should not start tracking user location.
     * Some implementations may need to connect to a GoogleApiClient or something similar.
     */
    public void connect() {}

    /**
     * Performs necessary actions to release resources allocated in connect() method. Default
     * implementation calls stopLocationUpdates() if location updates are still being requested
     * when this method is called.
     */
    public void disconnect() {
        if (mIsRequestingUserLocations) {
            stopLocationUpdates();
        }
    }

    /**
     * Starts tracking user location.
     */
    public void startLocationUpdates() {
        mIsRequestingUserLocations = true;
    }

    /**
     * Stops tracking user location.
     */
    public void stopLocationUpdates() {
        mIsRequestingUserLocations = false;
    }

    /**
     * Returns true if the user location is being tracked.
     */
    public boolean isLocationUpdating() {
        return mIsRequestingUserLocations;
    }

    /**
     * Returns true if the app has permission to access user location.
     */
    public boolean isUserLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Delivers a location to all waiting callback objects in main UI thread.
     */
    protected void deliverLocation(@NonNull final LatLng location) {
        // To avoid threading issues, copy single callbacks to a local list and clear them
        final List<LocationCallback> callbacks = new ArrayList<>();
        synchronized (mSingleCallbacks) {
            if (!mSingleCallbacks.isEmpty()) {
                callbacks.addAll(mSingleCallbacks);
                mSingleCallbacks.clear();
            }
        }
        // Add subscribers to the list without clearing them
        synchronized (mSubscriptionCallbacks) {
            if (!mSubscriptionCallbacks.isEmpty()) {
                callbacks.addAll(mSubscriptionCallbacks);
            }
        }

        if (!callbacks.isEmpty()) {
            // Post a runnable to main thread to deliver the location
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (LocationCallback callback : callbacks) {
                        callback.locationReceived(location);
                    }
                }
            });
        }
    }
}
