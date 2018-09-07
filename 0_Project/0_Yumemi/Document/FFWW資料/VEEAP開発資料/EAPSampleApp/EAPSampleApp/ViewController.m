//
//  ViewController.m
//  EAPSampleApp
//
//

#import <AudioToolbox/AudioToolbox.h>
#import "ViewController.h"
#import "EAPLog.h"

#define CNV_ID_STRING       @"String"
#define CNV_ID_DECIMAL01    @"%.1f" /* 小数第1位まで表示 */
#define CNV_ID_DECIMAL02    @"%.2f" /* 小数第2位まで表示 */
#define CNV_ID_DECIMAL03    @"%.3f" /* 小数第3位まで表示 */
#define CNV_ID_DECIMAL04    @"%.4f" /* 小数第4位まで表示 */
#define CNV_ID_DECIMAL06    @"%.6f" /* 小数第6位まで表示 */
#define CNV_ID_DECIMAL07    @"%.7f" /* 小数第7位まで表示 */

@interface ViewController () <libMDEAPframeworkDelegate>

@property (weak, nonatomic) NSString *resDataString;

@end

@implementation ViewController {
    NSMutableDictionary *statusDic;
    NSMutableDictionary *dataDic;
    NSDictionary *convSpecInfo;
    NSArray *audioDataKeys;
    
    /* Aircon Picker Data */
    NSArray *acClimStsSeatData;
    NSArray *acClimStsData;
    NSArray *acAcSetSeatData;
    NSArray *acAcSetData;
    NSArray *acModeSetSeatData;
    NSArray *acModeSetData;
    NSArray *acFanUpDnSeatData;
    NSArray *acFanUpDnData;
    NSArray *acFanSetSeatData;
    NSArray *acFanSetData;
    NSArray *acTempUpDnSeatData;
    NSArray *acTempUpDnData;
    NSArray *acTempSetSeatData;
    NSArray *acFrDefSetSeatData;
    NSArray *acFrDefSetData;
    NSArray *acRrDefSetSeatData;
    NSArray *acRrDefSetData;
    NSArray *acSyncSetSeatData;
    NSArray *acSyncSetData;
    NSArray *acRecFrsSetSeatData;
    NSArray *acRecFrsSetData;
    NSArray *acVentTempSetSeatData;
    NSArray *acVentTempSetData;
    /* Audio Picker Data */
    NSArray *adSrcChgDirData;
    NSArray *adSrcChgData;
    NSArray *adSkipUpDnData;
    NSArray *adPlayData;
    NSArray *adFfRewData;
    NSArray *adSeekData;
    NSArray *adShuffleData;
    NSArray *adRepeatData;
    NSArray *adSetAudioVolData;
    NSArray *adScanSrcData;
    NSArray *adScanData;
    NSArray *adTuneSrcData;
    NSArray *adTuneData;
    /* Setting Picker Data */
    NSArray *setDispSrcData;
    NSArray *setDispKindData;
    NSArray *setDispData;
    NSArray *setVolKindData;
    NSArray *setVolData;
    NSArray *setVolDirKindData;
    NSArray *setControlVolData;
    NSArray *setTouchPanelSensitivityData;
    NSArray *setToneKindData;
    NSArray *setToneData;
    NSArray *setToneDirKindData;
    NSArray *setFaderBalanceData;
    NSArray *setFaderBalanceUpDnData;
    NSArray *setFaderBalanceDirData;
    NSArray *setSubwooferData;
    NSArray *setSVCData;
    /* Notification Picker Data */
    NSArray *notiPopupKindData;

    /* Aircon Parameter Buffer */
    NSInteger acClimStsSeat;
    NSInteger acClimSts;
    NSInteger acAcSetSeat;
    NSInteger acAcSet;
    NSInteger acModeSetSeat;
    NSInteger acModeSet;
    NSInteger acFanUpDnSeat;
    NSInteger acFanUpDn;
    NSInteger acFanSetSeat;
    NSInteger acFanSet;
    NSInteger acTempUpDnSeat;
    NSInteger acTempUpDn;
    NSInteger acTempSetSeat;
    NSInteger acFrDefSetSeat;
    NSInteger acFrDefSet;
    NSInteger acRrDefSetSeat;
    NSInteger acRrDefSet;
    NSInteger acSyncSetSeat;
    NSInteger acSyncSet;
    NSInteger acRecFrsSetSeat;
    NSInteger acRecFrsSet;
    NSInteger acVentTempSetSeat;
    NSInteger acVentTempSet;
    /* Audio Parameter Buffer */
    NSInteger adSrcChgDir;
    NSInteger adSrcChg;
    NSInteger adSkipUpDn;
    NSInteger adPlay;
    NSInteger adFfRew;
    NSInteger adSeek;
    NSInteger adShuffle;
    NSInteger adRepeat;
    NSInteger adSetAudioVol;
    NSInteger adScanSrc;
    NSInteger adScan;
    NSInteger adTuneSrc;
    NSInteger adTune;
    /* Setting Parameter Buffer */
    NSInteger setDispSrc;
    NSInteger setDispKind;
    NSInteger setDisp;
    NSInteger setVolKind;
    NSInteger setVol;
    NSInteger setVolDirKind;
    NSInteger setControlVol;
    NSInteger setTouchPanelSensitivity;
    NSInteger setToneKind;
    NSInteger setTone;
    NSInteger setToneDirKind;
    NSInteger setFaderBalance;
    NSInteger setFaderBalanceUpDn;
    NSInteger setFaderBalanceDir;
    NSInteger setSubwoofer;
    NSInteger setSVC;
    /* Notification Parameter Buffer */
    NSInteger notiPopupKind;
    
    UITableViewCell *currentCell;
    
#ifdef __DEBUG_ON__
    NSArray *dbgCurrentAudioData;
#endif
}
@synthesize mAutoOnOffButton;
@synthesize mManualButton;
@synthesize timeLabel;

ViewController* viewController = nil;
static libMDEAPframework* libEAPSample = nil;
static EAPLog *logInfo = nil;

