//
//  ViewController.h
//  EAPSampleApp
//
//

#import <UIKit/UIKit.h>
#import <libMDEAPframework/libMDEAPframework.h>
#import "Accesskey.h"

#define SYSTEM_SOUNDID_VIBRATE_SHORT    (1003)
#define SYSTEM_SOUNDID_SEND             (1117)

typedef enum : NSInteger{
    ACCLIMSTS_SEAT_TAG = 1,
    ACCLIMSTS_CLIM_TAG,
    ACACSET_SEAT_TAG,
    ACACSET_ACSTS_TAG,
    ACMODE_SEAT_TAG,
    ACMODE_MODE_TAG,
    ACFANUD_SEAT_TAG,
    ACFANUD_FANUD_TAG,
    ACFANSET_SEAT_TAG,
    ACFANSET_FANVAL_TAG,
    ACTMPUD_SEAT_TAG,
    ACTMPUD_TMPUD_TAG,
    ACTMP_SEAT_TAG,
    ACTMP_TMPSET_TAG,
    ACFRDEF_SEAT_TAG,
    ACFRDEF_FRDEF_TAG,
    ACRRDEF_SEAT_TAG,
    ACRRDEF_RRDEF_TAG,
    ACSYNC_SEAT_TAG,
    ACSYNC_SYNC_TAG,
    ACRECFRS_SEAT_TAG,
    ACRECFRS_RECFRS_TAG,
    ACVNT_SEAT_TAG,
    ACVNT_VNTTMP_TAG,
    ADSRCDIR_SRC_TAG,
    ADSRC_NEXT_TAG,
    ADSKIPUPDN_SKIP_TAG,
    ADPLAY_PLAY_TAG,
    ADFFREW_FRREW_TAG,
    ADSEEK_SEEK_TAG,
    ADSHUFFLE_MODE_TAG,
    ADREPEAT_MODE_TAG,
    ADCHSXM_CHNO_TAG,
    ADVOL_UPDN_TAG,
    ADVOLDIR_VOL_TAG,
    ADSCAN_SRC_TAG,
    ADSCAN_SCAN_TAG,
    ADTUNE_SRC_TAG,
    ADTUNE_UPDN_TAG,
    SETDISP_SRC_TAG,
    SETDISP_KIND_TAG,
    SETDISP_UPDN_TAG,
    SETVOL_KIND_TAG,
    SETVOL_UPDN_TAG,
    SETVOLDIR_KIND_TAG,
    SETVOLDIR_VOL_TAG,
    SETCTRLVOL_VOL_TAG,
    SETTCHPNL_SNSTVTY_TAG,
    SETTONE_KIND_TAG,
    SETTONE_UPDN_TAG,
    SETTONEDIR_KIND_TAG,
    SETTONEDIR_SET_TAG,
    SETFADER_BALANCE_TAG,
    SETFADER_UPDN_TAG,
    SETFADERDIR_BALANCE_TAG,
    SETFADERDIR_SET_TAG,
    SETSUBWOOFER_UPDN_TAG,
    SETSUBWOOFERDIR_SET_TAG,
    SETSVC_SET_TAG,
    NAVIDST_LAT_TAG,
    NAVIDST_LON_TAG,
    NAVIDST_NAME_TAG,
    NOTIPOP_KIND_TAG,
    NOTIPOP_WORD_TAG,
    SAMPLE_APP_TAG_MAX
} sampleAppTag;

extern NSString *notify_Debug_Log_Output_on;
extern NSString *notify_Debug_Log_Output_off;

@protocol ViewControllerDelegate <NSObject>
@optional
- (void)didHandleResData: (NSString *)resdata;
@end

#ifdef __STAND_ALONE__
@protocol ViewControllerTestDelegate <NSObject>
@optional
- (void)didHandleLogData: (NSString *)logdata;
@end
#endif /* __STAND_ALONE__ */

@interface ViewController : UIViewController<UITableViewDelegate,
    UITableViewDataSource,
    UITextFieldDelegate,
    UITextViewDelegate,
    UIPickerViewDelegate,
    UIPickerViewDataSource>

@property IBOutlet UILabel *timeLabel;
@property IBOutlet UITableView *table;
@property (weak, nonatomic) IBOutlet UIButton *mAutoOnOffButton;
@property (weak, nonatomic) IBOutlet UIButton *mManualButton;
@property (weak, nonatomic) IBOutlet UITextView *mRtnTextView;
@property (nonatomic, retain) UIToolbar *keyboardToolbar;

/***** Aircon *****/

// [1]AirconClimateStatus
@property (nonatomic, retain) IBOutlet UIPickerView *mAcClimStsSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcClimStsPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcClimStsSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcClimStsText;
@property (weak, nonatomic) IBOutlet UIButton *mAcClimStsSendButton;

// [2]AirconACSSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcAcSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcAcSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcAcSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcAcSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcAcSetSendButton;

// [3]AirconModeSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcModeSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcModeSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcModeSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcModeSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcModeSetSendButton;

// [4]AirconFanUpDown
@property (nonatomic, retain) IBOutlet UIPickerView *mAcFanUpDnSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcFanUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcFanUpDnSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcFanUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mAcFanUpDnSendButton;

// [5]AirconFanSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcFanSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcFanSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcFanSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcFanSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcFanSetSendButton;

// [6]AirconTempUpDown
@property (nonatomic, retain) IBOutlet UIPickerView *mAcTempUpDnSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcTempUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcTempUpDnSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcTempUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mAcTempUpDnSendButton;

// [7]AirconTempSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcTempSetSeatPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcTempSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcTempSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcTempSetSendButton;

// [8]AirconFrDefSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcFrDefSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcFrDefSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcFrDefSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcFrDefSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcFrDefSetSendButton;

// [9]AirconRrDefSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcRrDefSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcRrDefSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcRrDefSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcRrDefSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcRrDefSetSendButton;

