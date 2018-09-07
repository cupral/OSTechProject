//
//  TestFunc.m
//  EAPSampleApp
//
//  Created by user on 2018/02/05.
//  Copyright © 2018年 HiICS. All rights reserved.
//

#ifdef __STAND_ALONE__

#include <sys/types.h>
#include <sys/sysctl.h>

#import <AudioToolbox/AudioToolbox.h>
#import "TestFunc.h"
#import "EAPLog.h"

#define DELAY_MAX (2147483647)
#define PERIOD_MAX (65535)
#define PERIOD_MIN (-1)

#define TEXT_DEFAULT_DELAY  (@"0")
#define TEXT_DEFAULT_BCONST (@"500")
#define TEXT_DEFAULT_BHIGH  (@"10")
#define TEXT_DEFAULT_BMID   (@"30")
#define TEXT_DEFAULT_BLOW   (@"500")
#define TEXT_DEFAULT_FHIGH  (@"10")
#define TEXT_DEFAULT_IMID   (@"30")
#define TEXT_DEFAULT_IAUDIO (@"50")

@interface TestFunc () <ViewControllerTestDelegate>
@end

@implementation TestFunc {
    NSArray *mdTestModeList;
    NSInteger mdTestMode;
    NSFileHandle *mLogFileHandle;
}

