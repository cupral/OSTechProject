package com.honda.mdve.sampleapplication;

import android.widget.Spinner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WidgetData {
    /* Aircon */
    public static final int[] AirconClimateStatus_seat                  = new int[]{0, 1, 2, 3};
    public static final int[] AirconClimateStatus_climatestate          = new int[]{0, 1, 2, 10, 11, 12, 13};
    public static final int[] AirconACSetting_seat                      = new int[]{0};
    public static final int[] AirconACSetting_acstatus                  = new int[]{0, 1, 10, 11};
    public static final int[] AirconModeSetting_seat                    = new int[]{0, 1, 2, 3};
    public static final int[] AirconModeSetting_modestatus              = new int[]{1, 2, 3, 4, 5, 10, 11, 12};
    public static final int[] AirconFanUpDown_seat                      = new int[]{0, 3};
    public static final int[] AirconFanUpDown_fanupdown                 = new int[]{1, 2, 11, 12};
    public static final int[] AirconFanSetting_seat                     = new int[]{0, 3};
    public static final int[] AirconFanSetting_fanvalue                 = new int[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
    public static final int[] AirconTempUpDown_seat                     = new int[]{0, 1, 2, 3};
    public static final int[] AirconTempUpDown_tempupdown               = new int[]{1, 2, 3, 4, 5, 11, 12, 13, 14, 15};
    public static final int[] AirconTempSetting_seat                    = new int[]{0, 1, 2, 3};
    public static final int[] AirconFrDefSetting_seat                   = new int[]{0};
    public static final int[] AirconFrDefSetting_defstatus              = new int[]{0, 1, 10};
    public static final int[] AirconRrDefSetting_seat                   = new int[]{0};
    public static final int[] AirconRrDefSetting_defstatus              = new int[]{0, 1, 10};
    public static final int[] AirconSyncSetting_seat                    = new int[]{0};
    public static final int[] AirconSyncSetting_syncstatus              = new int[]{0, 1, 10};
    public static final int[] AirconRecFrsSetting_seat                  = new int[]{0};
    public static final int[] AirconRecFrsSetting_recfrsstatus          = new int[]{0, 1, 10, 11};
    public static final int[] AirconVentTempSetting_seat                = new int[]{1, 2};
    public static final int[] AirconVentTempSetting_venttemp            = new int[]{1, 2, 3, 4, 5};
    /* Audio */
    public static final int[] AudioSourceChangeDirect_source            = new int[]{0, 1, 2, 3, 40, 41, 50, 51, 60};
    public static final int[] AudioSourceChange_next                    = new int[]{0, 1};
    public static final int[] AudioSkipUpDown_skip                      = new int[]{0, 1};
    public static final int[] AudioPlay_play                            = new int[]{0, 1};
    public static final int[] AudioFfRew_ffrew                          = new int[]{0, 1, 2};
    public static final int[] AudioSeek_seek                            = new int[]{0, 1, 2};
    public static final int[] AudioShuffle_shufflemode                  = new int[]{0, 1, 2};
    public static final int[] AudioRepeat_repeatmode                    = new int[]{0, 1, 2};
    public static final int[] AudioSetAudioVol_updown                   = new int[]{0, 1};
    public static final int[] AudioScan_source                          = new int[]{0, 1};
    public static final int[] AudioScan_scan                            = new int[]{0, 1};
    public static final int[] AudioTune_source                          = new int[]{0, 1};
    public static final int[] AudioTune_updown                          = new int[]{0, 1, 2, 3, 4};
    /* Setting */
    public static final int[] SettingDisplay_videosource                 = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] SettingDisplay_dispsetkind                 = new int[]{0, 1, 2, 3, 4};
    public static final int[] SettingDisplay_updown                      = new int[]{0, 1};
    public static final int[] SettingVolume_volkind                      = new int[]{0, 1, 2, 10, 11, 12};
    public static final int[] SettingVolume_updown                       = new int[]{0, 1};
    public static final int[] SettingVolumeDirect_volkind                = new int[]{0, 1};
    public static final int[] SettingControlVol_volume                   = new int[]{0, 1, 2, 3};
    public static final int[] SettingTouchPanelSensitivity_sensitivity   = new int[]{0, 1};
    public static final int[] SettingTone_tonekind                       = new int[]{0, 1, 2};
    public static final int[] SettingTone_updown                         = new int[]{0, 1};
    public static final int[] SettingToneDirect_tonekind                 = new int[]{0, 1, 2};
    public static final int[] SettingFaderBalance_faderbalance           = new int[]{0, 1};
    public static final int[] SettingFaderBalance_updownleftright        = new int[]{0, 1};
    public static final int[] SettingFaderBalanceDirect_faderbalance     = new int[]{0, 1};
    public static final int[] SettingSubwoofer_updown                    = new int[]{0, 1};
    public static final int[] SettingSVC_set                             = new int[]{0, 1, 2, 3};
    /* Navigation */
    // Nothing
    /* Notification */
    public static final int[] NotificationPopup_kind                     = new int[]{0, 10, 30};
    /* Response */
    public static final int[] AudioGetPresetList_source                  = new int[]{0, 1, 2};
    public static final int[] AudioSetPresetList_source                  = new int[]{0, 1};
    public static final int[] AudioSetPresetList_preset                  = new int[]{1, 2};
    public static final int[] AudioSetPresetListSXM_source               = new int[]{2};
    public static final int[] AudioGetStationList_source                 = new int[]{0, 1};

    /* First - Spinner ID / Second - Spinner 表示文字リスト */
    public static final HashMap<Integer, Integer> spinnerMap = new HashMap<Integer, Integer>(){{
        put(R.id.AirconClimateStatus_seat,                  R.array.AirconClimateStatus_seat_list);
        put(R.id.AirconClimateStatus_climatestate,          R.array.AirconClimateStatus_climatestate_list);
        put(R.id.AirconACSetting_seat,                      R.array.AirconACSetting_seat_list);
        put(R.id.AirconACSetting_acstatus,                  R.array.AirconACSetting_acstatus_list);
        put(R.id.AirconModeSetting_seat,                    R.array.AirconModeSetting_seat_list);
        put(R.id.AirconModeSetting_modestatus,              R.array.AirconModeSetting_modestatus_list);
        put(R.id.AirconFanUpDown_seat,                      R.array.AirconFanUpDown_seat_list);
        put(R.id.AirconFanUpDown_fanupdown,                 R.array.AirconFanUpDown_fanupdown_list);
        put(R.id.AirconFanSetting_seat,                     R.array.AirconFanSetting_seat_list);
        put(R.id.AirconFanSetting_fanvalue,                 R.array.AirconFanSetting_fanvalue_list);
        put(R.id.AirconTempUpDown_seat,                     R.array.AirconTempUpDown_seat_list);
        put(R.id.AirconTempUpDown_tempupdown,               R.array.AirconTempUpDown_tempupdown_list);
        put(R.id.AirconTempSetting_seat,                    R.array.AirconTempSetting_seat_list);
        put(R.id.AirconFrDefSetting_seat,                   R.array.AirconFrDefSetting_seat_list);
        put(R.id.AirconFrDefSetting_defstatus,              R.array.AirconFrDefSetting_defstatus_list);
        put(R.id.AirconRrDefSetting_seat,                   R.array.AirconRrDefSetting_seat_list);
        put(R.id.AirconRrDefSetting_defstatus,              R.array.AirconRrDefSetting_defstatus_list);
        put(R.id.AirconSyncSetting_seat,                    R.array.AirconSyncSetting_seat_list);
        put(R.id.AirconSyncSetting_syncstatus,              R.array.AirconSyncSetting_syncstatus_list);
        put(R.id.AirconRecFrsSetting_seat,                  R.array.AirconRecFrsSetting_seat_list);
        put(R.id.AirconRecFrsSetting_recfrsstatus,          R.array.AirconRecFrsSetting_recfrsstatus_list);
        put(R.id.AirconVentTempSetting_seat,                R.array.AirconVentTempSetting_seat_list);
        put(R.id.AirconVentTempSetting_venttemp,            R.array.AirconVentTempSetting_venttemp_list);
        put(R.id.AudioSourceChangeDirect_source,            R.array.AudioSourceChangeDirect_source_list);
        put(R.id.AudioSourceChange_next,                    R.array.AudioSourceChange_next_list);
        put(R.id.AudioSkipUpDown_skip,                      R.array.AudioSkipUpDown_skip_list);
        put(R.id.AudioPlay_play,                            R.array.AudioPlay_play_list);
        put(R.id.AudioFfRew_ffrew,                          R.array.AudioFfRew_ffrew_list);
        put(R.id.AudioSeek_seek,                            R.array.AudioSeek_seek_list);
        put(R.id.AudioShuffle_shufflemode,                  R.array.AudioShuffle_shufflemode_list);
        put(R.id.AudioRepeat_repeatmode,                    R.array.AudioRepeat_repeatemode_list);
        put(R.id.AudioSetAudioVol_updown,                   R.array.AudioSetAudioVol_updown_list);
        put(R.id.AudioScan_source,                          R.array.AudioScan_source_list);
        put(R.id.AudioScan_scan,                            R.array.AudioScan_scan_list);
        put(R.id.AudioTune_source,                          R.array.AudioTune_source_list);
        put(R.id.AudioTune_updown,                          R.array.AudioTune_updown_list);
        put(R.id.SettingDisplay_videosource,                R.array.SettingDisplay_videosource_list);
        put(R.id.SettingDisplay_dispsetkind,                R.array.SettingDisplay_dispsetkind_list);
        put(R.id.SettingDisplay_updown,                     R.array.SettingDisplay_updown_list);
        put(R.id.SettingVolume_volkind,                     R.array.SettingVolume_volkind_list);
        put(R.id.SettingVolume_updown,                      R.array.SettingVolume_updown_list);
        put(R.id.SettingVolumeDirect_volkind,               R.array.SettingVolumeDirect_volkind_list);
        put(R.id.SettingControlVol_volume,                  R.array.SettingControlVol_volume_list);
        put(R.id.SettingTouchPanelSensitivity_sensitivity,  R.array.SettingTouchPanelSensitivity_volume_list);
        put(R.id.SettingTone_tonekind,                      R.array.SettingTone_tonekind_list);
        put(R.id.SettingTone_updown,                        R.array.SettingTone_updown_list);
        put(R.id.SettingToneDirect_tonekind,                R.array.SettingToneDirect_tonekind_list);
        put(R.id.SettingFaderBalance_faderbalance,          R.array.SettingFaderBalance_faderbalance_list);
        put(R.id.SettingFaderBalance_updownleftright,       R.array.SettingFaderBalance_updownleftright_list);
        put(R.id.SettingFaderBalanceDirect_faderbalance,    R.array.SettingFaderBalanceDirect_faderbalance_list);
        put(R.id.SettingSubwoofer_updown,                   R.array.SettingSubwoofer_updown_list);
        put(R.id.SettingSVC_set,                            R.array.SettingSVC_set_list);
        put(R.id.NotificationPopup_kind,                    R.array.NotificationPopup_kind_list);
    }};

    public static final HashMap<Integer, Integer> spinnerMap2 = new HashMap<Integer, Integer>() {{
            put(R.id.AudioGetPresetList_source,             R.array.AudioGetPresetList_source_list);
            put(R.id.AudioSetPresetList_source,             R.array.AudioSetPresetList_source_list);
            put(R.id.AudioSetPresetList_preset,             R.array.AudioSetPresetList_preset_list);
            put(R.id.AudioSetPresetListSXM_source,          R.array.AudioSetPresetListSXM_source_list);
            put(R.id.AudioGetStationList_source,            R.array.AudioGetStationList_source_list);
    }};

    // タッチリリースイベント専用ボタンIDリスト
    public static final int[] onTouchUpList = new int[]
            {       R.id.AirconClimateStatus_Send,
                    R.id.AirconACSetting_Send,
                    R.id.AirconModeSetting_Send,
                    R.id.AirconFanUpDown_Send,
                    R.id.AirconFanSetting_Send,
                    R.id.AirconTempUpDown_Send,
                    R.id.AirconTempSetting_Send,
                    R.id.AirconFrDefSetting_Send,
                    R.id.AirconRrDefSetting_Send,
                    R.id.AirconSyncSetting_Send,
                    R.id.AirconRecFrsSetting_Send,
                    R.id.AirconVentTempSetting_Send,
                    R.id.AudioSourceChangeDirect_Send,
                    R.id.AudioSourceChange_Send,
                    R.id.AudioSkipUpDown_Send,
                    R.id.AudioPlay_Send,
                    R.id.AudioFfRew_Send,
                    R.id.AudioSeek_Send,
                    R.id.AudioShuffle_Send,
                    R.id.AudioRepeat_Send,
                    R.id.AudioSetChannelSXM_Send,
                    R.id.AudioSetAudioVol_Send,
                    R.id.AudioSetAudioVolDirect_Send,
                    R.id.AudioScan_Send,
                    R.id.AudioTune_Send,
                    R.id.SettingDisplay_Send,
                    R.id.SettingVolume_Send,
                    R.id.SettingVolumeDirect_Send,
                    R.id.SettingControlVol_Send,
                    R.id.SettingTouchPanelSensitivity_Send,
                    R.id.SettingTone_Send,
                    R.id.SettingToneDirect_Send,
                    R.id.SettingFaderBalance_Send,
                    R.id.SettingFaderBalanceDirect_Send,
                    R.id.SettingSubwoofer_Send,
                    R.id.SettingSubwooferDirect_Send,
                    R.id.SettingSVC_Send,
                    R.id.NaviDestinationLatLon_Send,
                    R.id.NotificationPopup_Send,
                    R.id.Performance_Send
            };

    public static final int[] onTouchUpList2 = new int[]
            {       R.id.AudioGetPresetList_Send,
                    R.id.AudioSetPresetList_Send,
                    R.id.AudioSetPresetListSXM_Send,
                    R.id.AudioGetStationList_Send
            };
}