//
//  ViewControllerEventRes.h
//  EAPSampleApp
//
//

#ifndef ViewControllerEventRes_h
#define ViewControllerEventRes_h

#import <UIKit/UIKit.h>
#import <libMDEAPframework/libMDEAPframework.h>
#import "ViewController.h"

#define CMDID_RES_GET_PRESET_LIST       (3008)
#define CMDID_RES_SET_PRESET_LIST       (3010)
#define CMDID_RES_GET_STATION_LIST      (3016)

#define AUDIO_SRC_KIND_AM   (0)
#define AUDIO_SRC_KIND_FM   (1)
#define AUDIO_SRC_KIND_SXM  (2)

#define TAG_LEN_THREE   (3)

typedef enum : NSInteger{
    ADGETPRESET_SRC_TAG = 1,
    ADSETPRESETAMFM_SRC_TAG,
    ADSETPRESETAMFM_PRESET_TAG,
    ADSETPRESETAMFM_PRESETNO_TAG,
    ADSETPRESETAMFM_FREQ_TAG,
    ADSETPRESETSXM_SRC_TAG,
    ADSETPRESETSXM_PRESETNO_TAG,
    ADSETPRESETSXM_CHANNEL_TAG,
    ADSETSTATIONAMFM_SRC_TAG,
    SAMPLE_APP_MDRES_TAG_MAX
} sampleAppMdResTag;

extern ViewController* viewController;

@interface ViewControllerEventRes : UIViewController<UITableViewDelegate,
UITableViewDataSource,
UITextFieldDelegate,
UITextViewDelegate,
UIPickerViewDelegate,
UIPickerViewDataSource>

@property (weak, nonatomic) IBOutlet UITableView *mMdResTableView;
@property (weak, nonatomic) IBOutlet UILabel *mCodeLabel;
@property (weak, nonatomic) IBOutlet UILabel *mCommandLabel;
@property (weak, nonatomic) IBOutlet UILabel *mCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *mAudioSrcLabel;
@property (weak, nonatomic) IBOutlet UITextView *mMdReqRtnTextView;
@property (nonatomic, retain) UIToolbar *keyboardToolbar;

// [21]AudioGetPresetlist
@property (nonatomic, retain) IBOutlet UIPickerView *mAdGetPresetListSrcPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAdGetPresetListSrcText;
@property (weak, nonatomic) IBOutlet UIButton *mAdGetPresetListButton;

// [24]AudioSetPresetList(AM/FM)
@property (nonatomic, retain) IBOutlet UIPickerView *mAdSetPresetListAmFmSrcPicker;
@property (nonatomic, retain) IBOutlet UIPickerView *mAdSetPresetListAmFmPresetPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListAmFmSrcText;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListAmFmPresetText;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListAmFmPresetNoText;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListAmFmFreqText;
@property (weak, nonatomic) IBOutlet UIButton *mAdSetPresetListAmFmButton;

// [25]AudioSetPresetList(SXM)
@property (nonatomic, retain) IBOutlet UIPickerView *mAdSetPresetListSxmSrcPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListSxmSrcText;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListSxmPresetNoText;
@property (weak, nonatomic) IBOutlet UITextField *mAdSetPresetListSxmChannelText;
@property (weak, nonatomic) IBOutlet UIButton *mAdSetPresetListSxmButton;

// [31]AudioGetStationList(AM/FM)
@property (nonatomic, retain) IBOutlet UIPickerView *mAdGetStationListAmFmSrcPicker;
@property (weak, nonatomic) IBOutlet UITextField *mAdGetStationListAmFmSrcText;
@property (weak, nonatomic) IBOutlet UIButton *mAdGetStationListAmFmSrcButton;

@end

#endif /* ViewControllerEventRes_h */