static libMDEAPframework *libEAPSampleTestFunc;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSLog(@"viewDidLoad of TestFunc");
    
    libEAPSampleTestFunc = [libMDEAPframework sharedController];
    
    if( viewController != nil ){
        viewController.delegateTest = self;
    }
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    if( paths != nil ) {
        NSString *documentDirectory = [paths objectAtIndex:0];
        NSString *logFileName = [NSString stringWithFormat:@"iPhone_%@_testlog.log", (NSString *)[self getTimestamp:@"yyyyMMdd_HHmmss"]];
        if( documentDirectory != nil ) {
            NSString *logFilePath = [documentDirectory stringByAppendingPathComponent:logFileName];
            NSFileManager *logFileManager = [NSFileManager defaultManager];
            BOOL result = [logFileManager fileExistsAtPath:logFilePath];
            if( result == false ) {
                result = [self createFile:logFilePath];
                if( result == true ) {
                    mLogFileHandle = [NSFileHandle fileHandleForWritingAtPath:logFilePath];
                }
            }
        }
    }
    
    mdTestMode = MODE_OFF;
    mdTestModeList = @[@[@(MODE_OFF),@"(1)Release MD Test Mode"],
                       @[@(MODE_STARTRES_DELAY),@"(2)EC-STARTRES(delay)"],
                       @[@(MODE_STARTRES_DROP),@"(3)EC-STARTRES(drop fragmented packet)"],
                       @[@(MODE_STARTRES_INV_PKT_TYPE),@"(4)EC-STARTRES(invalid packet type)"],
                       @[@(MODE_STARTRES_INV_LEN),@"(5)EC-STARTRES(content length 0)"],
                       @[@(MODE_STARTRES_INV_FRG_CNT),@"(6)EC-STARTRES(invalid packet fragment count)"],
                       @[@(MODE_STARTRES_INV_FRG_POS),@"(7)EC-STARTRES(invalid packet fragment position)"],
                       @[@(MODE_STARTRES_INV_CHKSUM),@"(8)EC-STARTRES(invalid checksum)"],
                       @[@(MODE_STARTRES_INV_CERT),@"(9)EC-STARTRES(invalid certificate)"],
                       @[@(MODE_FINISHED_DELAY),@"(10)EC-FINISHED(delay)"],
                       @[@(MODE_FINISHED_INV_PKT_TYPE),@"(11)EC-FINISHED(invalid packet type)"],
                       @[@(MODE_FINISHED_INV_CHKSUM),@"(12)EC-FINISHED(invalid checksum)"]];
    
    self.mLogTextView.editable = NO;
    self.mLogTextView.text = @"";
    
    // Keyboard toolbar
    self.keyboardToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0,0,self.view.bounds.size.width, 38.0f)];
    self.keyboardToolbar.barStyle = UIBarStyleBlackTranslucent;
    UIBarButtonItem *spaceBarItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    UIBarButtonItem *doneBarItem = [[UIBarButtonItem alloc] initWithTitle:@"Enter" style:UIBarButtonItemStyleDone target:self action:@selector(resignKeyboard:)];
    [self.keyboardToolbar setItems:@[spaceBarItem, doneBarItem]];
    
    // MD Test Mode Picker View
    self.mMdTestModePicker = [[UIPickerView alloc] init];
    self.mMdTestModePicker.delegate = self;
    self.mMdTestModePicker.tag = MDTESTMODE_MODE_TAG;
    self.mMdTestModePicker.showsSelectionIndicator = YES;
    // MD Test Mode TextField
    self.mMdTestModeText.delegate = self;
    self.mMdTestModeText.tag = MDTESTMODE_MODE_TAG;
    self.mMdTestModeText.inputAccessoryView = self.keyboardToolbar;
    self.mMdTestModeText.inputView = self.mMdTestModePicker;
    [self setMdTestModeData];
    // MD Test Mode Delay Time TextField
    self.mDelayText.delegate = self;
    self.mDelayText.tag = MDTESTMODE_DELAY_TAG;
    self.mDelayText.inputAccessoryView = self.keyboardToolbar;
    self.mDelayText.keyboardType = UIKeyboardTypeNumberPad;
    self.mDelayText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mDelayText.enabled = NO;
    self.mDelayText.backgroundColor = [UIColor grayColor];
    self.mDelayText.text = TEXT_DEFAULT_DELAY;
    // B-CONST Period TextField
    self.mBconstText.delegate = self;
    self.mBconstText.tag = MDTESTMODE_BCONST_TAG;
    self.mBconstText.inputAccessoryView = self.keyboardToolbar;
    self.mBconstText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mBconstText.text = TEXT_DEFAULT_BCONST;
    // B-HIGH Period TextField
    self.mBhighText.delegate = self;
    self.mBhighText.tag = MDTESTMODE_BHIGH_TAG;
    self.mBhighText.inputAccessoryView = self.keyboardToolbar;
    self.mBhighText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mBhighText.text = TEXT_DEFAULT_BHIGH;
    // B-MID Period TextField
    self.mBmidText.delegate = self;
    self.mBmidText.tag = MDTESTMODE_BMID_TAG;
    self.mBmidText.inputAccessoryView = self.keyboardToolbar;
    self.mBmidText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mBmidText.text = TEXT_DEFAULT_BMID;
    // B-LOW Period TextField
    self.mBlowText.delegate = self;
    self.mBlowText.tag = MDTESTMODE_BLOW_TAG;
    self.mBlowText.inputAccessoryView = self.keyboardToolbar;
    self.mBlowText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mBlowText.text = TEXT_DEFAULT_BLOW;
    // F-HIGH Period TextField
    self.mFhighText.delegate = self;
    self.mFhighText.tag = MDTESTMODE_FHIGH_TAG;
    self.mFhighText.inputAccessoryView = self.keyboardToolbar;
    self.mFhighText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mFhighText.text = TEXT_DEFAULT_FHIGH;
    // I-MID Period TextField
    self.mImidText.delegate = self;
    self.mImidText.tag = MDTESTMODE_IMID_TAG;
    self.mImidText.inputAccessoryView = self.keyboardToolbar;
    self.mImidText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mImidText.text = TEXT_DEFAULT_IMID;
    // I-AUDIO Period TextField
    self.mIaudioText.delegate = self;
    self.mIaudioText.tag = MDTESTMODE_IAUDIO_TAG;
    self.mIaudioText.inputAccessoryView = self.keyboardToolbar;
    self.mIaudioText.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.mIaudioText.text = TEXT_DEFAULT_IAUDIO;
    
    /* システム情報ログ出力 */
    [self outputSystemInfo];
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
    if([self.mMdTestModeText isFirstResponder])
    {
        return self.mMdTestModeText;
    }
    else if([self.mDelayText isFirstResponder])
    {
        [self complementZero:self.mDelayText];
        return self.mDelayText;
    }
    else if([self.mBconstText isFirstResponder])
    {
        [self clearSignChr:self.mBconstText];
        [self complementZero:self.mBconstText];
        return self.mBconstText;
    }
    else if([self.mBhighText isFirstResponder])
    {
        [self clearSignChr:self.mBhighText];
        [self complementZero:self.mBhighText];
        return self.mBhighText;
    }
    else if([self.mBmidText isFirstResponder])
    {
        [self clearSignChr:self.mBmidText];
        [self complementZero:self.mBmidText];
        return self.mBmidText;
    }
    else if([self.mBlowText isFirstResponder])
    {
        [self clearSignChr:self.mBlowText];
        [self complementZero:self.mBlowText];
        return self.mBlowText;
    }
    else if([self.mFhighText isFirstResponder])
    {
        [self clearSignChr:self.mFhighText];
        [self complementZero:self.mFhighText];
        return self.mFhighText;
    }
    else if([self.mImidText isFirstResponder])
    {
        [self clearSignChr:self.mImidText];
        [self complementZero:self.mImidText];
        return self.mImidText;
    }
    else if([self.mIaudioText isFirstResponder])
    {
        [self clearSignChr:self.mIaudioText];
        [self complementZero:self.mIaudioText];
        return self.mIaudioText;
    }
    return nil;
}