NSString *notify_Debug_Log_Output_on = @"notify_Debug_Log_Output_on";
NSString *notify_Debug_Log_Output_off = @"notify_Debug_Log_Output_off";


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    NSLog(@"viewDidLoad");

    libEAPSample = [[libMDEAPframework sharedController] initWithSetAccessKey:ACCESSKEY];
    
    // Start
    libEAPSample.delegate = self;
    viewController = self;

    logInfo = [EAPLog sharedLogInfo];
    [logInfo output_sw:TRUE];

    timeLabel.text = @"00:00:00.000";

    [[NSNotificationCenter defaultCenter] postNotificationName:notify_Debug_Log_Output_on object:self userInfo:nil];
    
    self.mRtnTextView.editable = NO;
    self.mRtnTextView.text = @"NO RESULT DATA";
    self.mNaviDestinationNameTextView.text = @"";
    self.mNotificationPopupWordTextView.text = @"";
    
    statusDic = [[NSMutableDictionary alloc] init];
    dataDic = [[NSMutableDictionary alloc] init];
    
    convSpecInfo = @{@"vin":CNV_ID_STRING,
                     @"headunit_part_num":CNV_ID_STRING,
                     @"headunit_sw_ver":CNV_ID_STRING,
                     @"current_audio":CNV_ID_STRING,
                     @"current_audio.strdata1":CNV_ID_STRING,
                     @"current_audio.strdata2":CNV_ID_STRING,
                     @"current_audio.strdata3":CNV_ID_STRING,
                     @"current_audio.strdata4":CNV_ID_STRING,
                     @"current_audio.strdata5":CNV_ID_STRING,
                     @"current_audio.strdata6":CNV_ID_STRING,
                     @"current_audio.strdata7":CNV_ID_STRING,
                     @"current_audio.strdata8":CNV_ID_STRING,
                     @"current_audio.strdata9":CNV_ID_STRING,
                     @"current_audio.strdata10":CNV_ID_STRING,
                     @"current_audio.strdata11":CNV_ID_STRING,
                     @"current_audio.strdata12":CNV_ID_STRING,
                     @"accumulated_fuel_consumption":CNV_ID_DECIMAL01,
                     @"average_fuel_economy_a":CNV_ID_DECIMAL01,
                     @"average_fuel_economy_b":CNV_ID_DECIMAL01,
                     @"trip_a":CNV_ID_DECIMAL01,
                     @"trip_b":CNV_ID_DECIMAL01,
                     @"steering_angle":CNV_ID_DECIMAL01,
                     @"b_voltage":CNV_ID_DECIMAL01,
                     @"ac_target_temp_dr":CNV_ID_DECIMAL01,
                     @"ac_target_temp_as":CNV_ID_DECIMAL01,
                     @"ac_target_temp_rr":CNV_ID_DECIMAL01,
                     @"vehicle_speed":CNV_ID_DECIMAL02,
                     @"traveled_distance":CNV_ID_DECIMAL02,
                     @"brake_pressure":CNV_ID_DECIMAL02,
                     @"atmospheric_pressure":CNV_ID_DECIMAL03,
                     @"engmanifoldV_sensorV":CNV_ID_DECIMAL04,
                     @"accel_pedal_position":CNV_ID_DECIMAL06,
                     @"longitudinal_g":CNV_ID_DECIMAL07,
                     @"lateral_g":CNV_ID_DECIMAL07,
                     @"yaw_rate":CNV_ID_DECIMAL07};
    
    audioDataKeys = @[@"source",
                      @"shufflemode",
                      @"repeatmode",
                      @"volume",
                      @"strdata1",
                      @"strdata2",
                      @"strdata3",
                      @"strdata4",
                      @"strdata5",
                      @"strdata6",
                      @"strdata7",
                      @"strdata8",
                      @"strdata9",
                      @"strdata10",
                      @"strdata11",
                      @"strdata12",
                      @"numdata1",
                      @"numdata2"];
    
    acClimStsSeatData = @[@[@"0",@"全席一括[0]"],
                          @[@"1",@"運転席[1]"],
                          @[@"2",@"助手席[2]"],
                          @[@"3",@"後部座席[3]"]];
    
    acClimStsData = @[@[@"0",@"エアコン OFF[0]"],
                      @[@"1",@"エアコン ON(AUTO)[1]"],
                      @[@"2",@"エアコン ON/OFF(トグル切り替え)[2]"],
                      @[@"10",@"全席OFF[10]"],
                      @[@"11",@"急速冷房(AUTO,設定温度:MAX COOL)[11]"],
                      @[@"12",@"急速暖房(AUTO,設定温度:MAX HOT)[12]"],
                      @[@"13",@"おまかせ(AUTO,設定温度センター)[13]"]];
    
    acAcSetSeatData = @[@[@"0",@"全席一括[0]"]];
    
    acAcSetData = @[@[@"0",@"A/C OFF[0]"],
                     @[@"1",@"A/C ON[1]"],
                     @[@"10",@"A/C ON/OFFトグル[10]"],
                     @[@"11",@"A/C AUTO[11]"]];
    
    acModeSetSeatData = @[@[@"0",@"全席一括[0]"],
                          @[@"1",@"運転席[1]"],
                          @[@"2",@"助手席[2]"],
                          @[@"3",@"後部座席[3]"]];
    
    acModeSetData = @[@[@"1",@"VENT[1]"],
                      @[@"2",@"B/L[2]"],
                      @[@"3",@"HEAT[3]"],
                      @[@"4",@"H/D[4]"],
                      @[@"5",@"DEF[5]"],
                      @[@"10",@"MODE UP[10]"],
                      @[@"11",@"MODE DOWN[11]"],
                      @[@"12",@"MODE AUTO[12]"]];
    
    acFanUpDnSeatData = @[@[@"0",@"全席一括[0]"],
                          @[@"3",@"後部座席[3]"]];
    
    acFanUpDnData = @[@[@"1",@"風量1段階 UP[1]"],
                      @[@"2",@"風量2段階 UP[2]"],
                      @[@"11",@"風量1段階 DOWN[11]"],
                      @[@"12",@"風量2段階 DOWN[12]"]];
    
    acFanSetSeatData = @[@[@"0",@"全席一括[0]"],
                         @[@"3",@"後部座席[3]"]];
    
    acFanSetData = @[@[@"1",@"風量1[1]"],
                     @[@"2",@"風量2[2]"],
                     @[@"3",@"風量3[3]"],
                     @[@"4",@"風量4[4]"],
                     @[@"5",@"風量5[5]"],
                     @[@"6",@"風量6[6]"],
                     @[@"7",@"風量7[7]"],
                     @[@"10",@"風量 順次切り替え UP[10]"],
                     @[@"11",@"風量 順次切り替え DOWN[11]"],
                     @[@"12",@"風量 AUTO[12]"]];
    
    acTempUpDnSeatData = @[@[@"0",@"全席一括[0]"],
                           @[@"1",@"運転席[1]"],
                           @[@"2",@"助手席[2]"],
                           @[@"3",@"後部座席[3]"]];
    
    acTempUpDnData = @[@[@"1",@"設定温度 1STEP UP[1]"],
                       @[@"2",@"設定温度 2STEP UP[2]"],
                       @[@"3",@"設定温度 3STEP UP[3]"],
                       @[@"4",@"設定温度 4STEP UP[4]"],
                       @[@"5",@"設定温度 5STEP UP[5]"],
                       @[@"11",@"設定温度 1STEP DOWN[11]"],
                       @[@"12",@"設定温度 2STEP DOWN[12]"],
                       @[@"13",@"設定温度 3STEP DOWN[13]"],
                       @[@"14",@"設定温度 4STEP DOWN[14]"],
                       @[@"15",@"設定温度 5STEP DOWN[15]"]];
    
    acTempSetSeatData = @[@[@"0",@"全席一括[0]"],
                          @[@"1",@"運転席[1]"],
                          @[@"2",@"助手席[2]"],
                          @[@"3",@"後部座席[3]"]];
    
    acFrDefSetSeatData = @[@[@"0",@"全席一括[0]"]];
    
    acFrDefSetData = @[@[@"0",@"フロントDEF OFF[0]"],
                       @[@"1",@"フロントDEF ON[1]"],
                       @[@"10",@"フロントDEF ON/OFFトグル[10]"]];
    
    acRrDefSetSeatData = @[@[@"0",@"全席一括[0]"]];
    
    acRrDefSetData = @[@[@"0",@"リアDEF OFF[0]"],
                       @[@"1",@"リアDEF ON[1]"],
                       @[@"10",@"リアDEF ON/OFFトグル[10]"]];
    
    acSyncSetSeatData = @[@[@"0",@"全席一括[0]"]];
    
    acSyncSetData = @[@[@"0",@"左右一括制御(Singleモード)[0]"],
                      @[@"1",@"左右独立制御(Dualモード)[1]"],
                      @[@"10",@"DUALモード ON/OFFトグル[10]"]];
    
    acRecFrsSetSeatData = @[@[@"0",@"全席一括[0]"]];
    
    acRecFrsSetData = @[@[@"0",@"内気循環(RECモード)[0]"],
                        @[@"1",@"外気導入(FRSモード)[1]"],
                        @[@"10",@"内外気 ON/OFFトグル[10]"],
                        @[@"11",@"内外気AUTO[11]"]];
    
    acVentTempSetSeatData = @[@[@"1",@"運転席[1]"],
                              @[@"2",@"助手席[2]"]];
    
    acVentTempSetData = @[@[@"1",@"B/L1(Lo)[1]"],
                          @[@"2",@"B/L2[2]"],
                          @[@"3",@"B/L3(±0)[3]"],
                          @[@"4",@"B/L4[4]"],
                          @[@"5",@"B/L5(Hi)[5]"]];
    
    adSrcChgDirData = @[@[@"0",@"OFF[0]"],
                          @[@"1",@"FM[1]"],
                          @[@"2",@"AM[2]"],
                          @[@"3",@"SXM[3]"],
                          @[@"40",@"USB1[40]"],
                          @[@"41",@"USB2[41]"],
                          @[@"50",@"iPod1[50]"],
                          @[@"51",@"iPod2[51]"],
                          @[@"60",@"BT-Audio[60]"]];
    
    adSrcChgData = @[@[@"0",@"Next[0]"],
                     @[@"1",@"Previous[1]"]];
    
    adSkipUpDnData = @[@[@"0",@"Skip(track) up[0]"],
                       @[@"1",@"Skip(track) down[1]"]];
    
    adPlayData = @[@[@"0",@"Play[0]"],
                   @[@"1",@"Pause[1]"]];
    
    adFfRewData = @[@[@"0",@"FF Start[0]"],
                    @[@"1",@"REW Start[1]"],
                    @[@"2",@"FF/REW Stop[2]"]];
    
    adSeekData = @[@[@"0",@"Seek Up[0]"],
                   @[@"1",@"Seek Down[1]"],
                   @[@"2",@"Seek Stop[2]"]];
    
    adShuffleData = @[@[@"0",@"シャッフルモード1[0]"],
                      @[@"1",@"シャッフルモード2[1]"],
                      @[@"2",@"シャッフルモード3[2]"]];
    
    adRepeatData = @[@[@"0",@"リピートモード1[0]"],
                     @[@"1",@"リピートモード2[1]"],
                     @[@"2",@"リピートモード3[2]"]];
    
    adSetAudioVolData = @[@[@"0",@"Up[0]"],
                          @[@"1",@"Down[1]"]];
    
    adScanSrcData = @[@[@"0",@"AM[0]"],
                      @[@"1",@"FM[1]"]];
    
    adScanData = @[@[@"0",@"Scan開始[0]"],
                   @[@"1",@"Scan停止[1]"]];
    
    adTuneSrcData = @[@[@"0",@"AM[0]"],
                      @[@"1",@"FM[1]"]];
    
    adTuneData = @[@[@"0",@"Up[0]"],
                   @[@"1",@"Down[1]"],
                   @[@"2",@"Auto-Up[2]"],
                   @[@"3",@"Auto-Down[3]"],
                   @[@"4",@"Auto-Cancel[4]"]];
    
    setDispSrcData = @[@[@"0",@"グラフィック[0]"],
                      @[@"1",@"テレビ映像[1]"],
                      @[@"2",@"圧縮Video映像[2]"],
                      @[@"3",@"AUX-VIDEO映像[3]"],
                      @[@"4",@"HDMI映像[4]"],
                      @[@"5",@"DVD映像[5]"],
                      @[@"6",@"リアワイドカメラ映像[6]"],
                      @[@"7",@"MVC映像[7]"],
                      @[@"8",@"LaneWatch映像[8]"]];
    
    setDispKindData = @[@[@"0",@"明るさ[0]"],
                        @[@"1",@"コントラスト[1]"],
                        @[@"2",@"黒の濃さ[2]"],
                        @[@"3",@"色の濃さ[3]"],
                        @[@"4",@"色合い[4]"]];
    
    setDispData = @[@[@"0",@"Up[0]"],
                    @[@"1",@"Down[1]"]];
    
    setVolKindData = @[@[@"0",@"ガイダンス音量[0]"],
                       @[@"1",@"テキスト読み上げ音量[1]"],
                       @[@"2",@"音声認識音量[2]"],
                       @[@"10",@"通話音量：着信[10]"],
                       @[@"11",@"通話音量：受話[11]"],
                       @[@"12",@"通話音量：送話[12]"]];
    
    setVolData = @[@[@"0",@"Up[0]"],
                    @[@"1",@"Down[1]"]];
    
    setVolDirKindData = @[@[@"0",@"ガイダンス音量[0]"],
                          @[@"1",@"テキスト読み上げ音量[1]"]];
    
    setControlVolData = @[@[@"0",@"OFF[0]"],
                          @[@"1",@"1[1]"],
                          @[@"2",@"2[2]"],
                          @[@"3",@"3[3]"]];
    
    setTouchPanelSensitivityData = @[@[@"0",@"Low[0]"],
                                     @[@"1",@"High[1]"]];
    
    setToneKindData = @[@[@"0",@"BASS[0]"],
                        @[@"1",@"MIDRANGE[1]"],
                        @[@"2",@"TREBLE[2]"]];
    
    setToneData = @[@[@"0",@"Up[0]"],
                    @[@"1",@"Down[1]"]];
    
    setToneDirKindData = @[@[@"0",@"BASS[0]"],
                           @[@"1",@"MIDRANGE[1]"],
                           @[@"2",@"TREBLE[2]"]];
    
    setFaderBalanceData = @[@[@"0",@"Fader[0]"],
                            @[@"1",@"Balance[1]"]];
    
    setFaderBalanceUpDnData = @[@[@"0",@"0[0]"],
                                @[@"1",@"1[1]"]];
    
    setFaderBalanceDirData = @[@[@"0",@"Fader[0]"],
                               @[@"1",@"Balance[1]"]];
    
    setSubwooferData = @[@[@"0",@"Up[0]"],
                         @[@"1",@"Down[1]"]];
    
    setSVCData = @[@[@"0",@"OFF[0]"],
                   @[@"1",@"LOW[1]"],
                   @[@"2",@"MID[2]"],
                   @[@"3",@"HIGH[3]"]];
    
    notiPopupKindData = @[@[@"0",@"下部ポップアップ[0]"],
                          @[@"10",@"サイズ：小[10]"],
                          @[@"30",@"サイズ：大[30]"]];

    // Keyboard toolbar
    self.keyboardToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0,0,self.view.bounds.size.width, 38.0f)];
    self.keyboardToolbar.barStyle = UIBarStyleBlackTranslucent;
    
    UIBarButtonItem *spaceBarItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem *doneBarItem = [[UIBarButtonItem alloc] initWithTitle:@"決定" style:UIBarButtonItemStyleDone target:self action:@selector(resignKeyboard:)];
    [self.keyboardToolbar setItems:@[spaceBarItem, doneBarItem]];
    
    // [1]AirconClimateStatus Seat Picker View
    self.mAcClimStsSeatPicker = [[UIPickerView alloc] init];
    self.mAcClimStsSeatPicker.delegate = self;
    self.mAcClimStsSeatPicker.tag = ACCLIMSTS_SEAT_TAG;
    self.mAcClimStsSeatPicker.showsSelectionIndicator = YES;
    // [1]AirconClimateStatus Climate Picker View
    self.mAcClimStsPicker = [[UIPickerView alloc] init];
    self.mAcClimStsPicker.delegate = self;
    self.mAcClimStsPicker.tag = ACCLIMSTS_CLIM_TAG;
    self.mAcClimStsPicker.showsSelectionIndicator = YES;
    // [1]AirconClimateStatus Seat Text Feild
    self.mAcClimStsSeatText.delegate = self;
    self.mAcClimStsSeatText.tag = ACCLIMSTS_SEAT_TAG;
    self.mAcClimStsSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcClimStsSeatText.inputView = self.mAcClimStsSeatPicker;
    // [1]AirconClimateStatus Climate Text Feild
    self.mAcClimStsText.delegate = self;
    self.mAcClimStsText.tag = ACCLIMSTS_CLIM_TAG;
    self.mAcClimStsText.inputAccessoryView = self.keyboardToolbar;
    self.mAcClimStsText.inputView = self.mAcClimStsPicker;
    
    // [2]AirconACSSetting Seat Picker View
    self.mAcAcSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcAcSetSeatPicker.delegate = self;
    self.mAcAcSetSeatPicker.tag = ACACSET_SEAT_TAG;
    self.mAcAcSetSeatPicker.showsSelectionIndicator = YES;
    // [2]AirconACSSetting AC Status Picker View
    self.mAcAcSetPicker = [[UIPickerView alloc] init];
    self.mAcAcSetPicker.delegate = self;
    self.mAcAcSetPicker.tag = ACACSET_ACSTS_TAG;
    self.mAcAcSetPicker.showsSelectionIndicator = YES;
    // [2]AirconACSSetting Seat Text Feild
    self.mAcAcSetSeatText.delegate = self;
    self.mAcAcSetSeatText.tag = ACACSET_SEAT_TAG;
    self.mAcAcSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcAcSetSeatText.inputView = self.mAcAcSetSeatPicker;
    // [2]AirconACSSetting AC Status Text Feild
    self.mAcAcSetText.delegate = self;
    self.mAcAcSetText.tag = ACACSET_ACSTS_TAG;
    self.mAcAcSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcAcSetText.inputView = self.mAcAcSetPicker;
    
    // [3]AirconModeSetting Seat Picker View
    self.mAcModeSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcModeSetSeatPicker.delegate = self;
    self.mAcModeSetSeatPicker.tag = ACMODE_SEAT_TAG;
    self.mAcModeSetSeatPicker.showsSelectionIndicator = YES;
    // [3]AirconModeSetting Mode Picker View
    self.mAcModeSetPicker = [[UIPickerView alloc] init];
    self.mAcModeSetPicker.delegate = self;
    self.mAcModeSetPicker.tag = ACMODE_MODE_TAG;
    self.mAcModeSetPicker.showsSelectionIndicator = YES;
    // [3]AirconModeSetting Seat Text Field
    self.mAcModeSetSeatText.delegate = self;
    self.mAcModeSetSeatText.tag = ACMODE_SEAT_TAG;
    self.mAcModeSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcModeSetSeatText.inputView = self.mAcModeSetSeatPicker;
    // [3]AirconModeSetting Mode Text Field
    self.mAcModeSetText.delegate = self;
    self.mAcModeSetText.tag = ACMODE_MODE_TAG;
    self.mAcModeSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcModeSetText.inputView = self.mAcModeSetPicker;

    // [4]AirconFanUpDown Seat Picker View
    self.mAcFanUpDnSeatPicker = [[UIPickerView alloc] init];
    self.mAcFanUpDnSeatPicker.delegate = self;
    self.mAcFanUpDnSeatPicker.tag =ACFANUD_SEAT_TAG;
    self.mAcFanUpDnSeatPicker.showsSelectionIndicator = YES;
    // [4]AirconFanUpDown Fan Up/Down Picker View
    self.mAcFanUpDnPicker = [[UIPickerView alloc] init];
    self.mAcFanUpDnPicker.delegate = self;
    self.mAcFanUpDnPicker.tag = ACFANUD_FANUD_TAG;
    self.mAcFanUpDnPicker.showsSelectionIndicator = YES;
    // [4]AirconFanUpDown Seat Text Field
    self.mAcFanUpDnSeatText.delegate = self;
    self.mAcFanUpDnSeatText.tag = ACFANUD_SEAT_TAG;
    self.mAcFanUpDnSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcFanUpDnSeatText.inputView = self.mAcFanUpDnSeatPicker;
    // [4]AirconFanUpDown Fan Up/Down Text Field
    self.mAcFanUpDnText.delegate = self;
    self.mAcFanUpDnText.tag = ACFANUD_FANUD_TAG;
    self.mAcFanUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mAcFanUpDnText.inputView = self.mAcFanUpDnPicker;

    // [5]AirconFanSetting Seat Picker View
    self.mAcFanSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcFanSetSeatPicker.delegate = self;
    self.mAcFanSetSeatPicker.tag = ACFANSET_SEAT_TAG;
    self.mAcFanSetSeatPicker.showsSelectionIndicator = YES;
    // [5]AirconFanSetting Fan Value Picker View
    self.mAcFanSetPicker = [[UIPickerView alloc] init];
    self.mAcFanSetPicker.delegate = self;
    self.mAcFanSetPicker.tag = ACFANSET_FANVAL_TAG;
    self.mAcFanSetPicker.showsSelectionIndicator = YES;
    // [5]AirconFanSetting Seat Text Field
    self.mAcFanSetSeatText.delegate = self;
    self.mAcFanSetSeatText.tag = ACFANSET_SEAT_TAG;
    self.mAcFanSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcFanSetSeatText.inputView = self.mAcFanSetSeatPicker;
    // [5]AirconFanSetting Fan Value Text Field
    self.mAcFanSetText.delegate = self;
    self.mAcFanSetText.tag = ACFANSET_FANVAL_TAG;
    self.mAcFanSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcFanSetText.inputView = self.mAcFanSetPicker;
    
    // [6]AirconTempUpDown Seat Picker View
    self.mAcTempUpDnSeatPicker = [[UIPickerView alloc] init];
    self.mAcTempUpDnSeatPicker.delegate = self;
    self.mAcTempUpDnSeatPicker.tag = ACTMPUD_SEAT_TAG;
    self.mAcTempUpDnSeatPicker.showsSelectionIndicator = YES;
    // [6]AirconTempUpDown Temperature UP/DOWN Picker View
    self.mAcTempUpDnPicker = [[UIPickerView alloc] init];
    self.mAcTempUpDnPicker.delegate = self;
    self.mAcTempUpDnPicker.tag = ACTMPUD_TMPUD_TAG;
    self.mAcTempUpDnPicker.showsSelectionIndicator = YES;
    // [6]AirconTempUpDown Seat Text Field
    self.mAcTempUpDnSeatText.delegate = self;
    self.mAcTempUpDnSeatText.tag = ACTMPUD_SEAT_TAG;
    self.mAcTempUpDnSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcTempUpDnSeatText.inputView = self.mAcTempUpDnSeatPicker;
    // [6]AirconTempUpDown Temperature UP/DOWN Text Field
    self.mAcTempUpDnText.delegate = self;
    self.mAcTempUpDnText.tag = ACTMPUD_TMPUD_TAG;
    self.mAcTempUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mAcTempUpDnText.inputView = self.mAcTempUpDnPicker;

    // [7]AirconTempSetting Seat Picker View
    self.mAcTempSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcTempSetSeatPicker.delegate = self;
    self.mAcTempSetSeatPicker.tag = ACTMP_SEAT_TAG;
    self.mAcTempSetSeatPicker.showsSelectionIndicator = YES;
    // [7]AirconTempSetting Seat Text Field
    self.mAcTempSetSeatText.delegate = self;
    self.mAcTempSetSeatText.tag = ACTMP_SEAT_TAG;
    self.mAcTempSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcTempSetSeatText.inputView = self.mAcTempSetSeatPicker;
    // [7]AirconTempSetting Temperature Text Field
    self.mAcTempSetText.delegate = self;
    self.mAcTempSetText.tag = ACTMP_TMPSET_TAG;
    self.mAcTempSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcTempSetText.keyboardType = UIKeyboardTypeNumberPad;
    
    // [8]AirconFrDefSetting Seat Picker View
    self.mAcFrDefSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcFrDefSetSeatPicker.delegate = self;
    self.mAcFrDefSetSeatPicker.tag = ACFRDEF_SEAT_TAG;
    self.mAcFrDefSetSeatPicker.showsSelectionIndicator = YES;
    // [8]AirconFrDefSetting Fron DEF Status Picker View
    self.mAcFrDefSetPicker = [[UIPickerView alloc] init];
    self.mAcFrDefSetPicker.delegate = self;
    self.mAcFrDefSetPicker.tag = ACFRDEF_FRDEF_TAG;
    self.mAcFrDefSetPicker.showsSelectionIndicator = YES;
    // [8]AirconFrDefSetting Seat Text Field
    self.mAcFrDefSetSeatText.delegate = self;
    self.mAcFrDefSetSeatText.tag = ACFRDEF_SEAT_TAG;
    self.mAcFrDefSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcFrDefSetSeatText.inputView = self.mAcFrDefSetSeatPicker;
    // [8]AirconFrDefSetting Front DEF Status Text Field
    self.mAcFrDefSetText.delegate = self;
    self.mAcFrDefSetText.tag = ACFRDEF_FRDEF_TAG;
    self.mAcFrDefSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcFrDefSetText.inputView = self.mAcFrDefSetPicker;
    
    // [9]AirconRrDefSetting Seat Picker View
    self.mAcRrDefSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcRrDefSetSeatPicker.delegate = self;
    self.mAcRrDefSetSeatPicker.tag = ACRRDEF_SEAT_TAG;
    self.mAcRrDefSetSeatPicker.showsSelectionIndicator = YES;
    // [9]AirconRrDefSetting Rear DEF Status Picker View
    self.mAcRrDefSetPicker = [[UIPickerView alloc] init];
    self.mAcRrDefSetPicker.delegate = self;
    self.mAcRrDefSetPicker.tag = ACRRDEF_RRDEF_TAG;
    self.mAcRrDefSetPicker.showsSelectionIndicator = YES;
    // [9]AirconRrDefSetting Seat Text Field
    self.mAcRrDefSetSeatText.delegate = self;
    self.mAcRrDefSetSeatText.tag = ACRRDEF_SEAT_TAG;
    self.mAcRrDefSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcRrDefSetSeatText.inputView = self.mAcRrDefSetSeatPicker;
    // [9]AirconRrDefSetting Rear DEF Status Text Field
    self.mAcRrDefSetText.delegate = self;
    self.mAcRrDefSetText.tag = ACRRDEF_RRDEF_TAG;
    self.mAcRrDefSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcRrDefSetText.inputView = self.mAcRrDefSetPicker;
    
    // [10]AirconSyncSetting Seat Picker View
    self.mAcSyncSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcSyncSetSeatPicker.delegate = self;
    self.mAcSyncSetSeatPicker.tag = ACSYNC_SEAT_TAG;
    self.mAcSyncSetSeatPicker.showsSelectionIndicator = YES;
    // [10]AirconSyncSetting Sync Status Picker View
    self.mAcSyncSetPicker = [[UIPickerView alloc] init];
    self.mAcSyncSetPicker.delegate = self;
    self.mAcSyncSetPicker.tag = ACSYNC_SYNC_TAG;
    self.mAcSyncSetPicker.showsSelectionIndicator = YES;
    // [10]AirconSyncSetting Seat Text Field
    self.mAcSyncSetSeatText.delegate = self;
    self.mAcSyncSetSeatText.tag = ACSYNC_SEAT_TAG;
    self.mAcSyncSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcSyncSetSeatText.inputView = self.mAcSyncSetSeatPicker;
    // [10]AirconSyncSetting Sync Status Text Feild
    self.mAcSyncSetText.delegate = self;
    self.mAcSyncSetText.tag = ACSYNC_SYNC_TAG;
    self.mAcSyncSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcSyncSetText.inputView = self.mAcSyncSetPicker;
    
    // [11]AirconRecFrsSetting Seat Picker View
    self.mAcRecFrsSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcRecFrsSetSeatPicker.delegate = self;
    self.mAcRecFrsSetSeatPicker.tag = ACRECFRS_SEAT_TAG;
    self.mAcRecFrsSetSeatPicker.showsSelectionIndicator = YES;
    // [11]AirconRecFrsSetting REC/FRS Picker View
    self.mAcRecFrsSetPicker = [[UIPickerView alloc] init];
    self.mAcRecFrsSetPicker.delegate = self;
    self.mAcRecFrsSetPicker.tag = ACRECFRS_RECFRS_TAG;
    self.mAcRecFrsSetPicker.showsSelectionIndicator = YES;
    // [11]AirconRecFrsSetting Seat Text Field
    self.mAcRecFrsSetSeatText.delegate = self;
    self.mAcRecFrsSetSeatText.tag = ACRECFRS_SEAT_TAG;
    self.mAcRecFrsSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcRecFrsSetSeatText.inputView = self.mAcRecFrsSetSeatPicker;
    // [11]AirconRecFrsSetting REC/FRS Text Field
    self.mAcRecFrsSetText.delegate = self;
    self.mAcRecFrsSetText.tag = ACRECFRS_RECFRS_TAG;
    self.mAcRecFrsSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcRecFrsSetText.inputView = self.mAcRecFrsSetPicker;
    
    // [12]AirconVentTempSetting Seat Picker View
    self.mAcVentTempSetSeatPicker = [[UIPickerView alloc] init];
    self.mAcVentTempSetSeatPicker.delegate = self;
    self.mAcVentTempSetSeatPicker.tag = ACVNT_SEAT_TAG;
    self.mAcVentTempSetSeatPicker.showsSelectionIndicator = YES;
    // [12]AirconVentTempSetting Vent Temperature Picker View
    self.mAcVentTempSetPicker = [[UIPickerView alloc] init];
    self.mAcVentTempSetPicker.delegate = self;
    self.mAcVentTempSetPicker.tag = ACVNT_VNTTMP_TAG;
    self.mAcVentTempSetPicker.showsSelectionIndicator = YES;
    // [12]AirconVentTempSetting Seat Text Field
    self.mAcVentTempSetSeatText.delegate = self;
    self.mAcVentTempSetSeatText.tag = ACVNT_SEAT_TAG;
    self.mAcVentTempSetSeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAcVentTempSetSeatText.inputView = self.mAcVentTempSetSeatPicker;
    // [12]AirconVentTempSetting Vent Temperature Text Field
    self.mAcVentTempSetText.delegate = self;
    self.mAcVentTempSetText.tag = ACVNT_VNTTMP_TAG;
    self.mAcVentTempSetText.inputAccessoryView = self.keyboardToolbar;
    self.mAcVentTempSetText.inputView = self.mAcVentTempSetPicker;

    // [13]AudioSourceChangeDirect Picker View
    self.mAudioSourceChangeDirectPicker = [[UIPickerView alloc] init];
    self.mAudioSourceChangeDirectPicker.delegate = self;
    self.mAudioSourceChangeDirectPicker.tag = ADSRCDIR_SRC_TAG;
    self.mAudioSourceChangeDirectPicker.showsSelectionIndicator = YES;
    // [13]AudioSourceChangeDirect Texit Field
    self.mAudioSourceChangeDirectText.delegate = self;
    self.mAudioSourceChangeDirectText.tag = ADSRCDIR_SRC_TAG;
    self.mAudioSourceChangeDirectText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSourceChangeDirectText.inputView = self.mAudioSourceChangeDirectPicker;
    
    // [14]AudioSourceChange Picker View
    self.mAudioSourceChangePicker = [[UIPickerView alloc] init];
    self.mAudioSourceChangePicker.delegate = self;
    self.mAudioSourceChangePicker.tag = ADSRC_NEXT_TAG;
    self.mAudioSourceChangePicker.showsSelectionIndicator = YES;
    // [14]AudioSourceChange Text Field
    self.mAudioSourceChangeText.delegate = self;
    self.mAudioSourceChangeText.tag = ADSRC_NEXT_TAG;
    self.mAudioSourceChangeText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSourceChangeText.inputView = self.mAudioSourceChangePicker;
    
    // [15]AudioSkipUpDown Picker View
    self.mAudioSkipUpDownPicker = [[UIPickerView alloc] init];
    self.mAudioSkipUpDownPicker.delegate = self;
    self.mAudioSkipUpDownPicker.tag = ADSKIPUPDN_SKIP_TAG;
    self.mAudioSkipUpDownPicker.showsSelectionIndicator = YES;
    // [15]AudioSkipUpDown Text Field
    self.mAudioSkipUpDownText.delegate = self;
    self.mAudioSkipUpDownText.tag = ADSKIPUPDN_SKIP_TAG;
    self.mAudioSkipUpDownText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSkipUpDownText.inputView = self.mAudioSkipUpDownPicker;
    
    // [16]AudioPlay Picker View
    self.mAudioPlayPicker = [[UIPickerView alloc] init];
    self.mAudioPlayPicker.delegate = self;
    self.mAudioPlayPicker.tag = ADPLAY_PLAY_TAG;
    self.mAudioPlayPicker.showsSelectionIndicator = YES;
    // [16]AudioPlay Text Field
    self.mAudioPlayText.delegate = self;
    self.mAudioPlayText.tag = ADPLAY_PLAY_TAG;
    self.mAudioPlayText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioPlayText.inputView = self.mAudioPlayPicker;
    
    // [17]AudioFfRew Picker View
    self.mAudioFfRewPicker = [[UIPickerView alloc] init];
    self.mAudioFfRewPicker.delegate = self;
    self.mAudioFfRewPicker.tag = ADFFREW_FRREW_TAG;
    self.mAudioFfRewPicker.showsSelectionIndicator = YES;
    // [17]AudioFfRew Text Field
    self.mAudioFfRewText.delegate = self;
    self.mAudioFfRewText.tag = ADFFREW_FRREW_TAG;
    self.mAudioFfRewText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioFfRewText.inputView = self.mAudioFfRewPicker;
    
    // [18]AudioSeek Picker View
    self.mAudioSeekPicker = [[UIPickerView alloc] init];
    self.mAudioSeekPicker.delegate = self;
    self.mAudioSeekPicker.tag = ADSEEK_SEEK_TAG;
    self.mAudioSeekPicker.showsSelectionIndicator = YES;
    // [18]AudioSeek Text Field
    self.mAudioSeekText.delegate = self;
    self.mAudioSeekText.tag = ADSEEK_SEEK_TAG;
    self.mAudioSeekText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSeekText.inputView = self.mAudioSeekPicker;

    // [19]AudioShuffle Picker View
    self.mAudioShufflePicker = [[UIPickerView alloc] init];
    self.mAudioShufflePicker.delegate = self;
    self.mAudioShufflePicker.tag = ADSHUFFLE_MODE_TAG;
    self.mAudioShufflePicker.showsSelectionIndicator = YES;
    // [19]AudioShuffle Text Field
    self.mAudioShuffleText.delegate = self;
    self.mAudioShuffleText.tag = ADSHUFFLE_MODE_TAG;
    self.mAudioShuffleText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioShuffleText.inputView = self.mAudioShufflePicker;

    // [20]AudioRepeat Picker View
    self.mAudioRepeatPicker = [[UIPickerView alloc] init];
    self.mAudioRepeatPicker.delegate = self;
    self.mAudioRepeatPicker.tag = ADREPEAT_MODE_TAG;
    self.mAudioRepeatPicker.showsSelectionIndicator = YES;
    // [20]AudioRepeat Text Field
    self.mAudioRepeatText.delegate = self;
    self.mAudioRepeatText.tag = ADREPEAT_MODE_TAG;
    self.mAudioRepeatText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioRepeatText.inputView = self.mAudioRepeatPicker;

    // [26]AudioSetChannelSXM Text Field
    self.mAudioSetChannelSXMText.delegate = self;
    self.mAudioSetChannelSXMText.tag = ADCHSXM_CHNO_TAG;
    self.mAudioSetChannelSXMText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSetChannelSXMText.keyboardType = UIKeyboardTypeNumberPad;

    // [27]AudioSetAudioVol Picker View
    self.mAudioSetAudioVolPicker = [[UIPickerView alloc] init];
    self.mAudioSetAudioVolPicker.delegate = self;
    self.mAudioSetAudioVolPicker.tag = ADVOL_UPDN_TAG;
    self.mAudioSetAudioVolPicker.showsSelectionIndicator = YES;
    // [27]AudioSetAudioVol Text Field
    self.mAudioSetAudioVolText.delegate = self;
    self.mAudioSetAudioVolText.tag = ADVOL_UPDN_TAG;
    self.mAudioSetAudioVolText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSetAudioVolText.inputView = self.mAudioSetAudioVolPicker;

    // [28]AudioSetAudioVolDirect Text Field
    self.mAudioSetAudioVolDirectText.delegate = self;
    self.mAudioSetAudioVolDirectText.tag = ADVOLDIR_VOL_TAG;
    self.mAudioSetAudioVolDirectText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioSetAudioVolDirectText.keyboardType = UIKeyboardTypeNumberPad;

    // [29]AudioScan source Picker View
    self.mAudioScanSourcePicker = [[UIPickerView alloc] init];
    self.mAudioScanSourcePicker.delegate = self;
    self.mAudioScanSourcePicker.tag = ADSCAN_SRC_TAG;
    self.mAudioScanSourcePicker.showsSelectionIndicator = YES;
    // [29]AudioScan up/down Picker View
    self.mAudioScanUpDnPicker = [[UIPickerView alloc] init];
    self.mAudioScanUpDnPicker.delegate = self;
    self.mAudioScanUpDnPicker.tag = ADSCAN_SCAN_TAG;
    self.mAudioScanUpDnPicker.showsSelectionIndicator = YES;
    // [29]AudioScan source Text Field
    self.mAudioScanSourceText.delegate = self;
    self.mAudioScanSourceText.tag = ADSCAN_SRC_TAG;
    self.mAudioScanSourceText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioScanSourceText.inputView = self.mAudioScanSourcePicker;
    // [29]AudioScan up/dwon Text Field
    self.mAudioScanUpDnText.delegate = self;
    self.mAudioScanUpDnText.tag = ADSCAN_SCAN_TAG;
    self.mAudioScanUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioScanUpDnText.inputView = self.mAudioScanUpDnPicker;

    // [30]AudioTune source Text Field source Picker View
    self.mAudioTuneSourcePicker = [[UIPickerView alloc] init];
    self.mAudioTuneSourcePicker.delegate = self;
    self.mAudioTuneSourcePicker.tag = ADTUNE_SRC_TAG;
    self.mAudioTuneSourcePicker.showsSelectionIndicator = YES;
    // [30]AudioTune source Text Field up/down Picker View
    self.mAudioTuneUpDnPicker = [[UIPickerView alloc] init];
    self.mAudioTuneUpDnPicker.delegate = self;
    self.mAudioTuneUpDnPicker.tag = ADTUNE_UPDN_TAG;
    self.mAudioTuneUpDnPicker.showsSelectionIndicator = YES;
    // [30]AudioTune source Text Field
    self.mAudioTuneSourceText.delegate = self;
    self.mAudioTuneSourceText.tag = ADTUNE_SRC_TAG;
    self.mAudioTuneSourceText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioTuneSourceText.inputView = self.mAudioTuneSourcePicker;
    // [30]AudioTune up/down Text Field
    self.mAudioTuneUpDnText.delegate = self;
    self.mAudioTuneUpDnText.tag = ADTUNE_UPDN_TAG;
    self.mAudioTuneUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mAudioTuneUpDnText.inputView = self.mAudioTuneUpDnPicker;

    // [33]SettingDisplay source Picker View
    self.mSettingDisplaySrcPicker = [[UIPickerView alloc] init];
    self.mSettingDisplaySrcPicker.delegate = self;
    self.mSettingDisplaySrcPicker.tag = SETDISP_SRC_TAG;
    self.mSettingDisplaySrcPicker.showsSelectionIndicator = YES;
    // [33]SettingDisplay kind Picker View
    self.mSettingDisplayKindPicker = [[UIPickerView alloc] init];
    self.mSettingDisplayKindPicker.delegate = self;
    self.mSettingDisplayKindPicker.tag = SETDISP_KIND_TAG;
    self.mSettingDisplayKindPicker.showsSelectionIndicator = YES;
    // [33]SettingDisplay up/down Picker View
    self.mSettingDisplayUpDnPicker = [[UIPickerView alloc] init];
    self.mSettingDisplayUpDnPicker.delegate = self;
    self.mSettingDisplayUpDnPicker.tag = SETDISP_UPDN_TAG;
    self.mSettingDisplayUpDnPicker.showsSelectionIndicator = YES;
     // [33]SettingDisplay source Text Field
    self.mSettingDisplaySrcText.delegate = self;
    self.mSettingDisplaySrcText.tag = SETDISP_SRC_TAG;
    self.mSettingDisplaySrcText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingDisplaySrcText.inputView = self.mSettingDisplaySrcPicker;
    // [33]SettingDisplay kind Text Field
    self.mSettingDisplayKindText.delegate = self;
    self.mSettingDisplayKindText.tag = SETDISP_KIND_TAG;
    self.mSettingDisplayKindText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingDisplayKindText.inputView = self.mSettingDisplayKindPicker;
    // [33]SettingDisplay up/down Text Field
    self.mSettingDisplayUpDnText.delegate = self;
    self.mSettingDisplayUpDnText.tag = SETDISP_UPDN_TAG;
    self.mSettingDisplayUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingDisplayUpDnText.inputView = self.mSettingDisplayUpDnPicker;

    // [34]SettingVolume kind Picker View
    self.mSettingVolumeKindPicker = [[UIPickerView alloc] init];
    self.mSettingVolumeKindPicker.delegate = self;
    self.mSettingVolumeKindPicker.tag = SETVOL_KIND_TAG;
    self.mSettingVolumeKindPicker.showsSelectionIndicator = YES;
    // [34]SettingVolume up/down Picker View
    self.mSettingVolumeUpDnPicker = [[UIPickerView alloc] init];
    self.mSettingVolumeUpDnPicker.delegate = self;
    self.mSettingVolumeUpDnPicker.tag = SETVOL_UPDN_TAG;
    self.mSettingVolumeUpDnPicker.showsSelectionIndicator = YES;
    // [34]SettingVolume kind Text Field
    self.mSettingVolumeKindText.delegate = self;
    self.mSettingVolumeKindText.tag = SETVOL_KIND_TAG;
    self.mSettingVolumeKindText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingVolumeKindText.inputView = self.mSettingVolumeKindPicker;
    // [34]SettingVolume up/down Text Field
    self.mSettingVolumeUpDnText.delegate = self;
    self.mSettingVolumeUpDnText.tag = SETVOL_UPDN_TAG;
    self.mSettingVolumeUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingVolumeUpDnText.inputView = self.mSettingVolumeUpDnPicker;

    // [35]SettingVolumeDirect kind Picker View
    self.mSettingVolumeDirectKindPicker = [[UIPickerView alloc] init];
    self.mSettingVolumeDirectKindPicker.delegate = self;
    self.mSettingVolumeDirectKindPicker.tag = SETVOLDIR_KIND_TAG;
    self.mSettingVolumeDirectKindPicker.showsSelectionIndicator = YES;
    // [35]SettingVolumeDirect kind Text Field
    self.mSettingVolumeDirectKindText.delegate = self;
    self.mSettingVolumeDirectKindText.tag = SETVOLDIR_KIND_TAG;
    self.mSettingVolumeDirectKindText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingVolumeDirectKindText.inputView = self.mSettingVolumeDirectKindPicker;
    // [35]SettingVolumeDirect volume Text Field
    self.mSettingVolumeDirectVolText.delegate = self;
    self.mSettingVolumeDirectVolText.tag = SETVOLDIR_VOL_TAG;
    self.mSettingVolumeDirectVolText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingVolumeDirectVolText.keyboardType = UIKeyboardTypeNumberPad;

    // [36]SettingControlVol Picker View
    self.mSettingControlVolPicker = [[UIPickerView alloc] init];
    self.mSettingControlVolPicker.delegate = self;
    self.mSettingControlVolPicker.tag = SETCTRLVOL_VOL_TAG;
    self.mSettingControlVolPicker.showsSelectionIndicator = YES;
    // [36]SettingControlVol Text Field
    self.mSettingControlVolText.delegate = self;
    self.mSettingControlVolText.tag = SETCTRLVOL_VOL_TAG;
    self.mSettingControlVolText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingControlVolText.inputView = self.mSettingControlVolPicker;

    // [37]SettingTouchPanelSensitivity Picker View
    self.mSettingTouchPanelSensitivityPicker = [[UIPickerView alloc] init];
    self.mSettingTouchPanelSensitivityPicker.delegate = self;
    self.mSettingTouchPanelSensitivityPicker.tag = SETTCHPNL_SNSTVTY_TAG;
    self.mSettingTouchPanelSensitivityPicker.showsSelectionIndicator = YES;
    // [37]SettingTouchPanelSensitivity Text Field
    self.mSettingTouchPanelSensitivityText.delegate = self;
    self.mSettingTouchPanelSensitivityText.tag = SETTCHPNL_SNSTVTY_TAG;
    self.mSettingTouchPanelSensitivityText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingTouchPanelSensitivityText.inputView = self.mSettingTouchPanelSensitivityPicker;

    // [38]SettingTone kind Picker View
    self.mSettingToneKindPicker = [[UIPickerView alloc] init];
    self.mSettingToneKindPicker.delegate = self;
    self.mSettingToneKindPicker.tag = SETTONE_KIND_TAG;
    self.mSettingToneKindPicker.showsSelectionIndicator = YES;
    // [38]SettingTone up/down Picker View
    self.mSettingToneUpDnPicker = [[UIPickerView alloc] init];
    self.mSettingToneUpDnPicker.delegate = self;
    self.mSettingToneUpDnPicker.tag = SETTONE_UPDN_TAG;
    self.mSettingToneUpDnPicker.showsSelectionIndicator = YES;
    // [38]SettingTone kind Text Field
    self.mSettingToneKindText.delegate = self;
    self.mSettingToneKindText.tag = SETTONE_KIND_TAG;
    self.mSettingToneKindText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingToneKindText.inputView = self.mSettingToneKindPicker;
    // [38]SettingTone up/down Text Field
    self.mSettingToneUpDnText.delegate = self;
    self.mSettingToneUpDnText.tag = SETTONE_UPDN_TAG;
    self.mSettingToneUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingToneUpDnText.inputView = self.mSettingToneUpDnPicker;

    // [39]SettingToneDirect kind Picker View
    self.mSettingToneDirectKindPicker = [[UIPickerView alloc] init];
    self.mSettingToneDirectKindPicker.delegate = self;
    self.mSettingToneDirectKindPicker.tag = SETTONEDIR_KIND_TAG;
    self.mSettingToneDirectKindPicker.showsSelectionIndicator = YES;
    // [39]SettingToneDirect kind Text Field
    self.mSettingToneDirectKindText.delegate = self;
    self.mSettingToneDirectKindText.tag = SETTONEDIR_KIND_TAG;
    self.mSettingToneDirectKindText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingToneDirectKindText.inputView = self.mSettingToneDirectKindPicker;
    // [39]SettingToneDirect set Text Field
    self.mSettingToneDirectSetText.delegate = self;
    self.mSettingToneDirectSetText.tag = SETTONEDIR_SET_TAG;
    self.mSettingToneDirectSetText.inputAccessoryView = self.keyboardToolbar;
