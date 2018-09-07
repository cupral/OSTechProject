//
//  ViewControllerEventRes.m
//  EAPSampleApp
//
//

#import <AudioToolbox/AudioToolbox.h>
#import "ViewControllerEventRes.h"
#import "ViewController.h"
#import "EAPLog.h"

@interface ViewControllerEventRes () <ViewControllerDelegate>
@end

@implementation ViewControllerEventRes {
    NSUInteger resCommandId;
    NSUInteger resSrcKind;
    
    NSArray *adGetPresetListSrcData;
    NSArray *adSetPresetListAmFmSrcData;
    NSArray *adSetPresetListAmFmPresetData;
    NSArray *adSetPresetListSxmSrcData;
    NSArray *adGetStationListAmFmSrcData;
    
    NSMutableDictionary *tagDic;
    NSMutableDictionary *resDataDic;
    NSMutableDictionary *presetPageDic;
    NSMutableDictionary *presetNoDic;
    NSMutableDictionary *presetNoSxmDic;
    NSMutableDictionary *stationFreqDic;
    
    NSInteger adGetPresetListSrc;
    NSInteger adSetPresetListAmFmSrc;
    NSInteger adSetPresetListAmFmPreset;
    NSInteger adSetPresetListSxmSrc;
    NSInteger adGetStationListAmFmSrc;

#ifdef __DEBUG_ON__
    NSString *dbgStrPresetAm;
    NSString *dbgStrPresetFm;
    NSString *dbgStrPresetSxm;
    NSString *dbgStrStationAm;
    NSString *dbgStrStationFm;
#endif
}

static libMDEAPframework* libEAPSampleEvtRes = nil;
static EAPLog *loginfoMdRes = nil;


- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    NSLog(@"viewDidLoad of EventRes");
    
    libEAPSampleEvtRes = [libMDEAPframework sharedController];

    if( viewController != nil) {
        viewController.delegate = self;
    }

    loginfoMdRes = [EAPLog sharedLogInfo];
    [loginfoMdRes output_sw:TRUE];
    
    self.mCodeLabel.text = @"";
    self.mCommandLabel.text = @"";
    self.mCountLabel.text = @"";
    self.mAudioSrcLabel.text = @"";
    
    self.mMdReqRtnTextView.editable = NO;
    self.mMdReqRtnTextView.text = @"NO RESULT DATA";
    
    self.mMdResTableView.estimatedRowHeight = 30;
    self.mMdResTableView.rowHeight = UITableViewAutomaticDimension;
    
    resCommandId = 0;
    resSrcKind = 0;
    
    adGetPresetListSrcData = @[@[@"0",@"AM[0]"],
                               @[@"1",@"FM[1]"],
                               @[@"2",@"SXM[2]"]];
    
    adSetPresetListAmFmSrcData = @[@[@"0",@"AM[0]"],
                                   @[@"1",@"FM[1]"]];
    
    adSetPresetListAmFmPresetData = @[@[@"1",@"1[1]"],
                                      @[@"2",@"2[2]"]];
    
    adSetPresetListSxmSrcData = @[@[@"2",@"SXM[2]"]];
    
    adGetStationListAmFmSrcData = @[@[@"0",@"AM[0]"],
                                    @[@"1",@"FM[1]"]];
    
    tagDic = [[NSMutableDictionary alloc] init];
    resDataDic = [[NSMutableDictionary alloc] init];
    presetPageDic = [[NSMutableDictionary alloc] init];
    presetNoDic = [[NSMutableDictionary alloc] init];
    presetNoSxmDic = [[NSMutableDictionary alloc] init];
    stationFreqDic = [[NSMutableDictionary alloc] init];
    
    // Keyboard toolbar
    self.keyboardToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0,0,self.view.bounds.size.width, 38.0f)];
    self.keyboardToolbar.barStyle = UIBarStyleBlackTranslucent;
    
    UIBarButtonItem *spaceBarItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem *doneBarItem = [[UIBarButtonItem alloc] initWithTitle:@"決定" style:UIBarButtonItemStyleDone target:self action:@selector(resignKeyboard:)];
    [self.keyboardToolbar setItems:@[spaceBarItem, doneBarItem]];
    
    // [21]AudioGetPresetlist source Picker View
    self.mAdGetPresetListSrcPicker = [[UIPickerView alloc] init];
    self.mAdGetPresetListSrcPicker.delegate = self;
    self.mAdGetPresetListSrcPicker.tag = ADGETPRESET_SRC_TAG;
    self.mAdGetPresetListSrcPicker.showsSelectionIndicator = YES;
    // [21]AudioGetPresetlist source TextField
    self.mAdGetPresetListSrcText.delegate = self;
    self.mAdGetPresetListSrcText.tag = ADGETPRESET_SRC_TAG;
    self.mAdGetPresetListSrcText.inputAccessoryView = self.keyboardToolbar;
    self.mAdGetPresetListSrcText.inputView = self.mAdGetPresetListSrcPicker;
    
    // [24]AudioSetPresetList(AM/FM) source Picker View
    self.mAdSetPresetListAmFmSrcPicker = [[UIPickerView alloc] init];
    self.mAdSetPresetListAmFmSrcPicker.delegate = self;
    self.mAdSetPresetListAmFmSrcPicker.tag = ADSETPRESETAMFM_SRC_TAG;
    self.mAdSetPresetListAmFmSrcPicker.showsSelectionIndicator = YES;
    // [24]AudioSetPresetList(AM/FM) presetno Picker View
    self.mAdSetPresetListAmFmPresetPicker = [[UIPickerView alloc] init];
    self.mAdSetPresetListAmFmPresetPicker.delegate = self;
    self.mAdSetPresetListAmFmPresetPicker.tag = ADSETPRESETAMFM_PRESET_TAG;
    self.mAdSetPresetListAmFmPresetPicker.showsSelectionIndicator = YES;
    // [24]AudioSetPresetList(AM/FM) source Text Field
    self.mAdSetPresetListAmFmSrcText.delegate = self;
    self.mAdSetPresetListAmFmSrcText.tag = ADSETPRESETAMFM_SRC_TAG;
    self.mAdSetPresetListAmFmSrcText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListAmFmSrcText.inputView = self.mAdSetPresetListAmFmSrcPicker;
    // [24]AudioSetPresetList(AM/FM) preset Text Field
    self.mAdSetPresetListAmFmPresetText.delegate = self;
    self.mAdSetPresetListAmFmPresetText.tag = ADSETPRESETAMFM_PRESET_TAG;
    self.mAdSetPresetListAmFmPresetText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListAmFmPresetText.inputView = self.mAdSetPresetListAmFmPresetPicker;
    // [24]AudioSetPresetList(AM/FM) presetno Text Field
    self.mAdSetPresetListAmFmPresetNoText.delegate = self;
    self.mAdSetPresetListAmFmPresetNoText.tag = ADSETPRESETAMFM_PRESETNO_TAG;
    self.mAdSetPresetListAmFmPresetNoText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListAmFmPresetNoText.keyboardType = UIKeyboardTypeNumberPad;
    // [24]AudioSetPresetList(AM/FM) frequency Text Field
    self.mAdSetPresetListAmFmFreqText.delegate = self;
    self.mAdSetPresetListAmFmFreqText.tag =ADSETPRESETAMFM_FREQ_TAG;
    self.mAdSetPresetListAmFmFreqText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListAmFmFreqText.keyboardType = UIKeyboardTypeNumberPad;
    
    // [25]AudioSetPresetList(SXM) source Picker View
    self.mAdSetPresetListSxmSrcPicker = [[UIPickerView alloc] init];
    self.mAdSetPresetListSxmSrcPicker.delegate = self;
    self.mAdSetPresetListSxmSrcPicker.tag = ADSETPRESETSXM_SRC_TAG;
    self.mAdSetPresetListSxmSrcPicker.showsSelectionIndicator = YES;
    // [25]AudioSetPresetList(SXM) srouce Text Field
    self.mAdSetPresetListSxmSrcText.delegate = self;
    self.mAdSetPresetListSxmSrcText.tag = ADSETPRESETSXM_SRC_TAG;
    self.mAdSetPresetListSxmSrcText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListSxmSrcText.inputView = self.mAdSetPresetListSxmSrcPicker;
    // [25]AudioSetPresetList(SXM) PresetNo Text Field
    self.mAdSetPresetListSxmPresetNoText.delegate = self;
    self.mAdSetPresetListSxmPresetNoText.tag = ADSETPRESETSXM_PRESETNO_TAG;
    self.mAdSetPresetListSxmPresetNoText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListSxmPresetNoText.keyboardType = UIKeyboardTypeNumberPad;
    // [25]AudioSetPresetList(SXM) ChannelNo Text Field
    self.mAdSetPresetListSxmChannelText.delegate = self;
    self.mAdSetPresetListSxmChannelText.tag = ADSETPRESETSXM_CHANNEL_TAG;
    self.mAdSetPresetListSxmChannelText.inputAccessoryView = self.keyboardToolbar;
    self.mAdSetPresetListSxmChannelText.keyboardType = UIKeyboardTypeNumberPad;
    
    // [31]AudioGetStationListAM/FM) source Picker View
    self.mAdGetStationListAmFmSrcPicker = [[UIPickerView alloc] init];
    self.mAdGetStationListAmFmSrcPicker.delegate = self;
    self.mAdGetStationListAmFmSrcPicker.tag = ADSETSTATIONAMFM_SRC_TAG;
    self.mAdGetStationListAmFmSrcPicker.showsSelectionIndicator = YES;
    // [31]AudioGetStationListAM/FM) source Text Field
    self.mAdGetStationListAmFmSrcText.delegate = self;
    self.mAdGetStationListAmFmSrcText.tag = ADSETSTATIONAMFM_SRC_TAG;
    self.mAdGetStationListAmFmSrcText.inputAccessoryView = self.keyboardToolbar;
    self.mAdGetStationListAmFmSrcText.inputView = self.mAdGetStationListAmFmSrcPicker;