/*
 * キーボード消去時に、テキストが"-"ならクリア
 */
- (void)clearSignChr:(ExtraUITextField *)textField
{
    if( [textField.text isEqualToString:@"-"] ) {
        textField.text = @"";
    }
}

/*
 * キーボード消去時に、テキストがから文字列なら0設定
 */
- (void)complementZero:(ExtraUITextField *)textField
{
    if( [textField.text isEqualToString:@""] ) {
        textField.text = @"0";
    }
}

/*
 * TextFieldの編集終了直前に値チェック
 */
- (BOOL)textFieldShouldEndEditing:(UITextField *)textField
{
    [self clearSignChr:(ExtraUITextField *)textField];
    [self complementZero:(ExtraUITextField *)textField];
    return YES;
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
    if(tag == MDTESTMODE_MODE_TAG && [self.mMdTestModeText.text isEqualToString:@""])
    {
        [self setMdTestModeData];
    }
}

- (void)setMdTestModeData
{
    NSInteger row = [self.mMdTestModePicker selectedRowInComponent:0];
    self.mMdTestModeText.text = mdTestModeList[row][1];
    mdTestMode = [mdTestModeList[row][0] intValue];
}


/**
 * 1文字目の許容文字かチェックする
 */
- (BOOL)isValidFirstCharSet:(NSString *)string
{
    NSCharacterSet *tgtCset = [NSCharacterSet characterSetWithCharactersInString:string];
    NSCharacterSet *allowCset = [NSCharacterSet characterSetWithCharactersInString:@"0123456789-"];
    return [allowCset isSupersetOfSet:tgtCset];
}

/**
 * 2文字目以降の許容文字かチェックする
 */
- (BOOL)isValidSecondOrAfterCharSet:(NSString *)string
{
    NSCharacterSet *tgtCset = [NSCharacterSet characterSetWithCharactersInString:string];
    NSCharacterSet *allowCset = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];
    return [allowCset isSupersetOfSet:tgtCset];
}