//    self.mSettingToneDirectSetText.keyboardType = UIKeyboardTypeNumbersAndPunctuation;

    // [40]SettingFaderBalance Picker View
    self.mSettingFaderBalancePicker = [[UIPickerView alloc] init];
    self.mSettingFaderBalancePicker.delegate = self;
    self.mSettingFaderBalancePicker.tag = SETFADER_BALANCE_TAG;
    self.mSettingFaderBalancePicker.showsSelectionIndicator = YES;
    // [40]SettingFaderBalance up/down Picker View
    self.mSettingFaderBalanceUpDnPicker = [[UIPickerView alloc] init];
    self.mSettingFaderBalanceUpDnPicker.delegate = self;
    self.mSettingFaderBalanceUpDnPicker.tag = SETFADER_UPDN_TAG;
    self.mSettingFaderBalanceUpDnPicker.showsSelectionIndicator = YES;
    // [40]SettingFaderBalance Text Field
    self.mSettingFaderBalanceText.delegate = self;
    self.mSettingFaderBalanceText.tag = SETFADER_BALANCE_TAG;
    self.mSettingFaderBalanceText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingFaderBalanceText.inputView = self.mSettingFaderBalancePicker;
    // [40]SettingFaderBalance up/down Text Field
    self.mSettingFaderBalanceUpDnText.delegate = self;
    self.mSettingFaderBalanceUpDnText.tag = SETFADER_UPDN_TAG;
    self.mSettingFaderBalanceUpDnText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingFaderBalanceUpDnText.inputView = self.mSettingFaderBalanceUpDnPicker;

    // [41]SettingFaderBalanceDirect Picker View
    self.mSettingFaderBalanceDirectPicker = [[UIPickerView alloc] init];
    self.mSettingFaderBalanceDirectPicker.delegate = self;
    self.mSettingFaderBalanceDirectPicker.tag = SETFADERDIR_BALANCE_TAG;
    self.mSettingFaderBalanceDirectPicker.showsSelectionIndicator = YES;
    // [41]SettingFaderBalanceDirect Text Field
    self.mSettingFaderBalanceDirectText.delegate = self;
    self.mSettingFaderBalanceDirectText.tag = SETFADERDIR_BALANCE_TAG;
    self.mSettingFaderBalanceDirectText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingFaderBalanceDirectText.inputView = self.mSettingFaderBalanceDirectPicker;
    // [41]SettingFaderBalanceDirect set Text Field
    self.mSettingFaderBalanceDirectSetText.delegate = self;
    self.mSettingFaderBalanceDirectSetText.tag = SETFADERDIR_SET_TAG;
    self.mSettingFaderBalanceDirectSetText.inputAccessoryView = self.keyboardToolbar;