// [10]AirconSyncSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcSyncSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcSyncSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcSyncSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcSyncSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcSyncSetSendButton;

// [11]AirconRecFrsSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcRecFrsSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcRecFrsSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcRecFrsSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcRecFrsSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcRecFrsSetSendButton;

// [12]AirconVentTempSetting
@property (nonatomic, retain) IBOutlet UIPickerView *mAcVentTempSetSeatPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAcVentTempSetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAcVentTempSetSeatText;
@property (weak, nonatomic) IBOutlet UITextField *mAcVentTempSetText;
@property (weak, nonatomic) IBOutlet UIButton *mAcVentTempSetSendButton;

/***** Audio *****/

// [13]AudioSourceChangeDirect
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioSourceChangeDirectPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioSourceChangeDirectText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSourceChangeDirectButton;

// [14]AudioSourceChange
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioSourceChangePicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioSourceChangeText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSourceChangeButton;

// [15]AudioSkipUpDown
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioSkipUpDownPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioSkipUpDownText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSkipUpDownButton;

// [16]AudioPlay
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioPlayPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioPlayText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioPlayButton;

// [17]AudioFfRew
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioFfRewPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioFfRewText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioFfRewButton;

// [18]AudioSeek
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioSeekPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioSeekText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSeekButton;

// [19]AudioShuffle
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioShufflePicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioShuffleText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioShuffleButton;

// [20]AudioRepeat
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioRepeatPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioRepeatText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioRepeatButton;

// [26]AudioSetChannelSXM
@property (weak, nonatomic) IBOutlet UITextField *mAudioSetChannelSXMText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSetChannelSXMButton;

// [27]AudioSetAudioVol
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioSetAudioVolPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioSetAudioVolText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSetAudioVolButton;

// [28]AudioSetAudioVolDirect
@property (weak, nonatomic) IBOutlet UITextField *mAudioSetAudioVolDirectText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioSetAudioVolDirectButton;

// [29]AudioScan
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioScanSourcePicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioScanUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioScanSourceText;
@property (weak, nonatomic) IBOutlet UITextField *mAudioScanUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioScanButton;

// [30]AudioTune
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioTuneSourcePicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAudioTuneUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAudioTuneSourceText;
@property (weak, nonatomic) IBOutlet UITextField *mAudioTuneUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mAudioTuneButton;

/***** Setting *****/

// [33]SettingDisplay
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingDisplaySrcPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingDisplayKindPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingDisplayUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingDisplaySrcText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingDisplayKindText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingDisplayUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingDisplayButton;

// [34]SettingVolume
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingVolumeKindPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingVolumeUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingVolumeKindText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingVolumeUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingVolumeButton;

// [35]SettingVolumeDirect
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingVolumeDirectKindPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingVolumeDirectKindText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingVolumeDirectVolText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingVolumeDirectButton;

// [36]SettingControlVol
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingControlVolPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingControlVolText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingControlVolButton;

// [37]SettingTouchPanelSensitivity
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingTouchPanelSensitivityPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingTouchPanelSensitivityText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingTouchPanelSensitivityButton;

// [38]SettingTone
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingToneKindPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingToneUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingToneKindText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingToneUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingToneButton;

// [39]SettingToneDirect
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingToneDirectKindPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingToneDirectKindText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingToneDirectSetText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingToneDirectButton;

// [40]SettingFaderBalance
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingFaderBalancePicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingFaderBalanceUpDnPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingFaderBalanceText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingFaderBalanceUpDnText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingFaderBalanceButton;

// [41]SettingFaderBalanceDirect
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingFaderBalanceDirectPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingFaderBalanceDirectText;
@property (weak, nonatomic) IBOutlet UITextField *mSettingFaderBalanceDirectSetText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingFaderBalanceDirectButton;

// [42]SettingSubwoofer
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingSubwooferPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingSubwooferText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingSubwooferButton;

// [43]SettingSubwooferDirect
@property (weak, nonatomic) IBOutlet UITextField *mSettingSubwooferDirectText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingSubwooferDirectButton;

// [44]SettingSVC
@property (nonatomic, retain) IBOutlet UIPickerView *mSettingSVCPicker;
@property (weak, nonatomic) IBOutlet UITextField *mSettingSVCText;
@property (weak, nonatomic) IBOutlet UIButton *mSettingSVCButton;

/***** Navigation *****/

// [45]NaviDestinationLatLon
@property (weak, nonatomic) IBOutlet UITextField *mNaviDestinationLatitudeText;
@property (weak, nonatomic) IBOutlet UITextField *mNaviDestinationLongitudeText;
@property (weak, nonatomic) IBOutlet UITextView *mNaviDestinationNameTextView;
@property (weak, nonatomic) IBOutlet UIButton *mNaviDestinationLatLonButton;

/***** Notification *****/

// [46]NotificationPopup
@property (nonatomic, retain) IBOutlet UIPickerView *mNotificationPopupKindPicker;
@property (weak, nonatomic) IBOutlet UITextField *mNotificationPopupKindText;
@property (weak, nonatomic) IBOutlet UITextView *mNotificationPopupWordTextView;
@property (weak, nonatomic) IBOutlet UIButton *mNotificationPopupButton;

@property (weak, nonatomic) id <ViewControllerDelegate> delegate;

#ifdef __STAND_ALONE__
@property (weak, nonatomic) id <ViewControllerTestDelegate> delegateTest;
#endif /* __STAND_ALONE__ */

- (IBAction)mAutoOnOffButton:(id)sender;
- (IBAction)mManualButton:(id)sender;
- (void)setTimestamp;

@end