#ifdef __DEBUG_ON__
    dbgStrPresetAm = @"{\"result\":\"Success\",\"code\":0,\"command\":3008,\"count\":13, \
        \"no1\":{\"data1\":0}, \
        \"no2\":{\"data1\":1,\"data2\":1,\"data3\":\"100kHz\"}, \
        \"no3\":{\"data1\":1,\"data2\":2,\"data3\":\"200kHz\"}, \
        \"no4\":{\"data1\":1,\"data2\":3,\"data3\":\"300kHz\"}, \
        \"no5\":{\"data1\":1,\"data2\":4,\"data3\":\"400kHz\"}, \
        \"no6\":{\"data1\":1,\"data2\":5,\"data3\":\"500kHz\"}, \
        \"no7\":{\"data1\":1,\"data2\":6,\"data3\":\"600kHz\"}, \
        \"no8\":{\"data1\":2,\"data2\":1,\"data3\":\"700kHz\"}, \
        \"no9\":{\"data1\":2,\"data2\":2,\"data3\":\"800kHz\"}, \
        \"no10\":{\"data1\":2,\"data2\":3,\"data3\":\"900kHz\"}, \
        \"no11\":{\"data1\":2,\"data2\":4,\"data3\":\"1000kHz\"}, \
        \"no12\":{\"data1\":2,\"data2\":5,\"data3\":\"1100kHz\"}, \
        \"no13\":{\"data1\":2,\"data2\":6,\"data3\":\"1200kHz\"}}";
    
    dbgStrPresetFm = @"{\"result\":\"Success\",\"code\":0,\"command\":3008,\"count\":4, \
        \"no1\":{\"data1\":1}, \
        \"no2\":{\"data1\":1,\"data2\":1,\"data3\":\"100kHz\"}, \
        \"no3\":{\"data1\":1,\"data2\":2,\"data3\":\"200kHz\"}, \
        \"no4\":{\"data1\":1,\"data2\":3,\"data3\":\"300kHz\"}}"; \
    
    dbgStrPresetSxm = @"{\"result\":\"Success\",\"code\":0,\"command\":3010,\"count\":13, \
        \"no1\":{\"data1\":2}, \
        \"no2\":{\"data1\":1,\"data2\":100,\"data3\":\"チャンネル名1\"}, \
        \"no3\":{\"data1\":2,\"data2\":150,\"data3\":\"チャンネル名2\"}, \
        \"no4\":{\"data1\":3,\"data2\":200,\"data3\":\"チャンネル名3\"}, \
        \"no5\":{\"data1\":4,\"data2\":250,\"data3\":\"チャンネル名4\"}, \
        \"no6\":{\"data1\":5,\"data2\":300,\"data3\":\"チャンネル名5\"}, \
        \"no7\":{\"data1\":6,\"data2\":350,\"data3\":\"チャンネル名6\"}, \
        \"no8\":{\"data1\":7,\"data2\":400,\"data3\":\"チャンネル名7\"}, \
        \"no9\":{\"data1\":8,\"data2\":450,\"data3\":\"チャンネル名8\"}, \
        \"no10\":{\"data1\":9,\"data2\":500,\"data3\":\"チャンネル名9\"}, \
        \"no11\":{\"data1\":10,\"data2\":550,\"data3\":\"チャンネル名10\"}, \
        \"no12\":{\"data1\":11,\"data2\":600,\"data3\":\"チャンネル名11\"}, \
        \"no13\":{\"data1\":12,\"data2\":650,\"data3\":\"チャンネル名12\"}}";
    
    dbgStrStationAm = @"{\"result\":\"Success\",\"code\":0,\"command\":3016,\"count\":51, \
        \"no1\":{\"data1\":0}, \
        \"no2\":{\"data1\":530,\"data2\":\"放送局名1\",\"data3\":\"周波数情報1\"}, \
        \"no3\":{\"data1\":600,\"data2\":\"放送局名2\",\"data3\":\"周波数情報2\"}, \
        \"no4\":{\"data1\":670,\"data2\":\"放送局名3\",\"data3\":\"周波数情報3\"}, \
        \"no5\":{\"data1\":740,\"data2\":\"放送局名4\",\"data3\":\"周波数情報4\"}, \
        \"no6\":{\"data1\":810,\"data2\":\"放送局名5\",\"data3\":\"周波数情報5\"}, \
        \"no7\":{\"data1\":880,\"data2\":\"放送局名6\",\"data3\":\"周波数情報6\"}, \
        \"no8\":{\"data1\":950,\"data2\":\"放送局名7\",\"data3\":\"周波数情報7\"}, \
        \"no9\":{\"data1\":1020,\"data2\":\"放送局名8\",\"data3\":\"周波数情報8\"}, \
        \"no10\":{\"data1\":1090,\"data2\":\"放送局名9\",\"data3\":\"周波数情報9\"}, \
        \"no11\":{\"data1\":1160,\"data2\":\"放送局名10\",\"data3\":\"周波数情報10\"}, \
        \"no12\":{\"data1\":1230,\"data2\":\"放送局名11\",\"data3\":\"周波数情報11\"}, \
        \"no13\":{\"data1\":1300,\"data2\":\"放送局名12\",\"data3\":\"周波数情報12\"}, \
        \"no14\":{\"data1\":1370,\"data2\":\"放送局名13\",\"data3\":\"周波数情報13\"}, \
        \"no15\":{\"data1\":1440,\"data2\":\"放送局名14\",\"data3\":\"周波数情報14\"}, \
        \"no16\":{\"data1\":1510,\"data2\":\"放送局名15\",\"data3\":\"周波数情報15\"}, \
        \"no17\":{\"data1\":1580,\"data2\":\"放送局名16\",\"data3\":\"周波数情報16\"}, \
        \"no18\":{\"data1\":1650,\"data2\":\"放送局名17\",\"data3\":\"周波数情報17\"}, \
        \"no19\":{\"data1\":1720,\"data2\":\"放送局名18\",\"data3\":\"周波数情報18\"}, \
        \"no20\":{\"data1\":1790,\"data2\":\"放送局名19\",\"data3\":\"周波数情報19\"}, \
        \"no21\":{\"data1\":1860,\"data2\":\"放送局名20\",\"data3\":\"周波数情報20\"}, \
        \"no22\":{\"data1\":1930,\"data2\":\"放送局名21\",\"data3\":\"周波数情報21\"}, \
        \"no23\":{\"data1\":2000,\"data2\":\"放送局名22\",\"data3\":\"周波数情報22\"}, \
        \"no24\":{\"data1\":2070,\"data2\":\"放送局名23\",\"data3\":\"周波数情報23\"}, \
        \"no25\":{\"data1\":2140,\"data2\":\"放送局名24\",\"data3\":\"周波数情報24\"}, \
        \"no26\":{\"data1\":2210,\"data2\":\"放送局名25\",\"data3\":\"周波数情報25\"}, \
        \"no27\":{\"data1\":2280,\"data2\":\"放送局名26\",\"data3\":\"周波数情報26\"}, \
        \"no28\":{\"data1\":2350,\"data2\":\"放送局名27\",\"data3\":\"周波数情報27\"}, \
        \"no29\":{\"data1\":2420,\"data2\":\"放送局名28\",\"data3\":\"周波数情報28\"}, \
        \"no30\":{\"data1\":2490,\"data2\":\"放送局名29\",\"data3\":\"周波数情報29\"}, \
        \"no31\":{\"data1\":2560,\"data2\":\"放送局名30\",\"data3\":\"周波数情報30\"}, \
        \"no32\":{\"data1\":2630,\"data2\":\"放送局名31\",\"data3\":\"周波数情報31\"}, \
        \"no33\":{\"data1\":2700,\"data2\":\"放送局名32\",\"data3\":\"周波数情報32\"}, \
        \"no34\":{\"data1\":2770,\"data2\":\"放送局名33\",\"data3\":\"周波数情報33\"}, \
        \"no35\":{\"data1\":2840,\"data2\":\"放送局名34\",\"data3\":\"周波数情報34\"}, \
        \"no36\":{\"data1\":2910,\"data2\":\"放送局名35\",\"data3\":\"周波数情報35\"}, \
        \"no37\":{\"data1\":2980,\"data2\":\"放送局名36\",\"data3\":\"周波数情報36\"}, \
        \"no38\":{\"data1\":3050,\"data2\":\"放送局名37\",\"data3\":\"周波数情報37\"}, \
        \"no39\":{\"data1\":3120,\"data2\":\"放送局名38\",\"data3\":\"周波数情報38\"}, \
        \"no40\":{\"data1\":3190,\"data2\":\"放送局名39\",\"data3\":\"周波数情報39\"}, \
        \"no41\":{\"data1\":3260,\"data2\":\"放送局名40\",\"data3\":\"周波数情報40\"}, \
        \"no42\":{\"data1\":3330,\"data2\":\"放送局名41\",\"data3\":\"周波数情報41\"}, \
        \"no43\":{\"data1\":3400,\"data2\":\"放送局名42\",\"data3\":\"周波数情報42\"}, \
        \"no44\":{\"data1\":3470,\"data2\":\"放送局名43\",\"data3\":\"周波数情報43\"}, \
        \"no45\":{\"data1\":3540,\"data2\":\"放送局名44\",\"data3\":\"周波数情報44\"}, \
        \"no46\":{\"data1\":3610,\"data2\":\"放送局名45\",\"data3\":\"周波数情報45\"}, \
        \"no47\":{\"data1\":3680,\"data2\":\"放送局名46\",\"data3\":\"周波数情報46\"}, \
        \"no48\":{\"data1\":3750,\"data2\":\"放送局名47\",\"data3\":\"周波数情報47\"}, \
        \"no49\":{\"data1\":3820,\"data2\":\"放送局名48\",\"data3\":\"周波数情報48\"}, \
        \"no50\":{\"data1\":3890,\"data2\":\"放送局名49\",\"data3\":\"周波数情報49\"}, \
        \"no51\":{\"data1\":3960,\"data2\":\"放送局名50\",\"data3\":\"周波数情報50\"}}";
    
    dbgStrStationFm = @"{\"result\":\"Success\",\"code\":0,\"command\":3016,\"count\":7,\"no1\":{\"data1\":1}, \
        \"no2\":{\"data1\":530,\"data2\":\"放送局名1\",\"data3\":\"周波数情報1\"}, \
        \"no3\":{\"data1\":600,\"data2\":\"放送局名2\",\"data3\":\"周波数情報2\"}, \
        \"no4\":{\"data1\":670,\"data2\":\"放送局名3\",\"data3\":\"周波数情報3\"}, \
        \"no5\":{\"data1\":740,\"data2\":\"放送局名4\",\"data3\":\"周波数情報4\"}, \
        \"no6\":{\"data1\":810,\"data2\":\"放送局名5\",\"data3\":\"周波数情報5\"}, \
        \"no51\":{\"data1\":777,\"data2\":\"放送局名6\",\"data3\":\"周波数情報6\"}}";