//    self.mSettingFaderBalanceDirectSetText.keyboardType = UIKeyboardTypeNumbersAndPunctuation;

    // [42]SettingSubwoofer Picker View
    self.mSettingSubwooferPicker = [[UIPickerView alloc] init];
    self.mSettingSubwooferPicker.delegate = self;
    self.mSettingSubwooferPicker.tag = SETSUBWOOFER_UPDN_TAG;
    self.mSettingSubwooferPicker.showsSelectionIndicator = YES;
    // [42]SettingSubwoofer Text Field
    self.mSettingSubwooferText.delegate = self;
    self.mSettingSubwooferText.tag = SETSUBWOOFER_UPDN_TAG;
    self.mSettingSubwooferText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingSubwooferText.inputView = self.mSettingSubwooferPicker;

    // [43]SettingSubwooferDirect Text Field
    self.mSettingSubwooferDirectText.delegate = self;
    self.mSettingSubwooferDirectText.tag = SETSUBWOOFERDIR_SET_TAG;
    self.mSettingSubwooferDirectText.inputAccessoryView = self.keyboardToolbar;
//    self.mSettingSubwooferDirectText.keyboardType = UIKeyboardTypeNumbersAndPunctuation;

    // [44]SettingSVC Picker View
    self.mSettingSVCPicker = [[UIPickerView alloc] init];
    self.mSettingSVCPicker.delegate = self;
    self.mSettingSVCPicker.tag = SETSVC_SET_TAG;
    self.mSettingSVCPicker.showsSelectionIndicator = YES;
    // [44]SettingSVC Text Field
    self.mSettingSVCText.delegate = self;
    self.mSettingSVCText.tag = SETSVC_SET_TAG;
    self.mSettingSVCText.inputAccessoryView = self.keyboardToolbar;
    self.mSettingSVCText.inputView = self.mSettingSVCPicker;

    // [45]NaviDestinationLatLon latitude Text Field
    self.mNaviDestinationLatitudeText.delegate = self;
    self.mNaviDestinationLatitudeText.tag = NAVIDST_LAT_TAG;
    self.mNaviDestinationLatitudeText.inputAccessoryView = self.keyboardToolbar;
    self.mNaviDestinationLatitudeText.text = @"28.385233";
    // [45]NaviDestinationLatLon longitude Text Field
    self.mNaviDestinationLongitudeText.delegate = self;
    self.mNaviDestinationLongitudeText.tag = NAVIDST_LON_TAG;
    self.mNaviDestinationLongitudeText.inputAccessoryView = self.keyboardToolbar;
    self.mNaviDestinationLongitudeText.text = @"-81.566068";
    // [45]NaviDestinationLatLon name Text View
    self.mNaviDestinationNameTextView.delegate = self;
    self.mNaviDestinationNameTextView.tag = NAVIDST_NAME_TAG;
    self.mNaviDestinationNameTextView.inputAccessoryView = self.keyboardToolbar;
    self.mNaviDestinationNameTextView.text = @"Walt Disney";
    
    // [46]NotificationPopup kind Picker View
    self.mNotificationPopupKindPicker = [[UIPickerView alloc] init];
    self.mNotificationPopupKindPicker.delegate = self;
    self.mNotificationPopupKindPicker.tag = NOTIPOP_KIND_TAG;
    self.mNotificationPopupKindPicker.showsSelectionIndicator = YES;
    // [46]NotificationPopup kind Text Field
    self.mNotificationPopupKindText.delegate = self;
    self.mNotificationPopupKindText.tag = NOTIPOP_KIND_TAG;
    self.mNotificationPopupKindText.inputAccessoryView = self.keyboardToolbar;
    self.mNotificationPopupKindText.inputView = self.mNotificationPopupKindPicker;
    // [46]NotificationPopup wording Text View
    self.mNotificationPopupWordTextView.delegate = self;
    self.mNotificationPopupWordTextView.tag = NOTIPOP_WORD_TAG;
    self.mNotificationPopupWordTextView.inputAccessoryView = self.keyboardToolbar;
    
#ifdef __DEBUG_ON__
    dbgCurrentAudioData = @[
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":0,\"volume\":10}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":1,\"volume\":11,\"strdata1\":\"108.0MHz\",\"strdata2\":\"放送局１\",\"strdata3\":\"タイプ１\",\"strdata4\":\"タイトル１\",\"strdata5\":\"アーティスト１\",\"strdata6\":\"放送局RBDS\",\"strdata7\":\"ロック\"}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":2,\"volume\":12,\"strdata1\":\"118.0MHz\",\"strdata2\":\"放送局２\",\"strdata3\":\"タイプ２\",\"strdata4\":\"タイトル２\",\"strdata5\":\"アーティスト２\"}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":3,\"volume\":13,\"strdata1\":\"128.0MHz\",\"strdata2\":\"放送局３\",\"strdata3\":\"タイプ３\",\"strdata4\":\"タイトル３\",\"strdata5\":\"アーティスト３\",\"numdata1\":100}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":40,\"shufflemode\":0,\"repeatmode\":0,\"volume\":14,\"strdata1\":\"タイトル１\",\"strdata2\":\"アーティスト１\",\"strdata3\":\"アルバム１\",\"strdata4\":\"プレイリスト１\",\"strdata5\":\"フォルダ１\",\"strdata6\":\"ファイル１\",\"numdata1\":30,\"numdata2\":330}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":50,\"shufflemode\":1,\"repeatmode\":1,\"volume\":15,\"strdata1\":\"タイトル１\",\"strdata2\":\"アーティスト１\",\"strdata3\":\"アルバム１\",\"strdata4\":\"Podcast1\",\"strdata5\":\"Episode title1\",\"strdata6\":\"20170628\",\"strdata7\":\"Audiobook name\",\"strdata8\":\"Chapter title\",\"strdata9\":\"Station name\",\"strdata10\":\"Couse or Collection name\",\"strdata11\":\"Content title\",\"strdata12\":\"Author name\",\"numdata1\":30,\"numdata2\":330}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":60,\"shufflemode\":1,\"repeatmode\":1,\"volume\":16,\"strdata1\":\"Album name\",\"strdata2\":\"Song title\",\"strdata3\":\"Artist name\"}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":1,\"source\":-1,\"volume\":17}}",
    @"{\"result\":\"Success\",\"code\":0,\"current_audio\":{\"status\":0}}"
    ];
#endif
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
//    return @"EAP sample app (Objective-C version)";
    return @"sampleapplication";
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 30;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    UILabel *label1 = (UILabel *)[currentCell viewWithTag:1];
    UILabel *label3 = (UILabel *)[currentCell viewWithTag:3];

    CGSize maxSize = CGSizeMake(76, CGFLOAT_MAX);
    NSDictionary *attr = @{NSFontAttributeName: [UIFont systemFontOfSize:12.0]};
    
    CGSize modifiedSize1 = [label1.text boundingRectWithSize:maxSize
                                                    options:NSStringDrawingUsesLineFragmentOrigin
                                                 attributes:attr
                                                    context:nil
                            
                           ].size;
    CGSize modifiedSize3 = [label3.text boundingRectWithSize:maxSize
                                                    options:NSStringDrawingUsesLineFragmentOrigin
                                                 attributes:attr
                                                    context:nil
                           ].size;
    
    return MAX(modifiedSize1.height, MAX(modifiedSize3.height, 30));
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [statusDic count] + 1;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if( indexPath.row == 0 ) {
        NSLog(@"selected label = vehicleInfo");
    }
    else {
        NSArray *tblAllKeys = [statusDic allKeys];
        NSArray *sortedKeyInfo = [tblAllKeys sortedArrayUsingSelector:@selector(compare:)];
        NSLog(@"selected label = %@", sortedKeyInfo[indexPath.row - 1]);
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *Identifier = @"tableCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:Identifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:Identifier];
    }
    NSLog(@"cell = %@", cell);
    
    UILabel *label1 = (UILabel *)[cell viewWithTag:1];
    UILabel *label2 = (UILabel *)[cell viewWithTag:2];
    UILabel *label3 = (UILabel *)[cell viewWithTag:3];
    
    label1.numberOfLines = 0;
    label2.numberOfLines = 0;
    label3.numberOfLines = 0;
    if (indexPath.row == 0) {
        label1.text = @"vehicleInfo";
        label2.text = @"status";
        label3.text = @"data";
    }
    else {
        NSArray *tblAllKeys = [statusDic allKeys];
        NSArray *sortedKeyInfo = [tblAllKeys sortedArrayUsingSelector:@selector(compare:)];
        NSString *key = sortedKeyInfo[indexPath.row - 1];
        
        label1.text = key;
        label2.text = [[NSString alloc] initWithFormat:@"%@", statusDic[key]];
        id convSpec = convSpecInfo[key];
        if ( convSpec == nil ) {
            /* デフォルト動作、整数型は本ルートが適用される */
            label3.text = [[NSString alloc] initWithFormat:@"%@", dataDic[key]];
        }
        else if ( [convSpec isEqualToString:CNV_ID_STRING]){
            label3.text = [[NSString alloc] initWithFormat:@"%@", dataDic[key]];
        }
        else if ( ([convSpec isEqualToString:CNV_ID_DECIMAL01]) ||
                ([convSpec isEqualToString:CNV_ID_DECIMAL02]) ||
                ([convSpec isEqualToString:CNV_ID_DECIMAL03]) ||
                ([convSpec isEqualToString:CNV_ID_DECIMAL04]) ||
                ([convSpec isEqualToString:CNV_ID_DECIMAL06]) ||
                ([convSpec isEqualToString:CNV_ID_DECIMAL07]) ){
            double isData = (double)[dataDic[key] doubleValue];
            double lower = (isData - (int)isData);
            if ((isData == 0) || (lower == 0)) {
                label3.text = [[NSString alloc]initWithFormat:@"%g", (double)isData];
            }
            else {
                label3.text = [[NSString alloc]initWithFormat:convSpec, (double)isData];
            }
        }
    }
    currentCell = cell;
    return cell;
}

- (IBAction)mAutoOnOffButton:(id)sender {
    if ([mAutoOnOffButton.currentTitle isEqual:  @"AUTO OFF"]) {
        [mAutoOnOffButton setTitle:@"AUTO ON" forState:UIControlStateNormal];
        if ([libEAPSample notifyVehicleDataStart:ACCESSKEY] != 0) {
            NSLog(@"notifyVehicleDataStart Failed");
        }
        else {
            NSLog(@"notifyVehicleDataStart Success");
        }
    }
    else {
        [mAutoOnOffButton setTitle:@"AUTO OFF" forState:UIControlStateNormal];
        if ([libEAPSample notifyVehicleDataStop] != 0) {
            NSLog(@"notifyVehicleDataStop Failed");
        }
        else {
            NSLog(@"notifyVehicleDataStop Success");
        }
    }
    [self invokeSystemSound];
}

- (IBAction)mManualButton:(id)sender {
    NSString *resultString_ER1 = (NSString *)[libEAPSample getVehicleData:@"DUMMY" accessKey:ACCESSKEY];
    [logInfo write_appsend:@"request" :resultString_ER1];

    NSString *resultString_ER2 = (NSString *)[libEAPSample getVehicleData:@"ACCELPEDAL" accessKey:@"DUMMYKEY"];
    [logInfo write_appsend:@"request" :resultString_ER2];
    
    NSArray *arrayMap = @[@"ACCELPEDAL", @"ENGINERPM", @"ENGINESTAT", @"ENGOILLIFE", @"SHIFT", @"CRUISE", @"METERSPEED", @"TMSPEED",
                  @"ODOMETER",@"TRAVELLEDDIST", @"FUEL", @"FUELECONOMY", @"TRIP", @"ESTIMATEDRANGE", @"STEER", @"BRAKE",
                  @"PARKINGBRAKE", @"TIREPRESSURE", @"VSA", @"WHEELSPEED", @"VEHICLEMOVE", @"VIN", @"BATVOLTAGE", @"IG",
                  @"ILLSTAT", @"BACKLIGHTSTAT", @"TURNSIGNAL", @"HEADLIGHT", @"HAZARD", @"OUTSIDETEMP", @"ACSETTEMP",
                  @"ACCURRENTTEMP", @"RAIN", @"WIPER", @"WINDOWCLOSE", @"PASSENGEREXISTENCE",
                  @"TIMESTAMP", @"HEADUNITINFO", @"CARDIRECTION", @"CARCONDITION", @"SUNROOF", @"DOORTYPE", @"WINDOWSTATUS",
                  @"NAVIROADATTR", @"NAVIONROAD", @"MODE", @"SEATBELT", @"ACFANSPEED", @"ACMODE", @"ACSYNC", @"ACRECFRE",
                  @"ACAC", @"ACDEFRR", @"ACDEFFR", @"ACVARIATION", @"ACLHRH", @"ACTEMPSTEP", @"ACMODERR", @"ACFANSPEEDRR",
                  @"ACSETTEMPRR", @"CLUTCHSW", @"ENGMANVSENSV", @"BRAKESW", @"VEHICLESTJUDGE", @"CLUTCHSWSIL", @"ATGEARPOS",
                  @"SMATICSHIFT", @"CRANKSHAFT", @"PRECEDINGVEHICLE", @"MTGEARPOS", @"REVMATCH", @"DAASINFO",
                  @"ADASINFO", @"CRUISESW", @"DISPLAYSETTINGGRAPHIC",
                  @"DISPLAYSETTINGTV", @"DISPLAYSETTINGCOMPRESSVIDEO", @"DISPLAYSETTINGAUXVIDEO", @"DISPLAYSETTINGHDMI",
                  @"DISPLAYSETTINGDVD", @"DISPLAYSETTINGREARWIDECAMERA", @"DISPLAYSETTINGMVC", @"DISPLAYSETTINGLANEWATCH",
                  @"VOLUMESETTING", @"TOUCHPANEL", @"TONE", @"FADERBALANCE", @"SUBWOOFER", @"SVC", @"CURRENTAUDIO"];

    [statusDic removeAllObjects];
    [dataDic removeAllObjects];
    for (int i = 0; i < [arrayMap count]; i++) {
        NSString *groupname = (NSString *)arrayMap[i];
#ifndef __DEBUG_ON__
        NSString *resultString = (NSString *)[libEAPSample getVehicleData:(NSString *)groupname accessKey:ACCESSKEY];
#else
        static NSInteger cnt = 0;
        NSString *resultString;
        if ( [groupname isEqualToString:@"CURRENTAUDIO"] ) {
            resultString = dbgCurrentAudioData[cnt];
            cnt++;
            if( cnt >= [dbgCurrentAudioData count] ) {
                cnt = 0;
            }
        }
        else {
            resultString = (NSString *)[libEAPSample getVehicleData:(NSString *)groupname accessKey:ACCESSKEY];
        }
#endif
        if (resultString != nil) {
            [logInfo write_appsend:@"request" :resultString];
            [self setResultData:resultString];
        }
    }
    [self.table reloadData];
    [self setTimestamp];
    [self invokeSystemSound];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/**
 * notifyVehicleData
 */
- (void) notifyVehicleData:(NSString *)vehicledata {
    NSLog(@"notifyVehivleData");

    if ( vehicledata ) {
        [logInfo write_appsend:@"notice" :vehicledata];
        [statusDic removeAllObjects];
        [dataDic removeAllObjects];
        [self appendTblViewDicData: vehicledata];
        [self.table reloadData];
    }
    [self setTimestamp];
}

/**
 * setTimestamp
 */
- (void)setTimestamp {
    NSDateFormatter* now_date = [[NSDateFormatter alloc] init];
    [now_date setDateFormat:@"HH:mm:ss.SSS"];
    timeLabel.text = [now_date stringFromDate:[NSDate date]];
}

/**
 * setResultData
 */
- (void)setResultData:(NSString *)resultString {
    [self appendTblViewDicData: resultString];
}

- (void) appendTblViewDicData:(NSString *)appedString
{
    NSData *jsonData = [appedString dataUsingEncoding:NSUnicodeStringEncoding];
    NSError *error;
    NSDictionary *jsonDic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingAllowFragments error:&error];
    
    for ( id key in jsonDic) {
        if ( [key isEqualToString:@"result"]) {
            /* resultは表示しない */
        }
        else if ( [key isEqualToString:@"code"] ) {
            [statusDic setObject:@"0" forKey:key]; /* codeのstatusは0固定 */
            [dataDic setObject:jsonDic[key] forKey:key];
        }
        else if ( [key isEqualToString:@"current_audio"] ) {
            NSDictionary *audioInfo = jsonDic[key];
            id statusVal = audioInfo[@"status"];
            if ( statusVal != nil ) {
                if ( [audioInfo count] == 1 ) {
                    /* I-AUDIO未受信の場合データはstatusのみで通知される */
                    [statusDic setObject:statusVal forKey:key];
                    [dataDic setObject:@"" forKey:key]; /* データは空文字列 */
                }
                else {
                    for ( id audioData in audioDataKeys ) {
                        id audioDataVal = audioInfo[audioData];
                        if( audioDataVal != nil ) {
                            /* current_audioの場合、車両データ名と各データのJSON tag名を連結して表示データ名とする */
                            NSString *newKey = [NSString stringWithFormat:@"%@.%@", key, audioData];
                            [statusDic setObject:statusVal forKey:newKey];
                            [dataDic setObject:audioDataVal forKey:newKey];
                        }
                    }
                }
            }
        }
        else {
            NSDictionary *jsonInfo = jsonDic[key];
            if( (jsonInfo[@"status"] != nil) && (jsonInfo[@"data"] != nil) ) {
                [statusDic setObject:jsonInfo[@"status"] forKey:key];
                [dataDic setObject:jsonInfo[@"data"]  forKey:key];
            }
        }
    }
}

