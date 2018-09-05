/*
 * Copyright 2016 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

if (window['ga']) {
  window['ga'](function() {
    var loc = window.location;
    window['ga'].getAll().forEach(function(tracker) {
      // send() is disabled for file:// protocol by default.
      // remove the checks to enable it.
      if (loc.protocol === 'file:') {
        tracker.set('checkProtocolTask', null);
        tracker.set('checkStorageTask', null);
        tracker.set('dataSource', 'offline');
        tracker.set('location', 'https://offline' + loc.pathname);
      }
      tracker.send('pageview');
    });
  });
}