#endif
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 0.1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [tagDic count];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *tag = (NSString *)[tagDic objectForKey:[NSString stringWithFormat:@"%ld", (long)indexPath.row]];
    NSString *presetPageStr;
    NSInteger presetPage;
    NSString *presetNoStr;
    NSInteger presetNo;
    NSString *presetNoSxmStr;
    NSInteger presetNoSxm;
    NSString *freqStr;
    NSInteger freq;
    NSString *rslt;
    switch ( resCommandId ) {
        case CMDID_RES_GET_PRESET_LIST:
        case CMDID_RES_SET_PRESET_LIST:
            switch( resSrcKind ) {
                case AUDIO_SRC_KIND_AM:
                case AUDIO_SRC_KIND_FM:
                    presetPageStr = (NSString *)[presetPageDic objectForKey:tag];
                    presetPage = [presetPageStr integerValue];
                    presetNoStr = (NSString *)[presetNoDic objectForKey:tag];
                    presetNo = [presetNoStr integerValue];
                    NSLog(@"reqAudioSelectPresetList:src=%ld,page=%ld,presetno=%ld", (long)resSrcKind, (long)presetPage, (long)presetNo);
                    rslt = [libEAPSampleEvtRes reqAudioSelectPresetList:ACCESSKEY source:resSrcKind preset:presetPage presetno:presetNo];
                    [self writeResultLog: @"reqAudioSelectPresetList" :rslt];
                    [self invokeSystemSound];
                    break;
                case AUDIO_SRC_KIND_SXM:
                    presetNoSxmStr = (NSString *)[presetNoSxmDic objectForKey:tag];
                    presetNoSxm = [presetNoSxmStr integerValue];
                    NSLog(@"reqAudioSelectPresetListSXM:src=%ld,presetno=%ld", (long)resSrcKind, (long)presetNoSxm);
                    rslt = [libEAPSampleEvtRes reqAudioSelectPresetListSXM:ACCESSKEY source:resSrcKind presetno:presetNoSxm];
                    [self writeResultLog: @"reqAudioSelectPresetListSXM" :rslt];
                    [self invokeSystemSound];
                    break;
                default:
                    NSLog(@"Invalid resSrcKind=%ld", (long)resSrcKind);
                    break;
            }
            break;
        case CMDID_RES_GET_STATION_LIST:
            freqStr = (NSString *)[stationFreqDic objectForKey:tag];
            freq = [freqStr integerValue];
            NSLog(@"reqAudioFreqDirect:src=%ld,freq=%ld", (long)resSrcKind, (long)freq);
            rslt = [libEAPSampleEvtRes reqAudioFreqDirect:ACCESSKEY source:resSrcKind freq:freq];
            [self writeResultLog: @"reqAudioFreqDirect" :rslt];
            [self invokeSystemSound];
            break;
        default:
            NSLog(@"Invalid resCommandId=%ld", (long)resCommandId);
            break;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *Identifier = @"tableCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:Identifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:Identifier];
    }
    NSString *rowStr = [NSString stringWithFormat:@"%ld", (long)indexPath.row];
    NSString *tag = [tagDic objectForKey: rowStr];
    NSString *resDataStr = [resDataDic objectForKey:tag];
    
    UILabel *label = cell.textLabel;
    label.font = [UIFont fontWithName:@"AppleGothic" size:12];
