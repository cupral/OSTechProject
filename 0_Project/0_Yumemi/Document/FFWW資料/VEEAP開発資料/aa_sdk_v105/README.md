# Android Auto SDK

## Getting started

### Setup your build environment
- Install the latest version of the Android SDK. Make sure the following
  packages are present in the Android SDK:
    - Android SDK Build-tools 25.0.2
    - Android SDK Platform 24
    - Latest Android Support Repository
    - Latest Android Auto Desktop Head Unit
    - Optionally, follow instructions on [Google APIs guide](https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project)
      if you want to use Google Play services APIs in your app.

- The java version on your development machine should be 1.8 (or Java 8).
- [Jack toolchain](https://source.android.com/source/jack.html) is required to
  build your apps. Follow instructions on [this guide](http://tools.android.com/tech-docs/jackandjill)
  to set it up.

### Setup your device
- Your device should be running a userdebug build of Android signed with
  dev-keys. Support for production builds signed with release-keys is
  coming soon!
- Google Play Services version 10.0.84 or above should be installed on the
  device. Visit the Play Store to install the latest version if required.
- Install the pre-release build of Android Auto app that was distributed
  along with this Android Auto SDK (gearhead.apk).

All the above steps are required. You will run into runtime errors if you
skip any step.

## Build the sample app
**NOTE: Before building the app, you should first insert your own Google Maps Android API key into the file:**

`samples/chargingstations/src/main/res/values/google_maps_api.xml`

Instructions on how to get a Google Maps Android API key can be found [here](https://developers.google.com/maps/documentation/android-api/signup).

If you choose to skip this step, the app will run; however, the map will not render, and a gray background will be displayed (see Troubleshooting section below).

You can build the app in Android Studio or from the command line by following the steps below.

### Build the sample app in Android Studio

- Open Android Studio
- Select `File > New > Import Project...`
- In `Select Eclipse or Gradle Project to Import` dialog, find the Android Auto
  SDK directory, and select `samples/build.gradle` file, and click `OK`
- Click `OK` in `Gradle Sync` dialog to allow Android Studio to add Gradle
  wrapper for the project

### Build the sample app using command line

You can install the app by running it in Android Studio, or by issuing the following command from the samples directory of the extracted zip content.

```sh
$ ./gradlew --daemon :chargingstations:assembleDebug
```

## Installation
From the root of the extracted zip files

```sh
$ adb install -r -d -g
./samples/chargingstations/build/outputs/apk/chargingstations-debug.apk
```

## Run Android Auto

### In a vehicle
- Plug your phone into an Android Auto compatible car and it should start.
- Complete the First Run Experience if needed.
- Android Auto should launch into the Home screen, with a stream of
  notifications.
- Tap the OEM icon (5th facet) to view the App Switcher, in which you can
  select your apps and the sample app.

### On a development desktop
- Plug your phone into your desktop.
- Follow the Desktop Head Unit guide and setup port forwarding.
- Launch the Desktop Head Unit.
- The rest of the steps are similar to running Android Auto in a vehicle.

## Troubleshooting
- If you get any errors when building the project, open `build.gradle` file for
  `chargingstations` module, and follow Android Studio's suggestions to fix the
  version numbers for Android component libraries
- If the map does not show up when you run RoboCharge demo app, make sure you
  have copied your Google Maps API key into file
  `samples/chargingstations/src/main/res/values/google_maps_api.xml`

## Known Issues
- If you have
  [minifyEnabled](https://developer.android.com/studio/build/shrink-code.html)
  for your app, you may need to compile your app twice to avoid
  ClassNotFoundException at runtime.
- android.widget.Spinner does not work in Android Auto
- MapView content renders at different sizes on different phones, typically
  too large.