/**
 * Text Fieldの編集可不可を返却(Text Field選択時に動作する)
 */
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(nonnull NSString *)string
{
    /* 更新後の文字列 */
    NSString *updatedStr = [textField.text stringByReplacingCharactersInRange:range withString:string];
    /* 更新後の数値 */
    NSInteger updatedValue = [updatedStr intValue];
    /* 更新後の文字列長 */
    NSInteger updatedLength = updatedStr.length;
    /* 更新後の文字列の1文字目 */
    NSString *firstChar = nil;
    /* 更新後の文字列の2文字目以降 */
    NSString *remainStr = nil;
    if( updatedLength == 1 ) {
        firstChar = [updatedStr substringToIndex:1];
    }
    else if ( updatedLength > 1 ) {
        firstChar = [updatedStr substringToIndex:1];
        remainStr = [updatedStr substringFromIndex:1];
    }
    NSUInteger tag = [textField tag];
    switch (tag) {
        case MDTESTMODE_MODE_TAG:
            return NO; // ユーザ入力の無効化
        case MDTESTMODE_DELAY_TAG:
            if( [textField.text isEqualToString:@""] ) {
                return YES;
            }
            else if( [[textField.text stringByReplacingCharactersInRange:range withString:string] longLongValue] <= DELAY_MAX) {
                return YES;
            }
            else{
                return NO;
            }
            break;
        case MDTESTMODE_BCONST_TAG:
        case MDTESTMODE_BHIGH_TAG:
        case MDTESTMODE_BMID_TAG:
        case MDTESTMODE_BLOW_TAG:
        case MDTESTMODE_FHIGH_TAG:
        case MDTESTMODE_IMID_TAG:
        case MDTESTMODE_IAUDIO_TAG:
            if( updatedLength == 0 ) { /* 編集後に空文字列 */
                /* backspaceやカットなどで空文字列になるケース */
                return YES; /* 編集許可 */
            }
            else if ( updatedLength == 1 ) { /* 編集後に1文字 */
                if( [self isValidFirstCharSet:firstChar] ) {
                    /* 許容文字（-含む数値）なら許可 */
                    return YES; /* 編集許可 */
                }
                else {
                    return NO; /* 編集非許可 */
                }
            }
            else{ /* 編集後に2文字以上 */
                if( (updatedValue <= PERIOD_MAX) &&
                   (updatedValue >= PERIOD_MIN) &&
                   [self isValidFirstCharSet:firstChar] &&
                   [self isValidSecondOrAfterCharSet:remainStr] ) {
                    /* 2文字以上の場合以下の条件を全て満たした場合許可 */
                    /* 最小値以上、最大値以下 */
                    /* 1文字目が許容文字(-含む数値) */
                    /* 2文字目以降が許容文字(数値のみ)で構成されている */
                    return YES; /* 編集許可 */
                }
                else{
                    return NO; /* 編集非許可 */
                }
            }
            break;
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
    if(pickerView.tag == MDTESTMODE_MODE_TAG){
        return mdTestModeList.count;
    }
    return 0;
}

/**
 * ピッカーに表示する文字列の取得
 */
- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    if(pickerView.tag == MDTESTMODE_MODE_TAG){
        return mdTestModeList[row][1];
    }
    return nil;
}

/**
 * ピッカーを回した時に更新された値をText Fieldに設定する
 */
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    NSUInteger tag = [pickerView tag];
    if(tag == MDTESTMODE_MODE_TAG)
    {
        [self setMdTestModeData];
    }
    switch (mdTestMode) {
        case MODE_STARTRES_DELAY:
        case MODE_FINISHED_DELAY:
            self.mDelayText.enabled = YES;
            self.mDelayText.backgroundColor = [UIColor whiteColor];
            break;
        default:
            self.mDelayText.enabled = NO;
            self.mDelayText.backgroundColor = [UIColor grayColor];
            break;
    }
}

/* Button音作動 */
- (void)invokeSystemSound {
    /* バイブレーション */
    //    AudioServicesPlaySystemSound(SYSTEM_SOUNDID_VIBRATE_SHORT);
    /* タップ音 */
    AudioServicesPlaySystemSound(SYSTEM_SOUNDID_SEND);
}

/**
 * Clearボタン押下
 */
- (IBAction)mLogClearButton:(id)sender {
    self.mLogTextView.text = @"";
    [self outputSystemInfo];
    [self invokeSystemSound];
}

/**
 * VDログ出力有無切り替えボタン押下
 */
- (IBAction)mSwitchLogButton:(id)sender {
    NSInteger rslt = [libEAPSampleTestFunc reqSwitchVdLogOutput:2];
    if( rslt == 0) {
        [self.mSwitchLogButton setTitle:@"VD LOG ON" forState:UIControlStateNormal];
    }
    else {
        [self.mSwitchLogButton setTitle:@"VD LOG OFF" forState:UIControlStateNormal];
    }
    [self invokeSystemSound];
}

/**
 * MDテストモード設定ボタン押下
 */