//    label.font = [UIFont systemFontOfSize:13];
    label.numberOfLines = 0;
    label.text = resDataStr;
    NSLog(@"cell = %@", cell);
    
    return cell;
}

- (void) didHandleResData:(NSString *)resdata {
    NSLog(@"notifyResData");
    
    NSString *jsonString = [resdata copy];

    if (jsonString) {
        [loginfoMdRes write_appsend:@"MdRes" :jsonString];

        NSData *jsonData = [jsonString dataUsingEncoding:NSUnicodeStringEncoding];
        NSError *error;
        NSDictionary *jsonDic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingAllowFragments error:&error];

        NSString *code = (NSString *)[jsonDic objectForKey:@"code"];
        self.mCodeLabel.text = [NSString stringWithFormat:@"%@", code];
        NSString *command = (NSString *)[jsonDic objectForKey:@"command"];
        self.mCommandLabel.text = [NSString stringWithFormat:@"%@", command];
        resCommandId = [command integerValue];
        NSString *count = (NSString *)[jsonDic objectForKey:@"count"];
        self.mCountLabel.text = [NSString stringWithFormat:@"%@", count];
        NSDictionary *source = (NSDictionary *)[jsonDic objectForKey:@"no1"];
        NSString *audioSouce = (NSString *)[source objectForKey:@"data1"];
        self.mAudioSrcLabel.text = [NSString stringWithFormat:@"%@", audioSouce];
        resSrcKind = [audioSouce integerValue];

        [tagDic removeAllObjects];
        [resDataDic removeAllObjects];
        [presetPageDic removeAllObjects];
        [presetNoDic removeAllObjects];
        [presetNoSxmDic removeAllObjects];
        [stationFreqDic removeAllObjects];
        NSInteger rowCnt = 0;
    	NSInteger maxCnt = [jsonDic count] - 4;
        for(NSInteger cnt = 0; cnt < maxCnt; cnt++) {
        	NSString *tag = [NSString stringWithFormat:@"no%ld", (long)(cnt + 2)];
            NSDictionary *data = (NSDictionary *)[jsonDic objectForKey:tag];
            if( data != nil) {
                NSString *rowStr = [NSString stringWithFormat:@"%ld", (long)rowCnt];
                [tagDic setObject:tag forKey:rowStr];
                
                NSData *w_data = [NSJSONSerialization dataWithJSONObject:data options:2 error:&error];
                NSString *w_string = [[NSString alloc]initWithData:w_data encoding:NSUTF8StringEncoding];
                NSString *resDataStr;
                if(tag.length == TAG_LEN_THREE) {
                    resDataStr = [NSString stringWithFormat:@"%@     %@",tag, w_string];
                }
                else {
                    resDataStr = [NSString stringWithFormat:@"%@   %@",tag, w_string];
                }
                [resDataDic setObject:resDataStr forKey:tag];

                NSString *data1 = (NSString *)[data objectForKey:@"data1"];
                NSString *data2 = (NSString *)[data objectForKey:@"data2"];
                NSString *data3 = (NSString *)[data objectForKey:@"data3"];
                NSLog(@"Find Data:%@:Data1=%@,Data2=%@,Data3=%@",tag, data1, data2, data3);
                switch ( resCommandId ) {
                    case CMDID_RES_GET_PRESET_LIST:
                    case CMDID_RES_SET_PRESET_LIST:
                        switch( resSrcKind ) {
                            case AUDIO_SRC_KIND_AM:
                            case AUDIO_SRC_KIND_FM:
                                [presetPageDic setObject:data1 forKey:tag];
                                [presetNoDic setObject:data2 forKey:tag];
                                break;
                            case AUDIO_SRC_KIND_SXM:
                                [presetNoSxmDic setObject:data1 forKey:tag];
                                break;
                            default:
                                NSLog(@"Invalid resSrcKind=%ld", (long)resSrcKind);
                                break;
                        }
                        break;
                    case CMDID_RES_GET_STATION_LIST:
                        [stationFreqDic setObject:data1 forKey:tag];
                        break;
                    default:
                        NSLog(@"Invalid resCommandId=%ld", (long)resCommandId);
                        break;
                }
                rowCnt++;
            }
        }
        [self.mMdResTableView reloadData];
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
    if([self.mAdGetPresetListSrcText isFirstResponder])
    {
        return self.mAdGetPresetListSrcText;
    }
    else if([self.mAdSetPresetListAmFmSrcText isFirstResponder])
    {
        return self.mAdSetPresetListAmFmSrcText;
    }
    else if([self.mAdSetPresetListAmFmPresetText isFirstResponder])
    {
        return self.mAdSetPresetListAmFmPresetText;
    }
    else if([self.mAdSetPresetListAmFmPresetNoText isFirstResponder])
    {
        return self.mAdSetPresetListAmFmPresetNoText;
    }
    else if([self.mAdSetPresetListAmFmFreqText isFirstResponder])
    {
        return self.mAdSetPresetListAmFmFreqText;
    }
    else if([self.mAdSetPresetListSxmSrcText isFirstResponder])
    {
        return self.mAdSetPresetListSxmSrcText;
    }
    else if([self.mAdSetPresetListSxmPresetNoText isFirstResponder])
    {
        return self.mAdSetPresetListSxmPresetNoText;
    }
    else if([self.mAdSetPresetListSxmChannelText isFirstResponder])
    {
        return self.mAdSetPresetListSxmChannelText;
    }
    else if([self.mAdGetStationListAmFmSrcText isFirstResponder])
    {
        return self.mAdGetStationListAmFmSrcText;
    }
    return nil;
}