/**
 * 決定ボタン押下時のinputViewの消去
 */
- (void)resignKeyboard:(id)sender
{
    id firstResponder = [self getFirstResponder];
    if([firstResponder isKindOfClass:[UITextField class]])
    {
        [firstResponder resignFirstResponder];
    }
    else if([firstResponder isKindOfClass:[UITextView class]])
    {
        [firstResponder resignFirstResponder];
    }
}

/**
 * ActiveになっているText Fieldを取得
 */
- (id)getFirstResponder
{
    if([self.mAcClimStsSeatText isFirstResponder])
    {
        return self.mAcClimStsSeatText;
    }
    else if([self.mAcClimStsText isFirstResponder])
    {
        return self.mAcClimStsText;
    }
    else if([self.mAcAcSetSeatText isFirstResponder])
    {
        return self.mAcAcSetSeatText;
    }
    else if([self.mAcAcSetText isFirstResponder])
    {
        return self.mAcAcSetText;
    }
    else if([self.mAcModeSetSeatText isFirstResponder])
    {
        return self.mAcModeSetSeatText;
    }
    else if([self.mAcModeSetText isFirstResponder])
    {
        return self.mAcModeSetText;
    }
    else if([self.mAcFanUpDnSeatText isFirstResponder])
    {
        return self.mAcFanUpDnSeatText;
    }
    else if([self.mAcFanUpDnText isFirstResponder])
    {
        return self.mAcFanUpDnText;
    }
    else if([self.mAcFanSetSeatText isFirstResponder])
    {
        return self.mAcFanSetSeatText;
    }
    else if([self.mAcFanSetText isFirstResponder])
    {
        return self.mAcFanSetText;
    }
    else if([self.mAcTempUpDnSeatText isFirstResponder])
    {
        return self.mAcTempUpDnSeatText;
    }
    else if([self.mAcTempUpDnText isFirstResponder])
    {
        return self.mAcTempUpDnText;
    }
    else if([self.mAcTempSetSeatText isFirstResponder])
    {
        return self.mAcTempSetSeatText;
    }
    else if([self.mAcTempSetText isFirstResponder])
    {
        return self.mAcTempSetText;
    }
    else if([self.mAcFrDefSetSeatText isFirstResponder])
    {
        return self.mAcFrDefSetSeatText;
    }
    else if([self.mAcFrDefSetText isFirstResponder])
    {
        return self.mAcFrDefSetText;
    }
    else if([self.mAcRrDefSetSeatText isFirstResponder])
    {
        return self.mAcRrDefSetSeatText;
    }
    else if([self.mAcRrDefSetText isFirstResponder])
    {
        return self.mAcRrDefSetText;
    }
    else if([self.mAcSyncSetSeatText isFirstResponder])
    {
        return self.mAcSyncSetSeatText;
    }
    else if([self.mAcSyncSetText isFirstResponder])
    {
        return self.mAcSyncSetText;
    }
    else if([self.mAcRecFrsSetSeatText isFirstResponder])
    {
        return self.mAcRecFrsSetSeatText;
    }
    else if([self.mAcRecFrsSetText isFirstResponder])
    {
        return self.mAcRecFrsSetText;
    }
    else if([self.mAcVentTempSetSeatText isFirstResponder])
    {
        return self.mAcVentTempSetSeatText;
    }
    else if([self.mAcVentTempSetText isFirstResponder])
    {
        return self.mAcVentTempSetText;
    }
    else if([self.mAudioSourceChangeDirectText isFirstResponder])
    {
        return self.mAudioSourceChangeDirectText;
    }
    else if([self.mAudioSourceChangeText isFirstResponder])
    {
        return self.mAudioSourceChangeText;
    }
    else if([self.mAudioSkipUpDownText isFirstResponder])
    {
        return self.mAudioSkipUpDownText;
    }
    else if([self.mAudioPlayText isFirstResponder])
    {
        return self.mAudioPlayText;
    }
    else if([self.mAudioFfRewText isFirstResponder])
    {
        return self.mAudioFfRewText;
    }
    else if([self.mAudioSeekText isFirstResponder])
    {
        return self.mAudioSeekText;
    }
    else if([self.mAudioShuffleText isFirstResponder])
    {
        return self.mAudioShuffleText;
    }
    else if([self.mAudioRepeatText isFirstResponder])
    {
        return self.mAudioRepeatText;
    }
    else if([self.mAudioSetChannelSXMText isFirstResponder])
    {
        return self.mAudioSetChannelSXMText;
    }
    else if([self.mAudioSetAudioVolText isFirstResponder])
    {
        return self.mAudioSetAudioVolText;
    }
    else if([self.mAudioSetAudioVolDirectText isFirstResponder])
    {
        return self.mAudioSetAudioVolDirectText;
    }
    else if([self.mAudioScanSourceText isFirstResponder])
    {
        return self.mAudioScanSourceText;
    }
    else if([self.mAudioScanUpDnText isFirstResponder])
    {
        return self.mAudioScanUpDnText;
    }
    else if([self.mAudioTuneSourceText isFirstResponder])
    {
        return self.mAudioTuneSourceText;
    }
    else if([self.mAudioTuneUpDnText isFirstResponder])
    {
        return self.mAudioTuneUpDnText;
    }
    else if([self.mSettingDisplaySrcText isFirstResponder])
    {
        return self.mSettingDisplaySrcText;
    }
    else if([self.mSettingDisplayKindText isFirstResponder])
    {
        return self.mSettingDisplayKindText;
    }
    else if([self.mSettingDisplayUpDnText isFirstResponder])
    {
        return self.mSettingDisplayUpDnText;
    }
    else if([self.mSettingVolumeKindText isFirstResponder])
    {
        return self.mSettingVolumeKindText;
    }
    else if([self.mSettingVolumeUpDnText isFirstResponder])
    {
        return self.mSettingVolumeUpDnText;
    }
    else if([self.mSettingVolumeDirectKindText isFirstResponder])
    {
        return self.mSettingVolumeDirectKindText;
    }
    else if([self.mSettingVolumeDirectVolText isFirstResponder])
    {
        return self.mSettingVolumeDirectVolText;
    }
    else if([self.mSettingControlVolText isFirstResponder])
    {
        return self.mSettingControlVolText;
    }
    else if([self.mSettingTouchPanelSensitivityText isFirstResponder])
    {
        return self.mSettingTouchPanelSensitivityText;
    }
    else if([self.mSettingToneKindText isFirstResponder])
    {
        return self.mSettingToneKindText;
    }
    else if([self.mSettingToneUpDnText isFirstResponder])
    {
        return self.mSettingToneUpDnText;
    }
    else if([self.mSettingToneDirectKindText isFirstResponder])
    {
        return self.mSettingToneDirectKindText;
    }
    else if([self.mSettingToneDirectSetText isFirstResponder])
    {
        return self.mSettingToneDirectSetText;
    }
    else if([self.mSettingFaderBalanceText isFirstResponder])
    {
        return self.mSettingFaderBalanceText;
    }
    else if([self.mSettingFaderBalanceUpDnText isFirstResponder])
    {
        return self.mSettingFaderBalanceUpDnText;
    }
    else if([self.mSettingFaderBalanceDirectText isFirstResponder])
    {
        return self.mSettingFaderBalanceDirectText;
    }
    else if([self.mSettingFaderBalanceDirectSetText isFirstResponder])
    {
        return self.mSettingFaderBalanceDirectSetText;
    }
    else if([self.mSettingSubwooferText isFirstResponder])
    {
        return self.mSettingSubwooferText;
    }
    else if([self.mSettingSubwooferDirectText isFirstResponder])
    {
        return self.mSettingSubwooferDirectText;
    }
    else if([self.mSettingSVCText isFirstResponder])
    {
        return self.mSettingSVCText;
    }
    else if([self.mNaviDestinationLatitudeText isFirstResponder])
    {
        return self.mNaviDestinationLatitudeText;
    }
    else if([self.mNaviDestinationLongitudeText isFirstResponder])
    {
        return self.mNaviDestinationLongitudeText;
    }
    else if([self.mNaviDestinationNameTextView isFirstResponder])
    {
        return self.mNaviDestinationNameTextView;
    }
    else if([self.mNotificationPopupKindText isFirstResponder])
    {
        return self.mNotificationPopupKindText;
    }
    else if([self.mNotificationPopupWordTextView isFirstResponder])
    {
        return self.mNotificationPopupWordTextView;
    }
    return nil;
}

/**
 * AccessoryViewがピッカーのText Field選択時に初期値を設定
 */
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    NSUInteger tag = [textField tag];
    [self checkSpecialFields:tag];
}

- (void)checkSpecialFields:(NSInteger)tag
{
    if(tag == ACCLIMSTS_SEAT_TAG && [self.mAcClimStsSeatText.text isEqualToString:@""])
    {
        [self setAcClimStsSeatData];
    }
    else if(tag == ACCLIMSTS_CLIM_TAG && [self.mAcClimStsText.text isEqualToString:@""])
    {
        [self setAcClimStsData];
    }
    else if(tag == ACACSET_SEAT_TAG && [self.mAcAcSetSeatText.text isEqualToString:@""])
    {
        [self setAcAcSetSeatData];
    }
    else if(tag == ACACSET_ACSTS_TAG && [self.mAcAcSetText.text isEqualToString:@""])
    {
        [self setAcAcSetData];
    }
    else if(tag == ACMODE_SEAT_TAG && [self.mAcModeSetSeatText.text isEqualToString:@""])
    {
        [self setAcModeSetSeatData];
    }
    else if(tag == ACMODE_MODE_TAG && [self.mAcModeSetText.text isEqualToString:@""])
    {
        [self setAcModeSetData];
    }
    else if(tag == ACFANUD_SEAT_TAG && [self.mAcFanUpDnSeatText.text isEqualToString:@""])
    {
        [self setAcFanUpDnSeatData];
    }
    else if(tag == ACFANUD_FANUD_TAG && [self.mAcFanUpDnText.text isEqualToString:@""])
    {
        [self setAcFanUpDnData];
    }
    else if(tag == ACFANSET_SEAT_TAG && [self.mAcFanSetSeatText.text isEqualToString:@""])
    {
        [self setAcFanSetSeatData];
    }
    else if(tag == ACFANSET_FANVAL_TAG && [self.mAcFanSetText.text isEqualToString:@""])
    {
        [self setAcFanSetData];
    }
    else if(tag == ACTMPUD_SEAT_TAG && [self.mAcTempUpDnSeatText.text isEqualToString:@""])
    {
        [self setAcTempUpDnSeatData];
    }
    else if(tag == ACTMPUD_TMPUD_TAG && [self.mAcTempUpDnText.text isEqualToString:@""])
    {
        [self setAcTempUpDnData];
    }
    else if(tag == ACTMP_SEAT_TAG && [self.mAcTempSetSeatText.text isEqualToString:@""])
    {
        [self setAcTempSetSeatData];
    }
    else if(tag == ACFRDEF_SEAT_TAG && [self.mAcFrDefSetSeatText.text isEqualToString:@""])
    {
        [self setAcFrDefSetSeatData];
    }
    else if(tag == ACFRDEF_FRDEF_TAG && [self.mAcFrDefSetText.text isEqualToString:@""])
    {
        [self setAcFrDefSetData];
    }
    else if(tag == ACRRDEF_SEAT_TAG && [self.mAcRrDefSetSeatText.text isEqualToString:@""])
    {
        [self setAcRrDefSetSeatData];
    }
    else if(tag == ACRRDEF_RRDEF_TAG && [self.mAcRrDefSetText.text isEqualToString:@""])
    {
        [self setAcRrDefSetData];
    }
    else if(tag == ACSYNC_SEAT_TAG && [self.mAcSyncSetSeatText.text isEqualToString:@""])
    {
        [self setAcSyncSetSeatData];
    }
    else if(tag == ACSYNC_SYNC_TAG && [self.mAcSyncSetText.text isEqualToString:@""])
    {
        [self setAcSyncSetData];
    }
    else if(tag == ACRECFRS_SEAT_TAG && [self.mAcRecFrsSetSeatText.text isEqualToString:@""])
    {
        [self setAcRecFrsSetSeatData];
    }
    else if(tag == ACRECFRS_RECFRS_TAG && [self.mAcRecFrsSetText.text isEqualToString:@""])
    {
        [self setAcRecFrsSetData];
    }
    else if(tag == ACVNT_SEAT_TAG && [self.mAcVentTempSetSeatText.text isEqualToString:@""])
    {
        [self setAcVentTempSetSeatData];
    }
    else if(tag == ACVNT_VNTTMP_TAG && [self.mAcVentTempSetText.text isEqualToString:@""])
    {
        [self setAcVentTempSetData];
    }
    else if(tag == ADSRCDIR_SRC_TAG && [self.mAudioSourceChangeDirectText.text isEqualToString:@""])
    {
        [self setAdSrcChgDirData];
    }
    else if(tag == ADSRC_NEXT_TAG && [self.mAudioSourceChangeText.text isEqualToString:@""])
    {
        [self setAdSrcChgData];
    }
    else if(tag == ADSKIPUPDN_SKIP_TAG && [self.mAudioSkipUpDownText.text isEqualToString:@""])
    {
        [self setAdSkipUpDnData];
    }
    else if(tag == ADPLAY_PLAY_TAG && [self.mAudioPlayText.text isEqualToString:@""])
    {
        [self setAdPlayData];
    }
    else if(tag == ADFFREW_FRREW_TAG && [self.mAudioFfRewText.text isEqualToString:@""])
    {
        [self setAdFfRewData];
    }
    else if(tag == ADSEEK_SEEK_TAG && [self.mAudioSeekText.text isEqualToString:@""])
    {
        [self setAdSeekData];
    }
    else if(tag == ADSHUFFLE_MODE_TAG && [self.mAudioShuffleText.text isEqualToString:@""])
    {
        [self setAdShuffleData];
    }
    else if(tag == ADREPEAT_MODE_TAG && [self.mAudioRepeatText.text isEqualToString:@""])
    {
        [self setAdRepeatData];
    }
    else if(tag == ADVOL_UPDN_TAG && [self.mAudioSetAudioVolText.text isEqualToString:@""])
    {
        [self setAdSetAudioVolData];
    }
    else if(tag == ADSCAN_SRC_TAG && [self.mAudioScanSourceText.text isEqualToString:@""])
    {
        [self setAdScanSrcData];
    }
    else if(tag == ADSCAN_SCAN_TAG && [self.mAudioScanUpDnText.text isEqualToString:@""])
    {
        [self setAdScanData];
    }
    else if(tag == ADTUNE_SRC_TAG && [self.mAudioTuneSourceText.text isEqualToString:@""])
    {
        [self setAdTuneSrcData];
    }
    else if(tag == ADTUNE_UPDN_TAG && [self.mAudioTuneUpDnText.text isEqualToString:@""])
    {
        [self setAdTuneData];
    }
    else if(tag == SETDISP_SRC_TAG && [self.mSettingDisplaySrcText.text isEqualToString:@""])
    {
        [self setSetDispSrcData];
    }
    else if(tag == SETDISP_KIND_TAG && [self.mSettingDisplayKindText.text isEqualToString:@""])
    {
        [self setSetDispKindData];
    }
    else if(tag == SETDISP_UPDN_TAG && [self.mSettingDisplayUpDnText.text isEqualToString:@""])
    {
        [self setSetDispData];
    }
    else if(tag == SETVOL_KIND_TAG && [self.mSettingVolumeKindText.text isEqualToString:@""])
    {
        [self setSetVolKindData];
    }
    else if(tag == SETVOL_UPDN_TAG && [self.mSettingVolumeUpDnText.text isEqualToString:@""])
    {
        [self setSetVolData];
    }
    else if(tag == SETVOLDIR_KIND_TAG && [self.mSettingVolumeDirectKindText.text isEqualToString:@""])
    {
        [self setSetVolDirKindData];
    }
    else if(tag == SETCTRLVOL_VOL_TAG && [self.mSettingControlVolText.text isEqualToString:@""])
    {
        [self setSetControlVolData];
    }
    else if(tag == SETTCHPNL_SNSTVTY_TAG && [self.mSettingTouchPanelSensitivityText.text isEqualToString:@""])
    {
        [self setSetTouchPanelSensitivityData];
    }
    else if(tag == SETTONE_KIND_TAG && [self.mSettingToneKindText.text isEqualToString:@""])
    {
        [self setSetToneKindData];
    }
    else if(tag == SETTONE_UPDN_TAG && [self.mSettingToneUpDnText.text isEqualToString:@""])
    {
        [self setSetToneData];
    }
    else if(tag == SETTONEDIR_KIND_TAG && [self.mSettingToneDirectKindText.text isEqualToString:@""])
    {
        [self setSetToneDirKindData];
    }
    else if(tag == SETFADER_BALANCE_TAG && [self.mSettingFaderBalanceText.text isEqualToString:@""])
    {
        [self setSetFaderBalanceData];
    }
    else if(tag == SETFADER_UPDN_TAG && [self.mSettingFaderBalanceUpDnText.text isEqualToString:@""])
    {
        [self setSetFaderBalanceUpDnData];
    }
    else if(tag == SETFADERDIR_BALANCE_TAG && [self.mSettingFaderBalanceDirectText.text isEqualToString:@""])
    {
        [self setSetFaderBalanceDirData];
    }
    else if(tag == SETSUBWOOFER_UPDN_TAG && [self.mSettingSubwooferText.text isEqualToString:@""])
    {
        [self setSetSubwooferData];
    }
    else if(tag == SETSVC_SET_TAG && [self.mSettingSVCText.text isEqualToString:@""])
    {
        [self setSetSVCData];
    }
    else if(tag == NOTIPOP_KIND_TAG && [self.mNotificationPopupKindText.text isEqualToString:@""])
    {
        [self setNotiPopupKindData];
    }
}