- (IBAction)mMdTestModeSetButton:(id)sender {
    NSInteger rslt = [libEAPSampleTestFunc reqSetMdTestMode:mdTestMode replyDelay:[self.mDelayText.text intValue]];
    NSLog(@"reqSetMdTestMode:mode=%ld, delay=%ld;reslt=%ld", (long)mdTestMode, (long)[self.mDelayText.text intValue], (long)rslt);
    [self invokeSystemSound];
}

/**
 * 周期デフォルト設定ボタン押下
 */
- (IBAction)mDefaultPeriodButton:(id)sender {
    self.mBconstText.text = TEXT_DEFAULT_BCONST;
    self.mBhighText.text = TEXT_DEFAULT_BHIGH;
    self.mBmidText.text = TEXT_DEFAULT_BMID;
    self.mBlowText.text = TEXT_DEFAULT_BLOW;
    self.mFhighText.text = TEXT_DEFAULT_FHIGH;
    self.mImidText.text = TEXT_DEFAULT_IMID;
    self.mIaudioText.text = TEXT_DEFAULT_IAUDIO;
}

/**
 * 周期設定ボタン押下
 */
- (IBAction)mPeriodSetButton:(id)sender {
    NSArray *period = @[
                        @([self.mBconstText.text intValue]),
                        @([self.mBhighText.text intValue]),
                        @([self.mBmidText.text intValue]),
                        @([self.mBlowText.text intValue]),
                        @([self.mFhighText.text intValue]),
                        @([self.mImidText.text intValue]),
                        @([self.mIaudioText.text intValue])
                        ];
    NSInteger rslt = [libEAPSampleTestFunc reqSetVeSendPeriod:period];
    NSLog(@"reqSetVeSendPeriod:bconst=%@, bhigh=%@, bmid=%@, blow=%@, fhigh=%@, imid=%@, iaudio=%@;reslt=%ld", period[0], period[1], period[2], period[3], period[4], period[5], period[6], (long)rslt);
    [self invokeSystemSound];
}

/**
 * ログ出力通知受信
 */
- (void)didHandleLogData: (NSString *)logdata
{
    NSString *logDataRtn = [NSString stringWithFormat:@"%@\n", logdata];
    [self.mLogTextView setText:[self.mLogTextView.text stringByAppendingString:logDataRtn]];
    [self writeToLogFile:logDataRtn];
}

- (void)writeToLogFile: (NSString *)logStr
{
    @synchronized (self) {
        [mLogFileHandle truncateFileAtOffset:[mLogFileHandle seekToEndOfFile]];
        NSData *data = [logStr dataUsingEncoding:NSUTF8StringEncoding];
        [mLogFileHandle writeData:data];
        [mLogFileHandle synchronizeFile];
    }
}

/**-----------------------------------------------------------------------------------
 * getTimestamp
 * 時間情報の出力形式設定
 *
 * @param  : format     : 出力フォーマット
 * @result : NSString   : 出力値
 -----------------------------------------------------------------------------------*/
- (NSString *)getTimestamp:(NSString *)format
{
    NSDateFormatter* now_date = [[NSDateFormatter alloc] init];
    [now_date setDateFormat:format];
    NSString *string = [now_date stringFromDate:[NSDate date]];
    return string;
}

/**
 * システム情報をログ出力する
 */
- (void)outputSystemInfo
{
    size_t size;
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    NSString *platform = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];
    free(machine);
//    NSString *model = [NSString stringWithFormat:@"model=%@", [UIDevice currentDevice].model];
    NSString *systemVersion = [NSString stringWithFormat:@"OSVer=%@", [UIDevice currentDevice].systemVersion];
    NSString *systemInfo = [NSString stringWithFormat:@"%@ [MD->MD]MD-INFO[Model=%@,%@]\n", (NSString *)[self getTimestamp:@"YYYY/MM/dd HH:mm:ss.SSS"], platform, systemVersion];
    [self.mLogTextView setText:[self.mLogTextView.text stringByAppendingString:systemInfo]];
    [self writeToLogFile:systemInfo];
}

/**-----------------------------------------------------------------------------------
 * createFile
 -----------------------------------------------------------------------------------*/
- (BOOL)createFile:(NSString *)filePath
{
    return [[NSFileManager defaultManager] createFileAtPath:filePath contents:[NSData data] attributes:nil];
}

@end

#endif /* __STAND_ALONE__ */