/*
 * AccessoryViewがピッカーのText Field選択時に初期値を設定
 */
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    NSUInteger tag = [textField tag];
    [self checkSpecialFields:tag];
}

- (void)checkSpecialFields:(NSInteger)tag
{
    if(tag == ADGETPRESET_SRC_TAG && [self.mAdGetPresetListSrcText.text isEqualToString:@""])
    {
        [self setAdGetPresetListSrcData];
    }
    else if(tag == ADSETPRESETAMFM_SRC_TAG && [self.mAdSetPresetListAmFmSrcText.text isEqualToString:@""])
    {
        [self setAdSetPresetListAmFmSrcData];
    }
    else if(tag == ADSETPRESETAMFM_PRESET_TAG && [self.mAdSetPresetListAmFmPresetText.text isEqualToString:@""])
    {
        [self setAdSetPresetListAmFmPresetData];
    }
    else if(tag == ADSETPRESETSXM_SRC_TAG && [self.mAdSetPresetListSxmSrcText.text isEqualToString:@""])
    {
        [self setAdSetPresetListSxmSrcData];
    }
    else if(tag == ADSETSTATIONAMFM_SRC_TAG && [self.mAdGetStationListAmFmSrcText.text isEqualToString:@""])
    {
        [self setAdGetStationListAmFmSrcData];
    }
}

- (void)setAdGetPresetListSrcData
{
    NSInteger row = [self.mAdGetPresetListSrcPicker selectedRowInComponent:0];
    self.mAdGetPresetListSrcText.text = adGetPresetListSrcData[row][1];
    adGetPresetListSrc = [adGetPresetListSrcData[row][0] intValue];
}

- (void)setAdSetPresetListAmFmSrcData
{
    NSInteger row = [self.mAdSetPresetListAmFmSrcPicker selectedRowInComponent:0];
    self.mAdSetPresetListAmFmSrcText.text = adSetPresetListAmFmSrcData[row][1];
    adSetPresetListAmFmSrc = [adSetPresetListAmFmSrcData[row][0] intValue];
}

