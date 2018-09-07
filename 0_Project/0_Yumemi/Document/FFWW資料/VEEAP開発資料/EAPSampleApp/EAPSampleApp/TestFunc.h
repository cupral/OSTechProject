//
//  TestFunc.h
//  EAPSampleApp
//
//  Created by user on 2018/02/05.
//  Copyright © 2018年 HiICS. All rights reserved.
//

#ifdef __STAND_ALONE__

#ifndef TestFunc_h
#define TestFunc_h

#import <UIKit/UIKit.h>
#import <libMDEAPframework/libMDEAPframework.h>
#import "ViewController.h"
#import "ExtraUITextField.h"

typedef enum : NSInteger{
    MDTESTMODE_MODE_TAG = 1,
    MDTESTMODE_DELAY_TAG,
    MDTESTMODE_BCONST_TAG,
    MDTESTMODE_BHIGH_TAG,
    MDTESTMODE_BMID_TAG,
    MDTESTMODE_BLOW_TAG,
    MDTESTMODE_FHIGH_TAG,
    MDTESTMODE_IMID_TAG,
    MDTESTMODE_IAUDIO_TAG,
    SAMPLE_APP_TESTFUNC_TAG_MAX
} sampleAppTestFuncTag;

typedef enum : NSInteger{
    MODE_OFF = 0,
    MODE_STARTRES_DELAY,
    MODE_STARTRES_DROP,
    MODE_STARTRES_INV_PKT_TYPE,
    MODE_STARTRES_INV_LEN,
    MODE_STARTRES_INV_FRG_CNT,
    MODE_STARTRES_INV_FRG_POS,
    MODE_STARTRES_INV_CHKSUM,
    MODE_STARTRES_INV_CERT,
    MODE_FINISHED_DELAY,
    MODE_FINISHED_INV_PKT_TYPE,
    MODE_FINISHED_INV_CHKSUM,
    MODE_MAX
} sampleAppMdTestModeT;

extern ViewController *viewController;

@interface TestFunc : UIViewController<UITextFieldDelegate,
UITextViewDelegate,
UIPickerViewDelegate,
UIPickerViewDataSource>

@property (weak, nonatomic) IBOutlet UITextView *mLogTextView;
@property (weak, nonatomic) IBOutlet UIButton *mLogClearButton;
@property (weak, nonatomic) IBOutlet UIButton *mSwitchLogButton;

@property (nonatomic, retain) UIToolbar *keyboardToolbar;

@property (nonatomic, retain) IBOutlet UIPickerView *mMdTestModePicker;
@property (weak, nonatomic) IBOutlet UITextField *mMdTestModeText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mDelayText;
@property (weak, nonatomic) IBOutlet UIButton *mMdTestModeSetButton;

@property (weak, nonatomic) IBOutlet ExtraUITextField *mBconstText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mBhighText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mBmidText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mBlowText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mFhighText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mImidText;
@property (weak, nonatomic) IBOutlet ExtraUITextField *mIaudioText;
@property (weak, nonatomic) IBOutlet UIButton *mDefaultPeriodButton;
@property (weak, nonatomic) IBOutlet UIButton *mPeriodSetButton;

@end

#endif /* TestFunc_h */

#endif /* __STAND_ALONE__ */
