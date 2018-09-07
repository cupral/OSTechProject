// IMDVEService.aidl
package com.honda.mdve.service;

import com.honda.mdve.service.IMdveCallback;
import com.honda.mdve.service.IMdveResCallback;

interface IMdveService {
	int registerCallback(in IMdveCallback callback, String accesskey, String version);
	int unregisterCallback(in IMdveCallback callback);
	String getVehicleData(String groupname, String accesskey, String version);
	String reqAudioSourceChangeDirect(String accesskey, String version, int source);
	String reqAudioSourceChange(String accesskey, String version, int next);
	String reqAudioSkipUpDown(String accesskey, String version, int skip);
	String reqAudioPlay(String accesskey, String version, int play);
	String reqAirconClimateStatus(String accesskey, String version, int seat, int climatestate);
	String reqAirconACSetting(String accesskey, String version, int seat, int acstatus);
	String reqAirconModeSetting(String accesskey, String version, int seat, int modestatus);
	String reqAirconFanUpDown(String accesskey, String version, int seat, int fanupdown);
	String reqAirconFanSetting(String accesskey, String version, int seat, int fanvalue);
	String reqAirconTempUpDown(String accesskey, String version, int seat, int tempupdown);
	String reqAirconTempSetting(String accesskey, String version, int seat, int tempvalue);
	String reqAirconFrDefSetting(String accesskey, String version, int seat, int defstatus);
	String reqAirconRrDefSetting(String accesskey, String version, int seat, int defstatus);
	String reqAirconSyncSetting(String accesskey, String version, int seat, int syncstatus);
	String reqAirconRecFrsSetting(String accesskey, String version, int seat, int recfrsstatus);
	String reqAirconVentTempSetting(String accesskey, String version, int seat, int venttemp);
	String reqAudioFfRew(String accesskey, String version, int ffrew);
	String reqAudioSeek(String accesskey, String version, int seek);
	String reqAudioShuffle(String accesskey, String version, int shufflemode);
	String reqAudioRepeat(String accesskey, String version, int repeatmode);
	String reqAudioGetPresetList(IMdveResCallback callback, String accesskey, String version, int source);
	String reqAudioSelectPresetList(String accesskey, String version, int source, int preset, int presetno);
	String reqAudioSelectPresetListSXM(String accesskey, String version, int source, int presetno);
	String reqAudioSetPresetList(IMdveResCallback callback, String accesskey, String version, int source, int preset, int presetno, int freq);
	String reqAudioSetPresetListSXM(IMdveResCallback callback, String accesskey, String version, int source, int presetno, int channelno);
	String reqAudioSetChannelSXM(String accesskey, String version, int channelno);
	String reqAudioSetAudioVol(String accesskey, String version, int updown);
	String reqAudioSetAudioVolDirect(String accesskey, String version, int vol);
	String reqAudioScan(String accesskey, String version, int source, int scan);
	String reqAudioTune(String accesskey, String version, int source, int updown);
	String reqAudioGetStationList(IMdveResCallback callback, String accesskey, String version, int source);
	String reqAudioFreqDirect(String accesskey, String version, int source, int freq);
	String reqSettingDisplay(String accesskey, String version, int videosource, int dispsetkind, int updown);
	String reqSettingVolume(String accesskey, String version, int volkind, int updown);
	String reqSettingVolumeDirect(String accesskey, String version, int volkind, int volume);
	String reqSettingControlVol(String accesskey, String version, int volume);
	String reqSettingTouchPanelSensitivity(String accesskey, String version, int sensitivity);
	String reqSettingTone(String accesskey, String version, int tonekind, int updown);
	String reqSettingToneDirect(String accesskey, String version, int tonekind, int set);
	String reqSettingFaderBalance(String accesskey, String version, int faderbalance, int updownleftright);
	String reqSettingFaderBalanceDirect(String accesskey, String version, int faderbalance, int set);
	String reqSettingSubwoofer(String accesskey, String version, int updown);
	String reqSettingSubwooferDirect(String accesskey, String version, int set);
	String reqSettingSVC(String accesskey, String version, int set);
	String reqNaviDestinationLatLon(String accesskey, String version, String latitude, String longitude, String destname);
	String reqNotificationPopup(String accesskey, String version, int kind, String wording);
}