- (void)setAdSetPresetListAmFmPresetData
{
    NSInteger row = [self.mAdSetPresetListAmFmPresetPicker selectedRowInComponent:0];
    self.mAdSetPresetListAmFmPresetText.text = adSetPresetListAmFmPresetData[row][1];
    adSetPresetListAmFmPreset = [adSetPresetListAmFmPresetData[row][0] intValue];
}

- (void)setAdSetPresetListSxmSrcData
{
    NSInteger row = [self.mAdSetPresetListSxmSrcPicker selectedRowInComponent:0];
    self.mAdSetPresetListSxmSrcText.text = adSetPresetListSxmSrcData[row][1];
    adSetPresetListSxmSrc = [adSetPresetListSxmSrcData[row][0] intValue];
}
- (void)setAdGetStationListAmFmSrcData
{
    NSInteger row = [self.mAdGetStationListAmFmSrcPicker selectedRowInComponent:0];
    self.mAdGetStationListAmFmSrcText.text = adGetStationListAmFmSrcData[row][1];
    adGetStationListAmFmSrc = [adGetStationListAmFmSrcData[row][0] intValue];
}

/**
 * Text Fieldの編集可不可を返却(Text Field選択時に動作する)
 */
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(nonnull NSString *)string
{
    NSUInteger tag = [textField tag];
    switch (tag) {
        case ADGETPRESET_SRC_TAG:
        case ADSETPRESETAMFM_SRC_TAG:
        case ADSETPRESETAMFM_PRESET_TAG:
//        case ADSETPRESETAMFM_PRESETNO_TAG:
//        case ADSETPRESETAMFM_FREQ_TAG:
        case ADSETPRESETSXM_SRC_TAG:
//        case ADSETPRESETSXM_PRESETNO_TAG:
//        case ADSETPRESETSXM_CHANNEL_TAG:
        case ADSETSTATIONAMFM_SRC_TAG:
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
 * ピッカーの行数を取得する
 */
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    if(pickerView.tag == ADGETPRESET_SRC_TAG){
        return adGetPresetListSrcData.count;
    }
    else if(pickerView.tag == ADSETPRESETAMFM_SRC_TAG){
        return adSetPresetListAmFmSrcData.count;
    }
    else if(pickerView.tag == ADSETPRESETAMFM_PRESET_TAG){
        return adSetPresetListAmFmPresetData.count;
    }
    else if(pickerView.tag == ADSETPRESETSXM_SRC_TAG){
        return adSetPresetListSxmSrcData.count;
    }
    else if(pickerView.tag == ADSETSTATIONAMFM_SRC_TAG){
        return adGetStationListAmFmSrcData.count;
    }
    return 0;
}

/**
 * ピッカーに表示する文字列の取得
 */
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    if(pickerView.tag == ADGETPRESET_SRC_TAG){
        return adGetPresetListSrcData[row][1];
    }
    else if(pickerView.tag == ADSETPRESETAMFM_SRC_TAG){
        return adSetPresetListAmFmSrcData[row][1];
    }
    else if(pickerView.tag == ADSETPRESETAMFM_PRESET_TAG){
        return adSetPresetListAmFmPresetData[row][1];
    }
    else if(pickerView.tag == ADSETPRESETSXM_SRC_TAG){
        return adSetPresetListSxmSrcData[row][1];
    }
    else if(pickerView.tag == ADSETSTATIONAMFM_SRC_TAG){
        return adGetStationListAmFmSrcData[row][1];
    }
    return nil;
}

/**
 * ピッカーを回した時に更新された値をText Fieldに設定する
 */
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    NSUInteger tag = [pickerView tag];
    if(tag == ADGETPRESET_SRC_TAG)
    {
        [self setAdGetPresetListSrcData];
    }
    else if(tag == ADSETPRESETAMFM_SRC_TAG)
    {
        [self setAdSetPresetListAmFmSrcData];
    }
    else if(tag == ADSETPRESETAMFM_PRESET_TAG)
    {
        [self setAdSetPresetListAmFmPresetData];
    }
    else if(tag == ADSETPRESETSXM_SRC_TAG)
    {
        [self setAdSetPresetListSxmSrcData];
    }
    else if(tag == ADSETSTATIONAMFM_SRC_TAG)
    {
        [self setAdGetStationListAmFmSrcData];
    }
}

/* responseデータ表示テーブルクリア */
- (void)clearMdResTable {
    self.mCodeLabel.text =  @"";
    self.mCommandLabel.text =  @"";
    self.mCountLabel.text =  @"";
    self.mAudioSrcLabel.text = @"";
    [tagDic removeAllObjects];
    [resDataDic removeAllObjects];
    [presetPageDic removeAllObjects];
    [presetNoDic removeAllObjects];
    [presetNoSxmDic removeAllObjects];
    [stationFreqDic removeAllObjects];
    [self.mMdResTableView reloadData];
}