- (void)setAcClimStsSeatData
{
    NSInteger row = [self.mAcClimStsSeatPicker selectedRowInComponent:0];
    self.mAcClimStsSeatText.text = acClimStsSeatData[row][1];
    acClimStsSeat = [acClimStsSeatData[row][0] intValue];
}

- (void)setAcClimStsData
{
    NSInteger row = [self.mAcClimStsPicker selectedRowInComponent:0];
    self.mAcClimStsText.text = acClimStsData[row][1];
    acClimSts = [acClimStsData[row][0] intValue];
}

- (void)setAcAcSetSeatData
{
    NSInteger row = [self.mAcAcSetSeatPicker selectedRowInComponent:0];
    self.mAcAcSetSeatText.text = acAcSetSeatData[row][1];
    acAcSetSeat = [acAcSetSeatData[row][0] intValue];
}

- (void)setAcAcSetData
{
    NSInteger row = [self.mAcAcSetPicker selectedRowInComponent:0];
    self.mAcAcSetText.text = acAcSetData[row][1];
    acAcSet = [acAcSetData[row][0] intValue];
}

- (void)setAcModeSetSeatData
{
    NSInteger row = [self.mAcModeSetSeatPicker selectedRowInComponent:0];
    self.mAcModeSetSeatText.text = acModeSetSeatData[row][1];
    acModeSetSeat = [acModeSetSeatData[row][0] intValue];
}

- (void)setAcModeSetData
{
    NSInteger row = [self.mAcModeSetPicker selectedRowInComponent:0];
    self.mAcModeSetText.text = acModeSetData[row][1];
    acModeSet = [acModeSetData[row][0] intValue];
}

- (void)setAcFanUpDnSeatData
{
    NSInteger row = [self.mAcFanUpDnSeatPicker selectedRowInComponent:0];
    self.mAcFanUpDnSeatText.text = acFanUpDnSeatData[row][1];
    acFanUpDnSeat = [acFanUpDnSeatData[row][0] intValue];
}

- (void)setAcFanUpDnData
{
    NSInteger row = [self.mAcFanUpDnPicker selectedRowInComponent:0];
    self.mAcFanUpDnText.text = acFanUpDnData[row][1];
    acFanUpDn = [acFanUpDnData[row][0] intValue];
}

- (void)setAcFanSetSeatData
{
    NSInteger row = [self.mAcFanSetSeatPicker selectedRowInComponent:0];
    self.mAcFanSetSeatText.text = acFanSetSeatData[row][1];
    acFanSetSeat = [acFanSetSeatData[row][0] intValue];
}

- (void)setAcFanSetData
{
    NSInteger row = [self.mAcFanSetPicker selectedRowInComponent:0];
    self.mAcFanSetText.text = acFanSetData[row][1];
    acFanSet = [acFanSetData[row][0] intValue];
}

- (void)setAcTempUpDnSeatData
{
    NSInteger row = [self.mAcTempUpDnSeatPicker selectedRowInComponent:0];
    self.mAcTempUpDnSeatText.text = acTempUpDnSeatData[row][1];
    acTempUpDnSeat = [acTempUpDnSeatData[row][0] intValue];
}

- (void)setAcTempUpDnData
{
    NSInteger row = [self.mAcTempUpDnPicker selectedRowInComponent:0];
    self.mAcTempUpDnText.text = acTempUpDnData[row][1];
    acTempUpDn = [acTempUpDnData[row][0] intValue];
}

- (void)setAcTempSetSeatData
{
    NSInteger row = [self.mAcTempSetSeatPicker selectedRowInComponent:0];
    self.mAcTempSetSeatText.text = acTempSetSeatData[row][1];
    acTempSetSeat = [acTempSetSeatData[row][0] intValue];
}

- (void)setAcFrDefSetSeatData
{
    NSInteger row = [self.mAcFrDefSetSeatPicker selectedRowInComponent:0];
    self.mAcFrDefSetSeatText.text = acFrDefSetSeatData[row][1];
    acFrDefSetSeat = [acFrDefSetSeatData[row][0] intValue];
}

- (void)setAcFrDefSetData
{
    NSInteger row = [self.mAcFrDefSetPicker selectedRowInComponent:0];
    self.mAcFrDefSetText.text = acFrDefSetData[row][1];
    acFrDefSet = [acFrDefSetData[row][0] intValue];
}

- (void)setAcRrDefSetSeatData
{
    NSInteger row = [self.mAcRrDefSetSeatPicker selectedRowInComponent:0];
    self.mAcRrDefSetSeatText.text = acRrDefSetSeatData[row][1];
    acRrDefSetSeat = [acRrDefSetSeatData[row][0] intValue];
}

- (void)setAcRrDefSetData
{
    NSInteger row = [self.mAcRrDefSetPicker selectedRowInComponent:0];
    self.mAcRrDefSetText.text = acRrDefSetData[row][1];
    acRrDefSet = [acRrDefSetData[row][0] intValue];
}

- (void)setAcSyncSetSeatData
{
    NSInteger row = [self.mAcSyncSetSeatPicker selectedRowInComponent:0];
    self.mAcSyncSetSeatText.text = acSyncSetSeatData[row][1];
    acSyncSetSeat = [acSyncSetSeatData[row][0] intValue];
}

- (void)setAcSyncSetData
{
    NSInteger row = [self.mAcSyncSetPicker selectedRowInComponent:0];
    self.mAcSyncSetText.text = acSyncSetData[row][1];
    acSyncSet = [acSyncSetData[row][0] intValue];
}

- (void)setAcRecFrsSetSeatData
{
    NSInteger row = [self.mAcRecFrsSetSeatPicker selectedRowInComponent:0];
    self.mAcRecFrsSetSeatText.text = acRecFrsSetSeatData[row][1];
    acRecFrsSetSeat = [acRecFrsSetSeatData[row][0] intValue];
}

- (void)setAcRecFrsSetData
{
    NSInteger row = [self.mAcRecFrsSetPicker selectedRowInComponent:0];
    self.mAcRecFrsSetText.text = acRecFrsSetData[row][1];
    acRecFrsSet = [acRecFrsSetData[row][0] intValue];
}

- (void)setAcVentTempSetSeatData
{
    NSInteger row = [self.mAcVentTempSetSeatPicker selectedRowInComponent:0];
    self.mAcVentTempSetSeatText.text = acVentTempSetSeatData[row][1];
    acVentTempSetSeat = [acVentTempSetSeatData[row][0] intValue];
}

- (void)setAcVentTempSetData
{
    NSInteger row = [self.mAcVentTempSetPicker selectedRowInComponent:0];
    self.mAcVentTempSetText.text = acVentTempSetData[row][1];
    acVentTempSet = [acVentTempSetData[row][0] intValue];
}

- (void)setAdSrcChgDirData
{
    NSInteger row = [self.mAudioSourceChangeDirectPicker selectedRowInComponent:0];
    self.mAudioSourceChangeDirectText.text = adSrcChgDirData[row][1];
    adSrcChgDir = [adSrcChgDirData[row][0] intValue];
}

- (void)setAdSrcChgData
{
    NSInteger row = [self.mAudioSourceChangePicker selectedRowInComponent:0];
    self.mAudioSourceChangeText.text = adSrcChgData[row][1];
    adSrcChg = [adSrcChgData[row][0] intValue];
}

- (void)setAdSkipUpDnData
{
    NSInteger row = [self.mAudioSkipUpDownPicker selectedRowInComponent:0];
    self.mAudioSkipUpDownText.text = adSkipUpDnData[row][1];
    adSkipUpDn = [adSkipUpDnData[row][0] intValue];
}

- (void)setAdPlayData
{
    NSInteger row = [self.mAudioPlayPicker selectedRowInComponent:0];
    self.mAudioPlayText.text = adPlayData[row][1];
    adPlay = [adPlayData[row][0] intValue];
}

- (void)setAdFfRewData
{
    NSInteger row = [self.mAudioFfRewPicker selectedRowInComponent:0];
    self.mAudioFfRewText.text = adFfRewData[row][1];
    adFfRew = [adFfRewData[row][0] intValue];
}

- (void)setAdSeekData
{
    NSInteger row = [self.mAudioSeekPicker selectedRowInComponent:0];
    self.mAudioSeekText.text = adSeekData[row][1];
    adSeek = [adSeekData[row][0] intValue];
}

- (void)setAdShuffleData
{
    NSInteger row = [self.mAudioShufflePicker selectedRowInComponent:0];
    self.mAudioShuffleText.text = adShuffleData[row][1];
    adShuffle = [adShuffleData[row][0] intValue];
}

- (void)setAdRepeatData
{
    NSInteger row = [self.mAudioRepeatPicker selectedRowInComponent:0];
    self.mAudioRepeatText.text = adRepeatData[row][1];
    adRepeat = [adRepeatData[row][0] intValue];
}

- (void)setAdSetAudioVolData
{
    NSInteger row = [self.mAudioSetAudioVolPicker selectedRowInComponent:0];
    self.mAudioSetAudioVolText.text = adSetAudioVolData[row][1];
    adSetAudioVol = [adSetAudioVolData[row][0] intValue];
}

- (void)setAdScanSrcData
{
    NSInteger row = [self.mAudioScanSourcePicker selectedRowInComponent:0];
    self.mAudioScanSourceText.text = adScanSrcData[row][1];
    adScanSrc = [adScanSrcData[row][0] intValue];
}

- (void)setAdScanData
{
    NSInteger row = [self.mAudioScanUpDnPicker selectedRowInComponent:0];
    self.mAudioScanUpDnText.text = adScanData[row][1];
    adScan = [adScanData[row][0] intValue];
}

- (void)setAdTuneSrcData
{
    NSInteger row = [self.mAudioTuneSourcePicker selectedRowInComponent:0];
    self.mAudioTuneSourceText.text = adTuneSrcData[row][1];
    adTuneSrc = [adTuneSrcData[row][0] intValue];
}

- (void)setAdTuneData
{
    NSInteger row = [self.mAudioTuneUpDnPicker selectedRowInComponent:0];
    self.mAudioTuneUpDnText.text = adTuneData[row][1];
    adTune = [adTuneData[row][0] intValue];
}

- (void)setSetDispSrcData
{
    NSInteger row = [self.mSettingDisplaySrcPicker selectedRowInComponent:0];
    self.mSettingDisplaySrcText.text = setDispSrcData[row][1];
    setDispSrc = [setDispSrcData[row][0] intValue];
}

- (void)setSetDispKindData
{
    NSInteger row = [self.mSettingDisplayKindPicker selectedRowInComponent:0];
    self.mSettingDisplayKindText.text = setDispKindData[row][1];
    setDispKind = [setDispKindData[row][0] intValue];
}

- (void)setSetDispData
{
    NSInteger row = [self.mSettingDisplayUpDnPicker selectedRowInComponent:0];
    self.mSettingDisplayUpDnText.text = setDispData[row][1];
    setDisp = [setDispData[row][0] intValue];
}

- (void)setSetVolKindData
{
    NSInteger row = [self.mSettingVolumeKindPicker selectedRowInComponent:0];
    self.mSettingVolumeKindText.text = setVolKindData[row][1];
    setVolKind = [setVolKindData[row][0] intValue];
}

- (void)setSetVolData
{
    NSInteger row = [self.mSettingVolumeUpDnPicker selectedRowInComponent:0];
    self.mSettingVolumeUpDnText.text = setVolData[row][1];
    setVol = [setVolData[row][0] intValue];
}

- (void)setSetVolDirKindData
{
    NSInteger row = [self.mSettingVolumeDirectKindPicker selectedRowInComponent:0];
    self.mSettingVolumeDirectKindText.text = setVolDirKindData[row][1];
    setVolDirKind = [setVolDirKindData[row][0] intValue];
}

- (void)setSetControlVolData
{
    NSInteger row = [self.mSettingControlVolPicker selectedRowInComponent:0];
    self.mSettingControlVolText.text = setControlVolData[row][1];
    setControlVol = [setControlVolData[row][0] intValue];
}

- (void)setSetTouchPanelSensitivityData
{
    NSInteger row = [self.mSettingTouchPanelSensitivityPicker selectedRowInComponent:0];
    self.mSettingTouchPanelSensitivityText.text = setTouchPanelSensitivityData[row][1];
    setTouchPanelSensitivity = [setTouchPanelSensitivityData[row][0] intValue];
}

- (void)setSetToneKindData
{
    NSInteger row = [self.mSettingToneKindPicker selectedRowInComponent:0];
    self.mSettingToneKindText.text = setToneKindData[row][1];
    setToneKind = [setToneKindData[row][0] intValue];
}

- (void)setSetToneData
{
    NSInteger row = [self.mSettingToneUpDnPicker selectedRowInComponent:0];
    self.mSettingToneUpDnText.text = setToneData[row][1];
    setTone = [setToneData[row][0] intValue];
}

- (void)setSetToneDirKindData
{
    NSInteger row = [self.mSettingToneDirectKindPicker selectedRowInComponent:0];
    self.mSettingToneDirectKindText.text = setToneDirKindData[row][1];
    setToneDirKind = [setToneDirKindData[row][0] intValue];
}

- (void)setSetFaderBalanceData
{
    NSInteger row = [self.mSettingFaderBalancePicker selectedRowInComponent:0];
    self.mSettingFaderBalanceText.text = setFaderBalanceData[row][1];
    setFaderBalance = [setFaderBalanceData[row][0] intValue];
}

- (void)setSetFaderBalanceUpDnData
{
    NSInteger row = [self.mSettingFaderBalanceUpDnPicker selectedRowInComponent:0];
    self.mSettingFaderBalanceUpDnText.text = setFaderBalanceUpDnData[row][1];
    setFaderBalanceUpDn = [setFaderBalanceUpDnData[row][0] intValue];
}

- (void)setSetFaderBalanceDirData
{
    NSInteger row = [self.mSettingFaderBalanceDirectPicker selectedRowInComponent:0];
    self.mSettingFaderBalanceDirectText.text = setFaderBalanceDirData[row][1];
    setFaderBalanceDir = [setFaderBalanceDirData[row][0] intValue];
}

- (void)setSetSubwooferData
{
    NSInteger row = [self.mSettingSubwooferPicker selectedRowInComponent:0];
    self.mSettingSubwooferText.text = setSubwooferData[row][1];
    setSubwoofer = [setSubwooferData[row][0] intValue];
}

- (void)setSetSVCData
{
    NSInteger row = [self.mSettingSVCPicker selectedRowInComponent:0];
    self.mSettingSVCText.text = setSVCData[row][1];
    setSVC = [setSVCData[row][0] intValue];
}

- (void)setNotiPopupKindData
{
    NSInteger row = [self.mNotificationPopupKindPicker selectedRowInComponent:0];
    self.mNotificationPopupKindText.text = notiPopupKindData[row][1];
    notiPopupKind = [notiPopupKindData[row][0] intValue];
}

/**
 * Text Fieldの編集可不可を返却(Text Field選択時に動作する)
 */
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(nonnull NSString *)string
{
    NSUInteger tag = [textField tag];
    switch (tag) {
        case ACCLIMSTS_SEAT_TAG:
        case ACCLIMSTS_CLIM_TAG:
        case ACACSET_SEAT_TAG:
        case ACACSET_ACSTS_TAG:
        case ACMODE_SEAT_TAG:
        case ACMODE_MODE_TAG:
        case ACFANUD_SEAT_TAG:
        case ACFANUD_FANUD_TAG:
        case ACFANSET_SEAT_TAG:
        case ACFANSET_FANVAL_TAG:
        case ACTMPUD_SEAT_TAG:
        case ACTMPUD_TMPUD_TAG:
        case ACTMP_SEAT_TAG:
//        case ACTMP_TMPSET_TAG:
        case ACFRDEF_SEAT_TAG:
        case ACFRDEF_FRDEF_TAG:
        case ACRRDEF_SEAT_TAG:
        case ACRRDEF_RRDEF_TAG:
        case ACSYNC_SEAT_TAG:
        case ACSYNC_SYNC_TAG:
        case ACRECFRS_SEAT_TAG:
        case ACRECFRS_RECFRS_TAG:
        case ACVNT_SEAT_TAG:
        case ACVNT_VNTTMP_TAG:
        case ADSRCDIR_SRC_TAG:
        case ADSRC_NEXT_TAG:
        case ADSKIPUPDN_SKIP_TAG:
        case ADPLAY_PLAY_TAG:
        case ADFFREW_FRREW_TAG:
        case ADSEEK_SEEK_TAG:
        case ADSHUFFLE_MODE_TAG:
        case ADREPEAT_MODE_TAG:
//        case ADCHSXM_CHNO_TAG:
        case ADVOL_UPDN_TAG:
//        case ADVOLDIR_VOL_TAG:
        case ADSCAN_SRC_TAG:
        case ADSCAN_SCAN_TAG:
        case ADTUNE_SRC_TAG:
        case ADTUNE_UPDN_TAG:
        case SETDISP_SRC_TAG:
        case SETDISP_KIND_TAG:
        case SETDISP_UPDN_TAG:
        case SETVOL_KIND_TAG:
        case SETVOL_UPDN_TAG:
        case SETVOLDIR_KIND_TAG:
//        case SETVOLDIR_VOL_TAG:
        case SETCTRLVOL_VOL_TAG:
        case SETTCHPNL_SNSTVTY_TAG:
        case SETTONE_KIND_TAG:
        case SETTONE_UPDN_TAG:
        case SETTONEDIR_KIND_TAG:
//        case SETTONEDIR_SET_TAG:
        case SETFADER_BALANCE_TAG:
        case SETFADER_UPDN_TAG:
        case SETFADERDIR_BALANCE_TAG:
//        case SETFADERDIR_SET_TAG:
        case SETSUBWOOFER_UPDN_TAG:
//        case SETSUBWOOFERDIR_SET_TAG:
        case SETSVC_SET_TAG:
//        case NAVIDST_LAT_TAG:
//        case NAVIDST_LON_TAG:
//        case NAVIDST_NAME_TAG:
//        case NOTIPOP_KIND_TAG:
//        case NOTIPOP_WORD_TAG:
            return NO; // ユーザ入力の無効化
        default:
            break;
    }
    return YES;
}

