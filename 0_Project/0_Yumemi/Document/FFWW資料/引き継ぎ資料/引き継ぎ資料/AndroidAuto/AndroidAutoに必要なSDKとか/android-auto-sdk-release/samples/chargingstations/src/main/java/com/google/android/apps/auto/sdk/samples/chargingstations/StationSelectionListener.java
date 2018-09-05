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

import android.support.annotation.Nullable;
import com.google.android.apps.auto.sdk.samples.chargingstations.data.NearbyStation;

/**
 * StationSelectionListener receives callback when a user selects a station. The method may be
 * called with null NearbyStation argument to indicate no station is selected.
 */
public interface StationSelectionListener {
    /**
     * Callback for station selection or deselection.
     *
     * @param station selected station or null if no station is selected
     */
    void onStationSelected(@Nullable NearbyStation station);
}