/* responseログ出力 */
- (void)writeResultLog:(NSString *)reqApiName :(NSString *)resultStr {
    NSDateFormatter* now_date = [[NSDateFormatter alloc] init];
    [now_date setDateFormat:@"[HH:mm:ss.SSS]"];
    NSString *date= [now_date stringFromDate:[NSDate date]];
    if(resultStr != nil)
    {
        NSString *rsltLogInfo = [NSString stringWithFormat:@"MdReq[%@]%@", reqApiName, resultStr];
        [loginfoMdRes write:rsltLogInfo];
        NSString *rsltAll = [NSString stringWithFormat: @"%@[%@]%@", date, reqApiName, resultStr];
        _mMdReqRtnTextView.text = rsltAll;
    }
    else{
        [loginfoMdRes write:@"MdReq[reqAirconVentTempSetting]Result=Empty"];
        NSString *rsltAll = [NSString stringWithFormat: @"%@[%@]Result=Empty", date, reqApiName];
        _mMdReqRtnTextView.text = rsltAll;
    }
}

/* Button音作動 */
- (void)invokeSystemSound {
    /* バイブレーション */
//    AudioServicesPlaySystemSound(SYSTEM_SOUNDID_VIBRATE_SHORT);
    /* タップ音 */
    AudioServicesPlaySystemSound(SYSTEM_SOUNDID_SEND);

}

- (IBAction)mAdGetPresetListButton:(id)sender {
    NSLog(@"reqAudioGetPresetList:src=%ld", (long)adGetPresetListSrc);
    NSString *rslt = [libEAPSampleEvtRes reqAudioGetPresetList:ACCESSKEY source:adGetPresetListSrc];
    [self writeResultLog: @"reqAudioGetPresetList" :rslt];
    [self clearMdResTable];
    [self invokeSystemSound];
#ifdef __DEBUG_ON__
    switch ( adGetPresetListSrc ) {
        case AUDIO_SRC_KIND_AM:
            [self didHandleResData:dbgStrPresetAm];
            break;
        case AUDIO_SRC_KIND_FM:
            [self didHandleResData:dbgStrPresetFm];
            break;
        case AUDIO_SRC_KIND_SXM:
            [self didHandleResData:dbgStrPresetSxm];
            break;
        default:
            break;
    }
#endif
}

- (IBAction)mAdSetPresetListAmFmButton:(id)sender {
    NSInteger adSetPresetListAmFmPesetNo = [self.mAdSetPresetListAmFmPresetNoText.text intValue];
    NSInteger adSetPresetListAmFmFreq = [self.mAdSetPresetListAmFmFreqText.text intValue];
    NSLog(@"reqAudioSetPresetList:src=%ld,page=%ld,preset=%ld,freq=%ld"
          ,(long)adSetPresetListAmFmSrc
          ,(long)adSetPresetListAmFmPreset
          ,(long)adSetPresetListAmFmPesetNo
          ,(long)adSetPresetListAmFmFreq);
    NSString *rslt = [libEAPSampleEvtRes reqAudioSetPresetList:ACCESSKEY source:adSetPresetListAmFmSrc preset:adSetPresetListAmFmPreset presetno:adSetPresetListAmFmPesetNo freq:adSetPresetListAmFmFreq];
    [self writeResultLog: @"reqAudioSetPresetList" :rslt];
    [self clearMdResTable];
    [self invokeSystemSound];
#ifdef __DEBUG_ON__
    switch ( adSetPresetListAmFmSrc ) {
        case AUDIO_SRC_KIND_AM:
            [self didHandleResData:dbgStrPresetAm];
            break;
        case AUDIO_SRC_KIND_FM:
            [self didHandleResData:dbgStrPresetFm];
            break;
        case AUDIO_SRC_KIND_SXM:
            [self didHandleResData:dbgStrPresetSxm];
            break;
        default:
            break;
    }
#endif
}

- (IBAction)mAdSetPresetListSxmButton:(id)sender {
    NSInteger adSetPresetListSxmPresetNo = [self.mAdSetPresetListSxmPresetNoText.text intValue];
    NSInteger adSetPresetListSxmChannel = [self.mAdSetPresetListSxmChannelText.text intValue];
    NSLog(@"reqAudioSetPresetListSXM:src=%ld,preset=%ld,ch=%ld"
          ,(long)adSetPresetListSxmSrc
          ,(long)adSetPresetListSxmPresetNo
          ,(long)adSetPresetListSxmChannel);
    NSString *rslt = [libEAPSampleEvtRes reqAudioSetPresetListSXM:ACCESSKEY source:adSetPresetListSxmSrc presetno:adSetPresetListSxmPresetNo channelno:adSetPresetListSxmChannel];
    [self writeResultLog: @"reqAudioSetPresetListSXM" :rslt];
    [self clearMdResTable];
    [self invokeSystemSound];
#ifdef __DEBUG_ON__
    [self didHandleResData:dbgStrPresetSxm];
#endif
}

- (IBAction)mAdGetStationListAmFmSrcButton:(id)sender {
    NSLog(@"reqAudioGetStationList:src=%ld", (long)adGetStationListAmFmSrc);
    NSString *rslt = [libEAPSampleEvtRes reqAudioGetStationList:ACCESSKEY source:adGetStationListAmFmSrc];
    [self writeResultLog: @"reqAudioGetStationList" :rslt];
    [self clearMdResTable];
    [self invokeSystemSound];
#ifdef __DEBUG_ON__
    switch ( adGetStationListAmFmSrc ) {
        case AUDIO_SRC_KIND_AM:
            [self didHandleResData:dbgStrStationAm];
            break;
        case AUDIO_SRC_KIND_FM:
            [self didHandleResData:dbgStrStationFm];
            break;
        default:
            break;
    }
#endif
}

@end