/**
 * ピッカーの列数を返す
 */
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

/**
 * ピッカーの行数を返す
 */
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    if(pickerView.tag == ACCLIMSTS_SEAT_TAG){
        return acClimStsSeatData.count;
    }
    else if(pickerView.tag == ACCLIMSTS_CLIM_TAG){
        return acClimStsData.count;
    }
    else if(pickerView.tag == ACACSET_SEAT_TAG){
        return acAcSetSeatData.count;
    }
    else if(pickerView.tag == ACACSET_ACSTS_TAG){
        return acAcSetData.count;
    }
    else if(pickerView.tag == ACMODE_SEAT_TAG){
        return acModeSetSeatData.count;
    }
    else if(pickerView.tag == ACMODE_MODE_TAG){
        return acModeSetData.count;
    }
    else if(pickerView.tag == ACFANUD_SEAT_TAG){
        return acFanUpDnSeatData.count;
    }
    else if(pickerView.tag == ACFANUD_FANUD_TAG){
        return acFanUpDnData.count;
    }
    else if(pickerView.tag == ACFANSET_SEAT_TAG){
        return acFanSetSeatData.count;
    }
    else if(pickerView.tag == ACFANSET_FANVAL_TAG){
        return acFanSetData.count;
    }
    else if(pickerView.tag == ACTMPUD_SEAT_TAG){
        return acTempUpDnSeatData.count;
    }
    else if(pickerView.tag == ACTMPUD_TMPUD_TAG){
        return acTempUpDnData.count;
    }
    else if(pickerView.tag == ACTMP_SEAT_TAG){
        return acTempSetSeatData.count;
    }
    else if(pickerView.tag == ACFRDEF_SEAT_TAG){
        return acFrDefSetSeatData.count;
    }
    else if(pickerView.tag == ACFRDEF_FRDEF_TAG){
        return acFrDefSetData.count;
    }
    else if(pickerView.tag == ACRRDEF_SEAT_TAG){
        return acRrDefSetSeatData.count;
    }
    else if(pickerView.tag == ACRRDEF_RRDEF_TAG){
        return acRrDefSetData.count;
    }
    else if(pickerView.tag == ACSYNC_SEAT_TAG){
        return acSyncSetSeatData.count;
    }
    else if(pickerView.tag == ACSYNC_SYNC_TAG){
        return acSyncSetData.count;
    }
    else if(pickerView.tag == ACRECFRS_SEAT_TAG){
        return acRecFrsSetSeatData.count;
    }
    else if(pickerView.tag == ACRECFRS_RECFRS_TAG){
        return acRecFrsSetData.count;
    }
    else if(pickerView.tag == ACVNT_SEAT_TAG){
        return acVentTempSetSeatData.count;
    }
    else if(pickerView.tag == ACVNT_VNTTMP_TAG){
        return acVentTempSetData.count;
    }
    else if(pickerView.tag == ADSRCDIR_SRC_TAG){
        return adSrcChgDirData.count;
    }
    else if(pickerView.tag == ADSRC_NEXT_TAG){
        return adSrcChgData.count;
    }
    else if(pickerView.tag == ADSKIPUPDN_SKIP_TAG){
        return adSkipUpDnData.count;
    }
    else if(pickerView.tag == ADPLAY_PLAY_TAG){
        return adPlayData.count;
    }
    else if(pickerView.tag == ADFFREW_FRREW_TAG){
        return adFfRewData.count;
    }
    else if(pickerView.tag == ADSEEK_SEEK_TAG){
        return adSeekData.count;
    }
    else if(pickerView.tag == ADSHUFFLE_MODE_TAG){
        return adShuffleData.count;
    }
    else if(pickerView.tag == ADREPEAT_MODE_TAG){
        return adRepeatData.count;
    }
    else if(pickerView.tag == ADVOL_UPDN_TAG){
        return adSetAudioVolData.count;
    }
    else if(pickerView.tag == ADSCAN_SRC_TAG){
        return adScanSrcData.count;
    }
    else if(pickerView.tag == ADSCAN_SCAN_TAG){
        return adScanData.count;
    }
    else if(pickerView.tag == ADTUNE_SRC_TAG){
        return adTuneSrcData.count;
    }
    else if(pickerView.tag == ADTUNE_UPDN_TAG){
        return adTuneData.count;
    }
    else if(pickerView.tag == SETDISP_SRC_TAG){
        return setDispSrcData.count;
    }
    else if(pickerView.tag == SETDISP_KIND_TAG){
        return setDispKindData.count;
    }
    else if(pickerView.tag == SETDISP_UPDN_TAG){
        return setDispData.count;
    }
    else if(pickerView.tag == SETVOL_KIND_TAG){
        return setVolKindData.count;
    }
    else if(pickerView.tag == SETVOL_UPDN_TAG){
        return setVolData.count;
    }
    else if(pickerView.tag == SETVOLDIR_KIND_TAG){
        return setVolDirKindData.count;
    }
    else if(pickerView.tag == SETCTRLVOL_VOL_TAG){
        return setControlVolData.count;
    }
    else if(pickerView.tag == SETTCHPNL_SNSTVTY_TAG){
        return setTouchPanelSensitivityData.count;
    }
    else if(pickerView.tag == SETTONE_KIND_TAG){
        return setToneKindData.count;
    }
    else if(pickerView.tag == SETTONE_UPDN_TAG){
        return setToneData.count;
    }
    else if(pickerView.tag == SETTONEDIR_KIND_TAG){
        return setToneDirKindData.count;
    }
    else if(pickerView.tag == SETFADER_BALANCE_TAG){
        return setFaderBalanceData.count;
    }
    else if(pickerView.tag == SETFADER_UPDN_TAG){
        return setFaderBalanceUpDnData.count;
    }
    else if(pickerView.tag == SETFADERDIR_BALANCE_TAG){
        return setFaderBalanceDirData.count;
    }
    else if(pickerView.tag == SETSUBWOOFER_UPDN_TAG){
        return setSubwooferData.count;
    }
    else if(pickerView.tag == SETSVC_SET_TAG){
        return setSVCData.count;
    }
    else if(pickerView.tag == NOTIPOP_KIND_TAG){
        return notiPopupKindData.count;
    }
    return 0;
}

/**
 * ピッカーに表示する文字列の取得
 */
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    if(pickerView.tag == ACCLIMSTS_SEAT_TAG){
        return acClimStsSeatData[row][1];
    }
    else if(pickerView.tag == ACCLIMSTS_CLIM_TAG){
        return acClimStsData[row][1];
    }
    else if(pickerView.tag == ACACSET_SEAT_TAG){
        return acAcSetSeatData[row][1];
    }
    else if(pickerView.tag == ACACSET_ACSTS_TAG){
        return acAcSetData[row][1];
    }
    else if(pickerView.tag == ACMODE_SEAT_TAG){
        return acModeSetSeatData[row][1];
    }
    else if(pickerView.tag == ACMODE_MODE_TAG){
        return acModeSetData[row][1];
    }
    else if(pickerView.tag == ACFANUD_SEAT_TAG){
        return acFanUpDnSeatData[row][1];
    }
    else if(pickerView.tag == ACFANUD_FANUD_TAG){
        return acFanUpDnData[row][1];
    }
    else if(pickerView.tag == ACFANSET_SEAT_TAG){
        return acFanSetSeatData[row][1];
    }
    else if(pickerView.tag == ACFANSET_FANVAL_TAG){
        return acFanSetData[row][1];
    }
    else if(pickerView.tag == ACTMPUD_SEAT_TAG){
        return acTempUpDnSeatData[row][1];
    }
    else if(pickerView.tag == ACTMPUD_TMPUD_TAG){
        return acTempUpDnData[row][1];
    }
    else if(pickerView.tag == ACTMP_SEAT_TAG){
        return acTempSetSeatData[row][1];
    }
    else if(pickerView.tag == ACFRDEF_SEAT_TAG){
        return acFrDefSetSeatData[row][1];
    }
    else if(pickerView.tag == ACFRDEF_FRDEF_TAG){
        return acFrDefSetData[row][1];
    }
    else if(pickerView.tag == ACRRDEF_SEAT_TAG){
        return acRrDefSetSeatData[row][1];
    }
    else if(pickerView.tag == ACRRDEF_RRDEF_TAG){
        return acRrDefSetData[row][1];
    }
    else if(pickerView.tag == ACSYNC_SEAT_TAG){
        return acSyncSetSeatData[row][1];
    }
    else if(pickerView.tag == ACSYNC_SYNC_TAG){
        return acSyncSetData[row][1];
    }
    else if(pickerView.tag == ACRECFRS_SEAT_TAG){
        return acRecFrsSetSeatData[row][1];
    }
    else if(pickerView.tag == ACRECFRS_RECFRS_TAG){
        return acRecFrsSetData[row][1];
    }
    else if(pickerView.tag == ACVNT_SEAT_TAG){
        return acVentTempSetSeatData[row][1];
    }
    else if(pickerView.tag == ACVNT_VNTTMP_TAG){
        return acVentTempSetData[row][1];
    }
    else if(pickerView.tag == ADSRCDIR_SRC_TAG){
        return adSrcChgDirData[row][1];
    }
    else if(pickerView.tag == ADSRC_NEXT_TAG){
        return adSrcChgData[row][1];
    }
    else if(pickerView.tag == ADSKIPUPDN_SKIP_TAG){
        return adSkipUpDnData[row][1];
    }
    else if(pickerView.tag == ADPLAY_PLAY_TAG){
        return adPlayData[row][1];
    }
    else if(pickerView.tag == ADFFREW_FRREW_TAG){
        return adFfRewData[row][1];
    }
    else if(pickerView.tag == ADSEEK_SEEK_TAG){
        return adSeekData[row][1];
    }
    else if(pickerView.tag == ADSHUFFLE_MODE_TAG){
        return adShuffleData[row][1];
    }
    else if(pickerView.tag == ADREPEAT_MODE_TAG){
        return adRepeatData[row][1];
    }
    else if(pickerView.tag == ADVOL_UPDN_TAG){
        return adSetAudioVolData[row][1];
    }
    else if(pickerView.tag == ADSCAN_SRC_TAG){
        return adScanSrcData[row][1];
    }
    else if(pickerView.tag == ADSCAN_SCAN_TAG){
        return adScanData[row][1];
    }
    else if(pickerView.tag == ADTUNE_SRC_TAG){
        return adTuneSrcData[row][1];
    }
    else if(pickerView.tag == ADTUNE_UPDN_TAG){
        return adTuneData[row][1];
    }
    else if(pickerView.tag == SETDISP_SRC_TAG){
        return setDispSrcData[row][1];
    }
    else if(pickerView.tag == SETDISP_KIND_TAG){
        return setDispKindData[row][1];
    }
    else if(pickerView.tag == SETDISP_UPDN_TAG){
        return setDispData[row][1];
    }
    else if(pickerView.tag == SETVOL_KIND_TAG){
        return setVolKindData[row][1];
    }
    else if(pickerView.tag == SETVOL_UPDN_TAG){
        return setVolData[row][1];
    }
    else if(pickerView.tag == SETVOLDIR_KIND_TAG){
        return setVolDirKindData[row][1];
    }
    else if(pickerView.tag == SETCTRLVOL_VOL_TAG){
        return setControlVolData[row][1];
    }
    else if(pickerView.tag == SETTCHPNL_SNSTVTY_TAG){
        return setTouchPanelSensitivityData[row][1];
    }
    else if(pickerView.tag == SETTONE_KIND_TAG){
        return setToneKindData[row][1];
    }
    else if(pickerView.tag == SETTONE_UPDN_TAG){
        return setToneData[row][1];
    }
    else if(pickerView.tag == SETTONEDIR_KIND_TAG){
        return setToneDirKindData[row][1];
    }
    else if(pickerView.tag == SETFADER_BALANCE_TAG){
        return setFaderBalanceData[row][1];
    }
    else if(pickerView.tag == SETFADER_UPDN_TAG){
        return setFaderBalanceUpDnData[row][1];
    }
    else if(pickerView.tag == SETFADERDIR_BALANCE_TAG){
        return setFaderBalanceDirData[row][1];
    }
    else if(pickerView.tag == SETSUBWOOFER_UPDN_TAG){
        return setSubwooferData[row][1];
    }
    else if(pickerView.tag == SETSVC_SET_TAG){
        return setSVCData[row][1];
    }
    else if(pickerView.tag == NOTIPOP_KIND_TAG){
        return notiPopupKindData[row][1];
    }
    return nil;
}

/**
 * ピッカーを回した時に更新された値をText Fieldに設定する
 */
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    NSUInteger tag = [pickerView tag];
    if(tag == ACCLIMSTS_SEAT_TAG)
    {
        [self setAcClimStsSeatData];
    }
    else if(tag == ACCLIMSTS_CLIM_TAG)
    {
        [self setAcClimStsData];
    }
    else if(tag == ACACSET_SEAT_TAG)
    {
        [self setAcAcSetSeatData];
    }
    else if(tag == ACACSET_ACSTS_TAG)
    {
        [self setAcAcSetData];
    }
    else if(tag == ACMODE_SEAT_TAG)
    {
        [self setAcModeSetSeatData];
    }
    else if(tag == ACMODE_MODE_TAG)
    {
        [self setAcModeSetData];
    }
    else if(tag == ACFANUD_SEAT_TAG)
    {
        [self setAcFanUpDnSeatData];
    }
    else if(tag == ACFANUD_FANUD_TAG)
    {
        [self setAcFanUpDnData];
    }
    else if(tag == ACFANSET_SEAT_TAG)
    {
        [self setAcFanSetSeatData];
    }
    else if(tag == ACFANSET_FANVAL_TAG)
    {
        [self setAcFanSetData];
    }
    else if(tag == ACTMPUD_SEAT_TAG)
    {
        [self setAcTempUpDnSeatData];
    }
    else if(tag == ACTMPUD_TMPUD_TAG)
    {
        [self setAcTempUpDnData];
    }
    else if(tag == ACTMP_SEAT_TAG)
    {
        [self setAcTempSetSeatData];
    }
    else if(tag == ACFRDEF_SEAT_TAG)
    {
        [self setAcFrDefSetSeatData];
    }
    else if(tag == ACFRDEF_FRDEF_TAG)
    {
        [self setAcFrDefSetData];
    }
    else if(tag == ACRRDEF_SEAT_TAG)
    {
        [self setAcRrDefSetSeatData];
    }
    else if(tag == ACRRDEF_RRDEF_TAG)
    {
        [self setAcRrDefSetData];
    }
    else if(tag == ACSYNC_SEAT_TAG)
    {
        [self setAcSyncSetSeatData];
    }
    else if(tag == ACSYNC_SYNC_TAG)
    {
        [self setAcSyncSetData];
    }
    else if(tag == ACRECFRS_SEAT_TAG)
    {
        [self setAcRecFrsSetSeatData];
    }
    else if(tag == ACRECFRS_RECFRS_TAG)
    {
        [self setAcRecFrsSetData];
    }
    else if(tag == ACVNT_SEAT_TAG)
    {
        [self setAcVentTempSetSeatData];
    }
    else if(tag == ACVNT_VNTTMP_TAG)
    {
        [self setAcVentTempSetData];
    }
    else if(tag == ADSRCDIR_SRC_TAG)
    {
        [self setAdSrcChgDirData];
    }
    else if(tag == ADSRC_NEXT_TAG)
    {
        [self setAdSrcChgData];
    }
    else if(tag == ADSKIPUPDN_SKIP_TAG)
    {
        [self setAdSkipUpDnData];
    }
    else if(tag == ADPLAY_PLAY_TAG)
    {
        [self setAdPlayData];
    }
    else if(tag == ADFFREW_FRREW_TAG)
    {
        [self setAdFfRewData];
    }
    else if(tag == ADSEEK_SEEK_TAG)
    {
        [self setAdSeekData];
    }
    else if(tag == ADSHUFFLE_MODE_TAG)
    {
        [self setAdShuffleData];
    }
    else if(tag == ADREPEAT_MODE_TAG)
    {
        [self setAdRepeatData];
    }
    else if(tag == ADVOL_UPDN_TAG)
    {
        [self setAdSetAudioVolData];
    }
    else if(tag == ADSCAN_SRC_TAG)
    {
        [self setAdScanSrcData];
    }
    else if(tag == ADSCAN_SCAN_TAG)
    {
        [self setAdScanData];
    }
    else if(tag == ADTUNE_SRC_TAG)
    {
        [self setAdTuneSrcData];
    }
    else if(tag == ADTUNE_UPDN_TAG)
    {
        [self setAdTuneData];
    }
    else if(tag == SETDISP_SRC_TAG)
    {
        [self setSetDispSrcData];
    }
    else if(tag == SETDISP_KIND_TAG)
    {
        [self setSetDispKindData];
    }
    else if(tag == SETDISP_UPDN_TAG)
    {
        [self setSetDispData];
    }
    else if(tag == SETVOL_KIND_TAG)
    {
        [self setSetVolKindData];
    }
    else if(tag == SETVOL_UPDN_TAG)
    {
        [self setSetVolData];
    }
    else if(tag == SETVOLDIR_KIND_TAG)
    {
        [self setSetVolDirKindData];
    }
    else if(tag == SETCTRLVOL_VOL_TAG)
    {
        [self setSetControlVolData];
    }
    else if(tag == SETTCHPNL_SNSTVTY_TAG)
    {
        [self setSetTouchPanelSensitivityData];
    }
    else if(tag == SETTONE_KIND_TAG)
    {
        [self setSetToneKindData];
    }
    else if(tag == SETTONE_UPDN_TAG)
    {
        [self setSetToneData];
    }
    else if(tag == SETTONEDIR_KIND_TAG)
    {
        [self setSetToneDirKindData];
    }
    else if(tag == SETFADER_BALANCE_TAG)
    {
        [self setSetFaderBalanceData];
    }
    else if(tag == SETFADER_UPDN_TAG)
    {
        [self setSetFaderBalanceUpDnData];
    }
    else if(tag == SETFADERDIR_BALANCE_TAG)
    {
        [self setSetFaderBalanceDirData];
    }
    else if(tag == SETSUBWOOFER_UPDN_TAG)
    {
        [self setSetSubwooferData];
    }
    else if(tag == SETSVC_SET_TAG)
    {
        [self setSetSVCData];
    }
    else if(tag == NOTIPOP_KIND_TAG)
    {
        [self setNotiPopupKindData];
    }
}

