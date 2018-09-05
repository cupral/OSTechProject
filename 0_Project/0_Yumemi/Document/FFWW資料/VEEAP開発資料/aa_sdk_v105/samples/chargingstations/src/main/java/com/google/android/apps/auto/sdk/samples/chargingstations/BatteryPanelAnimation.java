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

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import com.google.android.apps.auto.sdk.DayNightStyle;

/**
 * BatteryPanelAnimation class implements animated transitions between battery status screen and
 * the map that displays the locations of charging stations.
 */
public class BatteryPanelAnimation {
    private static final long FAB_FADE_IN_DELAY_MS = 250;
    private static final long REVEAL_ANIMATION_DURATION_MS = 333;
    private static final long REVEAL_ANIMATION_DELAY_MS = 33;
    private static final long TRANSLATION_ANIMATION_DURATION_MS = 317;
    private static final long TRANSLATION_Y_ANIMATION_DELAY_MS = 67;

    private ChargingStationMapActivity mActivity;
    private TextSearchManager mTextSearchManager;

    public BatteryPanelAnimation(
            ChargingStationMapActivity activity, TextSearchManager textSearchManager) {
        mActivity = activity;
        mTextSearchManager = textSearchManager;
    }

    /**
     * Hides battery status panel revealing the map of charging stations underneath.
     */
    void hideBatteryPanel() {
        // Load animation to fade out the battery status panel
        final View batteryStatusPanel = mActivity.findViewById(R.id.battery_status_panel);
        Animator batteryStatusAnimator = AnimatorInflater.loadAnimator(
                batteryStatusPanel.getContext(), R.animator.hide_battery_panel);
        batteryStatusAnimator.setTarget(batteryStatusPanel);
        batteryStatusAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Update UI (mostly status bar) for map display
                batteryStatusPanel.setVisibility(View.GONE);
                mTextSearchManager.setBatteryStatusPanelShowing(false);
                mActivity.getCarUiController()
                        .getStatusBarController().setDayNightStyle(DayNightStyle.AUTO);
            }
        });
        // Prepare animations for fab and its backing
        final View fab = mActivity.findViewById(R.id.battery_status_fab);
        Animator fabAnimator = loadShowFabAnimator(fab);
        final View fabBacking = mActivity.findViewById(R.id.battery_status_fab_backing);
        Animator fabBackingAnimator = loadShowFabAnimator(fabBacking);

        // Combine fab animations and set them to start after text search box animates in
        AnimatorSet fadeInFab = new AnimatorSet();
        fadeInFab.playTogether(fabBackingAnimator, fabAnimator);
        fadeInFab.setStartDelay(FAB_FADE_IN_DELAY_MS);

        AnimatorSet fullAnimator = new AnimatorSet();
        fullAnimator.playSequentially(batteryStatusAnimator, fadeInFab);
        fullAnimator.start();
    }

    /**
     * Loads animator to show the FAB and prepares to apply it to a view.
     *
     * @param view View to be animated using "show FAB" animator
     * @return the prepared animator targeting the given view
     */
    private Animator loadShowFabAnimator(View view) {
        // Initialize scale and alpha to start values
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setAlpha(0f);
        // Load animator and set the given view to be its target
        Animator growAndFadeIn =
                AnimatorInflater.loadAnimator(view.getContext(), R.animator.show_fab);
        growAndFadeIn.setTarget(view);
        return growAndFadeIn;
    }

    /**
     * Shows the battery status panel hiding the map of charging stations.
     */
    void showBatteryPanel() {
        // Prepare the UI for battery status panel being visible
        mTextSearchManager.setBatteryStatusPanelShowing(true);
        mActivity.getCarUiController()
                .getStatusBarController().setDayNightStyle(DayNightStyle.FORCE_NIGHT);

        // Find the key views involved in the transition and some dimensions of them
        final View batteryStatusPanel = mActivity.findViewById(R.id.battery_status_panel);
        final View fab = mActivity.findViewById(R.id.battery_status_fab);
        final View fabDisc = mActivity.findViewById(R.id.battery_status_fab_backing);
        int panelWidth = batteryStatusPanel.getWidth();
        int panelHeight = batteryStatusPanel.getHeight();
        int fabCenterX = fab.getLeft() + fab.getWidth() / 2;
        int fabCenterY = fab.getTop() + fab.getHeight() / 2;

        // Move center point of batteryStatusPanel to center of FAB
        batteryStatusPanel.setTranslationX(fabCenterX - panelWidth / 2);
        batteryStatusPanel.setTranslationY(fabCenterY - panelHeight / 2);
        // Compute final radius for the circular reveal (from starting point to a corner)
        int xDistance = panelWidth / 2;
        int yDistance = panelHeight / 2;
        int finalRadius =
                (int) Math.round(Math.sqrt(xDistance * xDistance + yDistance * yDistance));

        // Create circular reveal animation (from center point of FAB)
        Animator revealAnimator =  ViewAnimationUtils.createCircularReveal(batteryStatusPanel,
                panelWidth / 2, panelHeight / 2, fab.getWidth() / 2, finalRadius);
        revealAnimator.setInterpolator(new FastOutSlowInInterpolator());
        revealAnimator.setDuration(REVEAL_ANIMATION_DURATION_MS);
        revealAnimator.setStartDelay(REVEAL_ANIMATION_DELAY_MS);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Make batteryStatusPanel visible when transition begins
                batteryStatusPanel.setAlpha(1);
                batteryStatusPanel.setVisibility(View.VISIBLE);
            }
        });

        // Create X and Y translation animations for battery status panel
        ObjectAnimator batteryStatusXAnimator = ObjectAnimator.ofFloat(batteryStatusPanel,
                View.TRANSLATION_X, batteryStatusPanel.getTranslationX(), 0);
        batteryStatusXAnimator.setDuration(TRANSLATION_ANIMATION_DURATION_MS);
        batteryStatusXAnimator.setInterpolator(new FastOutSlowInInterpolator());
        ObjectAnimator batteryStatusYAnimator = ObjectAnimator.ofFloat(batteryStatusPanel,
                View.TRANSLATION_Y, batteryStatusPanel.getTranslationY(), 0);
        batteryStatusYAnimator.setDuration(TRANSLATION_ANIMATION_DURATION_MS);
        batteryStatusYAnimator.setStartDelay(TRANSLATION_Y_ANIMATION_DELAY_MS);
        batteryStatusYAnimator.setInterpolator(new FastOutSlowInInterpolator());
        AnimatorSet batteryStatusAnimator = new AnimatorSet();
        batteryStatusAnimator.playTogether(batteryStatusXAnimator, batteryStatusYAnimator);

        // Clone X and Y translation animations for FAB
        ObjectAnimator fabXAnimator = batteryStatusXAnimator.clone();
        fabXAnimator.setFloatValues(0, -batteryStatusPanel.getTranslationX());
        fabXAnimator.setTarget(fab);
        ObjectAnimator discXAnimator = fabXAnimator.clone();
        discXAnimator.setTarget(fabDisc);
        ObjectAnimator fabYAnimator = batteryStatusYAnimator.clone();
        fabYAnimator.setFloatValues(0, -batteryStatusPanel.getTranslationY());
        fabYAnimator.setTarget(fab);
        ObjectAnimator discYAnimator = fabYAnimator.clone();
        discYAnimator.setTarget(fabDisc);
        AnimatorSet fabAnimator = new AnimatorSet();
        fabAnimator.playTogether(fabXAnimator, fabYAnimator, discXAnimator, discYAnimator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(batteryStatusAnimator, fabAnimator, revealAnimator);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Restore the original location of the FAB when animation ends
                fab.setTranslationX(0f);
                fab.setTranslationY(0f);
                fabDisc.setTranslationX(0f);
                fabDisc.setTranslationY(0f);
            }
        });
        animatorSet.start();
    }
}
