//
//  ViewController.m
//  WebViewJavascriptBridgeSample
//
//  Created by ssato on 2016/01/29.
//  Copyright © 2016年 ssato. All rights reserved.
//

#import "ViewController.h"

#import "WebViewJavascriptBridge.h"

/**
 *  pepperのIPアドレス 適宜修正
 */
static NSString *const PepperIPAddress = @"169.254.232.52";

@interface ViewController ()<UIWebViewDelegate>

@property (weak, nonatomic)IBOutlet UIWebView *webView;
@property (strong, nonatomic)WebViewJavascriptBridge* bridge;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    [self loadExamplePage:self.webView];

    [WebViewJavascriptBridge enableLogging];

    [self createBridge];

    [self registerPeppperEventCallBack];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Button Action

/**
 *  webViewをリロード
 *
 *  @param sender
 */
- (IBAction)reloadWebView:(UIButton *)sender
{
    [self.webView reload];
}

/**
 *  pepperと接続開始コマンドを送る
 *
 *  @param sender
 */
- (IBAction)connect:(UIButton *)sender
{
    [self.bridge callHandler:@"startConnect" data:PepperIPAddress responseCallback:^(id response) {
        NSLog(@"接続するためのjsを叩いたコールバック: %@", response);
    }];
}

/**
 *  挨拶コマンドを送る
 *
 *  @param sender
 */
- (IBAction)hello:(UIButton *)sender
{
    [self.bridge callHandler:@"hello" data:@"こんにちは、ペッパーです。" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
// restart
- (IBAction)restart:(id)sender {
    [self.bridge callHandler:@"restart" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
// appStart
- (IBAction)appStart:(UIButton *)sender {
    [self.bridge callHandler:@"appStart" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//outputIP
- (IBAction)outputIP:(id)sender {
    [self.bridge callHandler:@"outputIP" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//qrDescription
- (IBAction)qrDescription:(id)sender {
    [self.bridge callHandler:@"qrDescription" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//identification

- (IBAction)identification:(id)sender {
  // ユーザデータを配列で送信
  // [ユーザID,ユーザ名,誕生年,誕生月,誕生日]
  NSArray *userData = [NSArray arrayWithObjects:@"00000001", @"東京 太郎", @"1995", @"05", @"31", nil];
    [self.bridge callHandler:@"identification" data:userData responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}

// guidance
- (IBAction)guidance:(id)sender {
    [self.bridge callHandler:@"guidance" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}

//personalInformation
- (IBAction)personalInformation:(id)sender {
    [self.bridge callHandler:@"personalInformation" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//attitude
- (IBAction)attitude:(id)sender {
    [self.bridge callHandler:@"attitude" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//notes
- (IBAction)notes:(id)sender {
    [self.bridge callHandler:@"notes" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//progress1
- (IBAction)progress1:(id)sender {
    [self.bridge callHandler:@"progress1" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//progress2
- (IBAction)progress2:(id)sender {
    [self.bridge callHandler:@"progress2" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}

//progress3
- (IBAction)progress3:(id)sender {
    [self.bridge callHandler:@"progress3" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}

//progress4
- (IBAction)progress4:(id)sender {
    [self.bridge callHandler:@"progress4" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//interviewStart
- (IBAction)interviewStart:(id)sender {
    [self.bridge callHandler:@"interviewStart" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//question
  // 質問する文章を配列で送信
  // [タブレットに出力する文章,Pepperが読み上げる文章]
- (IBAction)question:(id)sender {
  NSArray *questionTxt = [NSArray arrayWithObjects:@"あなたが大学生の頃、一番頑張ったと思う活動などを教えてください。", @"\\rspd=95\\\\vct=115\\あなたが大学生の頃、一番頑張ったと思う活動などを教えてください。",@7, nil];
    [self.bridge callHandler:@"question" data:questionTxt responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//hearing
- (IBAction)hearing:(id)sender {
    [self.bridge callHandler:@"hearing" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//suspended
- (IBAction)suspended:(id)sender {
    [self.bridge callHandler:@"suspended" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//suspendEnd
- (IBAction)suspendEnd:(id)sender {
    [self.bridge callHandler:@"suspendEnd" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}


//upload
  // data = データ容量数(MB);
- (IBAction)upload:(id)sender {
    NSString *megaByte = @"254";
    [self.bridge callHandler:@"upload" data:megaByte responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
// rate
  // data = 進捗率
- (IBAction)rate:(id)sender {
    NSString *rateNum = @"35";
    [self.bridge callHandler:@"rate" data:rateNum responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}


//interviewFinish
- (IBAction)interviewFinish:(id)sender {
    [self.bridge callHandler:@"interviewFinish" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//appFinish
- (IBAction)appFinish:(id)sender {
    [self.bridge callHandler:@"appFinish" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
//outputIP_close
- (IBAction)outputIP_close:(id)sender {
    [self.bridge callHandler:@"outputIP_close" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}
// appError
- (IBAction)appError:(id)sender {
    [self.bridge callHandler:@"appError" data:@"" responseCallback:^(id response) {
        NSLog(@"挨拶するためのjsを叩いたコールバック: %@", response);
    }];
}






#pragma mark - private

/**
 *  HTML読み込み
 *
 *  @param webView
 */
- (void)loadExamplePage:(UIWebView*)webView {
    NSString* htmlPath = [[NSBundle mainBundle] pathForResource:@"ExampleApp" ofType:@"html"];
    NSString* appHtml = [NSString stringWithContentsOfFile:htmlPath encoding:NSUTF8StringEncoding error:nil];
    NSURL *baseURL = [NSURL fileURLWithPath:htmlPath];
    [webView loadHTMLString:appHtml baseURL:baseURL];
}

/**
 *  bridge生成
 */
- (void)createBridge
{
    self.bridge = [WebViewJavascriptBridge bridgeForWebView:self.webView];
}


/**
 *  ペッパーが発火させたイベントのコールバックを受け取る処理を登録
 */
- (void)registerPeppperEventCallBack
{
    [self.bridge registerHandler:@"pepperEvent" handler:^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ペッパーが発火したイベントをObjCで受け取るサンプル %@", data);
        responseCallback(@"ObjCで受け取ったかどうかはNSLogで確認");
    }];

    [self.bridge registerHandler:@"jsEvent" handler:^(id data, WVJBResponseCallback responseCallback) {
        NSLog(@"ボタンイベントが発火したイベントをObjCで受け取るサンプル %@", data);
        responseCallback(@"ObjCで受け取ったかどうかはNSLogで確認");
    }];
}

@end