/* responseログ出力 */
- (void)writeResultLog:(NSString *)reqApiName :(NSString *)resultStr {
    NSDateFormatter* now_date = [[NSDateFormatter alloc] init];
    [now_date setDateFormat:@"[HH:mm:ss.SSS]"];
    NSString *date= [now_date stringFromDate:[NSDate date]];
    if(resultStr != nil)
    {
        NSString *rsltLogInfo = [NSString stringWithFormat:@"MdReq[%@]%@", reqApiName, resultStr];
        [logInfo write:rsltLogInfo];
        NSString *rsltAll = [NSString stringWithFormat: @"%@[%@]%@", date, reqApiName, resultStr];
        _mRtnTextView.text = rsltAll;
    }
    else{
        [logInfo write:@"MdReq[reqAirconVentTempSetting]Result=Empty"];
        NSString *rsltAll = [NSString stringWithFormat: @"%@[%@]Result=Empty", date, reqApiName];
        _mRtnTextView.text = rsltAll;
    }
}

/* Button音作動 */
- (void)invokeSystemSound {
    /* バイブレーション */
//    AudioServicesPlaySystemSound(SYSTEM_SOUNDID_VIBRATE_SHORT);
    /* タップ音 */
    AudioServicesPlaySystemSound(SYSTEM_SOUNDID_SEND);
}

- (IBAction)mAcClimStsSendButton:(id)sender {
    NSLog(@"reqAirconClimateStatus:seat=%ld:val=%ld", (long)acClimStsSeat, (long)acClimSts);
    NSString *rslt = [libEAPSample reqAirconClimateStatus :ACCESSKEY seat:acClimStsSeat climatestate:acClimSts];
    [self writeResultLog: @"reqAirconClimateStatus" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcAcsSetSendButton:(id)sender {
    NSLog(@"reqAirconACSetting:seat=%ld:val=%ld", (long)acAcSetSeat, (long)acAcSet);
    NSString *rslt = [libEAPSample reqAirconACSetting :ACCESSKEY seat:acAcSetSeat acstatus:acAcSet];
    [self writeResultLog: @"reqAirconACSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcModeSetSendButton:(id)sender {
    NSLog(@"reqAirconModeSetting:seat=%ld:val=%ld", (long)acModeSetSeat, (long)acModeSet);
    NSString *rslt = [libEAPSample reqAirconModeSetting :ACCESSKEY seat:acModeSetSeat modestatus:acModeSet];
    [self writeResultLog: @"reqAirconModeSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcFanUpDnSendButton:(id)sender {
    NSLog(@"reqAirconFanUpDown:seat=%ld:val=%ld", (long)acFanUpDnSeat, (long)acFanUpDn);
    NSString *rslt = [libEAPSample reqAirconFanUpDown :ACCESSKEY seat:acFanUpDnSeat fanupdown:acFanUpDn];
    [self writeResultLog: @"reqAirconFanUpDown" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcFanSetSendButton:(id)sender {
    NSLog(@"reqAirconFanSetting:seat=%ld:val=%ld", (long)acFanSetSeat, (long)acFanSet);
    NSString *rslt = [libEAPSample reqAirconFanSetting :ACCESSKEY seat:acFanSetSeat fanvalue:acFanSet];
    [self writeResultLog: @"reqAirconFanSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcTempUpDnSendButton:(id)sender {
    NSLog(@"reqAirconTempUpDown:seat=%ld:val=%ld", (long)acTempUpDnSeat, (long)acTempUpDn);
    NSString *rslt = [libEAPSample reqAirconTempUpDown :ACCESSKEY seat:acTempUpDnSeat tempupdown:acTempUpDn];
    [self writeResultLog: @"reqAirconTempUpDown" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcTempSetSendButton:(id)sender {
    NSInteger acTempSet = [_mAcTempSetText.text intValue];
    NSLog(@"reqAirconTempSetting:seat=%ld:val=%ld", (long)acTempSetSeat, (long)acTempSet);
    NSString *rslt = [libEAPSample reqAirconTempSetting :ACCESSKEY seat:acTempSetSeat tempvalue:acTempSet];
    [self writeResultLog: @"reqAirconTempSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcFrDefSetSendButton:(id)sender {
    NSLog(@"reqAirconFrDefSetting:seat=%ld:val=%ld", (long)acFrDefSetSeat, (long)acFrDefSet);
    NSString *rslt = [libEAPSample reqAirconFrDefSetting :ACCESSKEY seat:acFrDefSetSeat defstatus:acFrDefSet];
    [self writeResultLog: @"reqAirconFrDefSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcRrDefSetSendButton:(id)sender {
    NSLog(@"reqAirconRrDefSetting:seat=%ld:val=%ld", (long)acRrDefSetSeat, (long)acRrDefSet);
    NSString *rslt = [libEAPSample reqAirconRrDefSetting :ACCESSKEY seat:acRrDefSetSeat defstatus:acRrDefSet];
    [self writeResultLog: @"reqAirconRrDefSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcSyncSetSendButton:(id)sender {
    NSLog(@"reqAirconSyncSetting:seat=%ld:val=%ld", (long)acSyncSetSeat, (long)acSyncSet);
    NSString *rslt = [libEAPSample reqAirconSyncSetting :ACCESSKEY seat:acSyncSetSeat syncstatus:acSyncSet];
    [self writeResultLog: @"reqAirconSyncSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcRecFrsSetSendButton:(id)sender {
    NSLog(@"reqAirconRecFrsSetting:seat=%ld:val=%ld", (long)acRecFrsSetSeat, (long)acRecFrsSet);
    NSString *rslt = [libEAPSample reqAirconRecFrsSetting :ACCESSKEY seat:acRecFrsSetSeat recfrsstatus:acRecFrsSet];
    [self writeResultLog: @"reqAirconRecFrsSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAcVentTempSetSendButton:(id)sender {
    NSLog(@"reqAirconVentTempSetting:seat=%ld:val=%ld", (long)acVentTempSetSeat, (long)acVentTempSet);
    NSString *rslt = [libEAPSample reqAirconVentTempSetting :ACCESSKEY seat:acVentTempSetSeat venttemp:acVentTempSet];
    [self writeResultLog: @"reqAirconVentTempSetting" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSourceChangeDirectButton:(id)sender {
    NSLog(@"reqAudioSourceChangeDirect:src=%ld", (long)adSrcChgDir);
    NSString *rslt = [libEAPSample reqAudioSourceChangeDirect :ACCESSKEY source:adSrcChgDir];
    [self writeResultLog: @"reqAudioSourceChangeDirect" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSourceChangeButton:(id)sender {
    NSLog(@"reqAudioSourceChange:src=%ld", (long)adSrcChg);
    NSString *rslt = [libEAPSample reqAudioSourceChange :ACCESSKEY next:adSrcChg];
    [self writeResultLog: @"reqAudioSourceChange" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSkipUpDownButton:(id)sender {
    NSLog(@"reqAudioSkipUpDown:updn=%ld", (long)adSkipUpDn);
    NSString *rslt = [libEAPSample reqAudioSkipUpDown :ACCESSKEY skip:adSkipUpDn];
    [self writeResultLog: @"reqAudioSkipUpDown" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioPlayButton:(id)sender {
    NSLog(@"reqAudioPlay:play=%ld", (long)adPlay);
    NSString *rslt = [libEAPSample reqAudioPlay :ACCESSKEY play:adPlay];
    [self writeResultLog: @"reqAudioPlay" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioFfRewButton:(id)sender {
    NSLog(@"reqAudioFfRew:val=%ld", (long)adFfRew);
    NSString *rslt = [libEAPSample reqAudioFfRew :ACCESSKEY ffrew:adFfRew];
    [self writeResultLog: @"reqAudioFfRew" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSeekButton:(id)sender {
    NSLog(@"reqAudioSeek:val=%ld", (long)adSeek);
    NSString *rslt = [libEAPSample reqAudioSeek :ACCESSKEY seek:adSeek];
    [self writeResultLog: @"reqAudioSeek" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioShuffleButton:(id)sender {
    NSLog(@"reqAudioShuffle:val=%ld", (long)adShuffle);
    NSString *rslt = [libEAPSample reqAudioShuffle :ACCESSKEY shufflemode:adShuffle];
    [self writeResultLog: @"reqAudioShuffle" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioRepeatButton:(id)sender {
    NSLog(@"reqAudioRepeat:val=%ld", (long)adRepeat);
    NSString *rslt = [libEAPSample reqAudioRepeat :ACCESSKEY repeatmode:adRepeat];
    [self writeResultLog: @"reqAudioRepeat" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSetChannelSXMButton:(id)sender {
    NSInteger adSetChSXM = [_mAudioSetChannelSXMText.text intValue];
    NSLog(@"reqAudioSetChannelSXM:val=%ld", (long)adSetChSXM);
    NSString *rslt = [libEAPSample reqAudioSetChannelSXM :ACCESSKEY channelno:adSetChSXM];
    [self writeResultLog: @"reqAudioSetChannelSXM" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSetAudioVolButton:(id)sender {
    NSLog(@"reqAudioSetAudioVol:val=%ld", (long)adSetAudioVol);
    NSString *rslt = [libEAPSample reqAudioSetAudioVol :ACCESSKEY updown:adSetAudioVol];
    [self writeResultLog: @"reqAudioSetAudioVol" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioSetAudioVolDirectButton:(id)sender {
    NSInteger adSetAudioVolDir = [_mAudioSetAudioVolDirectText.text intValue];
    NSLog(@"reqAudioSetAudioVolDirect:val=%ld", (long)adSetAudioVolDir);
    NSString *rslt = [libEAPSample reqAudioSetAudioVolDirect :ACCESSKEY vol:adSetAudioVolDir];
    [self writeResultLog: @"reqAudioSetAudioVolDirect" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioScanButton:(id)sender {
    NSLog(@"reqAudioScan:vol1=%ld,vol2=%ld", (long)adScanSrc, (long)adScan);
    NSString *rslt = [libEAPSample reqAudioScan :ACCESSKEY source:adScanSrc scan:adScan];
    [self writeResultLog: @"reqAudioScan" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mAudioTuneButton:(id)sender {
    NSLog(@"reqAudioTune:val1=%ld,val2=%ld", (long)adTuneSrc, (long)adTune);
    NSString *rslt = [libEAPSample reqAudioTune :ACCESSKEY source:adTuneSrc updown:adTune];
    [self writeResultLog: @"reqAudioTune" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingDisplayButton:(id)sender {
    NSLog(@"reqSettingDisplay:val1=%ld,val2=%ld,val3=%ld", (long)setDispSrc, (long)setDispKind, (long)setDisp);
    NSString *rslt = [libEAPSample reqSettingDisplay :ACCESSKEY videosource:setDispSrc dispsetkind:setDispKind updown:setDisp];
    [self writeResultLog: @"reqSettingDisplay" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingVolumeButton:(id)sender {
    NSLog(@"reqSettingVolume:val1=%ld,val2=%ld", (long)setVolKind, (long)setVol);
    NSString *rslt = [libEAPSample reqSettingVolume :ACCESSKEY volkind:setVolKind updown:setVol];
    [self writeResultLog: @"reqSettingVolume" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingVolumeDirectButton:(id)sender {
    NSInteger setVolDir = [_mSettingVolumeDirectVolText.text intValue];
    NSLog(@"reqSettingVolumeDirect:val1=%ld,val2=%ld", (long)setVolDirKind, (long)setVolDir);
    NSString *rslt = [libEAPSample reqSettingVolumeDirect :ACCESSKEY volkind:setVolDirKind volume:setVolDir];
    [self writeResultLog: @"reqSettingVolumeDirect" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingControlVolButton:(id)sender {
    NSLog(@"reqSettingControlVol:val=%ld", (long)setControlVol);
    NSString *rslt = [libEAPSample reqSettingControlVol :ACCESSKEY volume:setControlVol];
    [self writeResultLog: @"reqSettingControlVol" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingTouchPanelSensitivityButton:(id)sender {
    NSLog(@"reqSettingTouchPanelSensitivity:val=%ld", (long)setTouchPanelSensitivity);
    NSString *rslt = [libEAPSample reqSettingTouchPanelSensitivity:ACCESSKEY sensitivity:setTouchPanelSensitivity];
    [self writeResultLog: @"reqSettingTouchPanelSensitivity" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingToneButton:(id)sender {
    NSLog(@"reqSettingTone:val1=%ld,val2=%ld", (long)setToneKind, (long)setTone);
    NSString *rslt = [libEAPSample reqSettingTone :ACCESSKEY tonekind:setToneKind updown:setTone];
    [self writeResultLog: @"reqSettingTone" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingToneDirectButton:(id)sender {
    NSInteger setToneDir = [_mSettingToneDirectSetText.text intValue];
    NSLog(@"reqSettingToneDirect:val1=%ld,val2=%ld", (long)setToneDirKind, (long)setToneDir);
    NSString *rslt = [libEAPSample reqSettingToneDirect :ACCESSKEY tonekind:setToneDirKind set:setToneDir];
    [self writeResultLog: @"reqSettingToneDirect" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingFaderBalanceButton:(id)sender {
    NSLog(@"reqSettingFaderBalance:val1=%ld,val2=%ld", (long)setFaderBalance, (long)setFaderBalanceUpDn);
    NSString *rslt = [libEAPSample reqSettingFaderBalance :ACCESSKEY faderbalance:setFaderBalance updownleftright:setFaderBalanceUpDn];
    [self writeResultLog: @"reqSettingFaderBalance" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingFaderBalanceDirectButton:(id)sender {
    NSInteger setFaderBalanceDirSet = [_mSettingFaderBalanceDirectSetText.text intValue];
    NSLog(@"reqSettingFaderBalanceDirect:val1=%ld,val2=%ld",
          (long)setFaderBalanceDir,
          (long)setFaderBalanceDirSet);
    NSString *rslt = [libEAPSample reqSettingFaderBalanceDirect :ACCESSKEY
                        faderbalance:setFaderBalanceDir
                        set:setFaderBalanceDirSet];
    [self writeResultLog: @"reqSettingFaderBalanceDirect" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingSubwooferButton:(id)sender {
    NSLog(@"reqSettingSubwoofer:val=%ld", (long)setSubwoofer);
    NSString *rslt = [libEAPSample reqSettingSubwoofer :ACCESSKEY updown:setSubwoofer];
    [self writeResultLog: @"reqSettingSubwoofer" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingSubwooferDirectButton:(id)sender {
    NSInteger setSubwooferDir = [_mSettingSubwooferDirectText.text intValue];
    NSLog(@"reqSettingSubwooferDirect:val=%ld", (long)setSubwooferDir);
    NSString *rslt = [libEAPSample reqSettingSubwooferDirect :ACCESSKEY set:setSubwooferDir];
    [self writeResultLog: @"reqSettingSubwooferDirect" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mSettingSVCButton:(id)sender {
    NSLog(@"reqSettingSVC:svc=%ld", (long)setSVC);
    NSString *rslt = [libEAPSample reqSettingSVC :ACCESSKEY set:setSVC];
    [self writeResultLog: @"reqSettingSVC" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mNaviDestinationLatLonButton:(id)sender {
    NSString *latitude;
    NSString *longitude;
    NSString *destination;
    NSLog(@"reqNaviDestinationLatLon:lat=%@,long=%@,name=%@",
          self.mNaviDestinationLatitudeText.text,
          self.mNaviDestinationLongitudeText.text,
          self.mNaviDestinationNameTextView.text);
    if( [self.mNaviDestinationLatitudeText.text length] == 0 ) {
        latitude = nil;
    }
    else {
        latitude = self.mNaviDestinationLatitudeText.text;
    }
    if( [self.mNaviDestinationLongitudeText.text length] == 0 ) {
        longitude = nil;
    }
    else {
        longitude = self.mNaviDestinationLongitudeText.text;
    }
    if( [self.mNaviDestinationNameTextView.text length] == 0 ) {
        destination = nil;
    }
    else {
        destination = self.mNaviDestinationNameTextView.text;
    }
    NSString *rslt = [libEAPSample reqNaviDestinationLatLon :ACCESSKEY
                                                  latitude:latitude
                                                  longitude:longitude
                                                  destname:destination];
    [self writeResultLog: @"reqNaviDestinationLatLon" :rslt];
    [self invokeSystemSound];
}

- (IBAction)mNotificationPopupButton:(id)sender {
    NSLog(@"reqNotificationPopup:kind=%ld,word=%@", (long)notiPopupKind, self.mNotificationPopupWordTextView.text);
    NSString *rslt;
    if( [self.mNotificationPopupWordTextView.text length] == 0 ) {
        rslt = [libEAPSample reqNotificationPopup :ACCESSKEY kind:notiPopupKind wording:nil];
    }
    else {
        rslt = [libEAPSample reqNotificationPopup :ACCESSKEY kind:notiPopupKind wording:self.mNotificationPopupWordTextView.text];
    }
    [self writeResultLog: @"reqNotificationPopup" :rslt];
    [self invokeSystemSound];
}

- (void) notifyResData:(NSString *)resdata {
    NSLog(@"notifyResData");
    
    self.resDataString = resdata;
    if ( [self.delegate respondsToSelector:@selector(didHandleResData:)]) {
        [self.delegate didHandleResData:self.resDataString];
    }
}

#ifdef __STAND_ALONE__
- (void) notifyMdTestLogData:(NSString *)logdata {
//    NSLog(@"notifyMdTestLogData");
    
    if ( [self.delegateTest respondsToSelector:@selector(didHandleLogData:)]) {
        [self.delegateTest didHandleLogData:logdata];
    }
}
#endif /* __STAND_ALONE__ */

@end
