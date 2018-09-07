//
//  ViewController.swift
//  Signage
//
//  Created by AnzaiTakayuki on 2018/05/23.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var imageViewB: UIImageView!
    @IBOutlet weak var imageViewA: UIImageView!
    var matchTimer:Timer!
    var loopTimer:Timer!
    
    var tabletWidth:Int = 768
    var tabletHeight:Int = 1024
    
    //scroll
    var img:[UIImage] = []
    var imageView01: [UIImageView?] = []
    
    //setting.Bundle
    var ptn:Int = 100
    
    @IBOutlet weak var scrView: UIScrollView!
    @IBOutlet weak var imageView: UIImageView!
    
    //loading
    var ActivityIndicator: UIActivityIndicatorView!
    
//====================================================================================
//    view Did Load ViewControllerが表示するView階層の全てがメモリ上に読み込まれたタイミングで呼び出し
//====================================================================================
    override func loadView() {
        super.loadView()
        print("loadView")
        
        //アプリインストール、起動時
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("viewDidLoad")
        NotificationCenter.default.addObserver(self, selector: #selector(ViewController.viewWillEnterForeground(_:)),
                                               name: NSNotification.Name.UIApplicationWillEnterForeground, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(ViewController.viewDidEnterBackground(_:)),
                                               name: NSNotification.Name.UIApplicationDidEnterBackground, object: nil)
        
    }
//====================================================================================
//    viewDidAppear Viewが完全に表示された際に呼ばれます。
//====================================================================================
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("viewWillAppear")

    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("viewWillLayoutSubviews")
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        print("viewDidLayoutSubviews")
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        print("viewDidAppear")
        initFunction()

    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        print("viewWillDisappear")
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        print("viewDidDisappear")
        
    }
    
    

//====================================================================================
//    フォアグラウンド or バックグラウンド
//====================================================================================
    @objc func viewWillEnterForeground(_ notification: Notification?) {
        if (self.isViewLoaded && (self.view.window != nil)) {
            print("フォアグラウンド")
            initFunction()
        }
    }
    
    @objc func viewDidEnterBackground(_ notification: Notification?) {
        if (self.isViewLoaded && (self.view.window != nil)) {
            print("バックグラウンド")
            if imageView01.count == 1{
                print("through")
            }else{
                matchTimer.invalidate()
                loopTimer.invalidate()
            }
        }
    }

//====================================================================================
//    初期化 関数
//====================================================================================
    func initFunction(){
        var _appStart:Bool = true;
        do{
            try getSettingSlideSecond()
        }catch{
            print("スライド間隔秒数エラー")
            _appStart = false
            createSimpleDialog()
        }
        if _appStart{
            ignoring(check: true)
            loadingTextViewSwitch(check: true)
            let imgCount = createImageArray()
            setLoadingIcon()
            settingScrView()
            hideSlideBar()
            
            //画像1枚時処理
            if imgCount == 1{
                //クルクル停止
                ActivityIndicator.stopAnimating()
                //loadingTextView停止
                loadingTextViewSwitch(check:false)
                
//                let image = img[0]
                var xS:CGFloat = 0
                let yS:CGFloat = 0
                let widthS:CGFloat = CGFloat(tabletWidth)
                let heightS:CGFloat = CGFloat(tabletHeight)
                //全体のサイズ ScrollViewフレームサイズ取得
                let SVSize = scrView.frame.size
                self.scrView.contentSize = CGSize(width:SVSize.width * CGFloat(tabletWidth),height:SVSize.height)
                var offset: CGPoint
                offset = CGPoint(x:tabletWidth,y:0)
                self.scrView.setContentOffset(offset,animated:true)
                
                //ScrollViewに追加
                xS = CGFloat(tabletWidth)
                self.imageView01[0]!.frame = CGRectMake(xS,yS,widthS,heightS)
                //ScrollViewに追加
                scrView.addSubview(imageView01[0]!)
                
            }else{
                matchTimer = Timer.scheduledTimer(timeInterval: 0.001, target: self, selector: #selector(self.matchSlideTiming), userInfo: nil, repeats: true)
            }
            
        }
    }

//====================================================================================
//    タブレット操作禁止
//====================================================================================
    func ignoring(check:Bool){
        if check{
            //タブレット操作無効
            UIApplication.shared.beginIgnoringInteractionEvents()
        }else{
            //タブレット操作有効
            UIApplication.shared.endIgnoringInteractionEvents()
        }

    }
    
//====================================================================================
//    Scroll View サイズ 設定
//====================================================================================
    func settingScrView(){
        //全体のサイズ ScrollViewフレームサイズ取得
        let SVSize = scrView.frame.size
        //画像サイズ * 3 高さ CGSizeMake(240*3,240)
        scrView.contentSize = CGSize(width:SVSize.width * CGFloat(img.count),height:SVSize.height)
        //1ページ単位でスクロールさせる
        scrView.isPagingEnabled = true
        //scroll画面の初期位置
        scrView.contentOffset = CGPoint(x:0,y:0)
    }
    
//====================================================================================
//    Loading アイコン表示
//====================================================================================
    func setLoadingIcon(){
        ActivityIndicator = UIActivityIndicatorView()
        ActivityIndicator.frame = CGRect(x: 0,y:0,width:300,height:300)
        ActivityIndicator.center = self.view.center
        
        //クルクルをストップしたときに非表示にする
        ActivityIndicator.hidesWhenStopped = true
        //色を設定
        ActivityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.white
        //Viewに追加
        self.view.addSubview(ActivityIndicator)
        ActivityIndicator.startAnimating()
    }
//====================================================================================
//    スライドバー非表示
//====================================================================================
    func hideSlideBar(){
        //スクロールバー非表示
        self.scrView.showsVerticalScrollIndicator = false;
        self.scrView.showsHorizontalScrollIndicator = false;
    }
    
//====================================================================================
//    画像配列 生成
//====================================================================================
    func createImageArray()->Int?{
        //画像配列を設定
        img = [UIImage(named:"image01.png")!,
               UIImage(named:"image02.png")!,
               UIImage(named:"image03.png")!,
        ]
        //UIImageViewにUIImageを追加
        ptn = getSettingSlidePattern()!
        print("pattern : " + ptn.description)
        switch ptn {
        case 0: imageView01 = [UIImageView(image:img[0]),UIImageView(image:img[1])]; break
        case 1: imageView01 = [UIImageView(image:img[1]),UIImageView(image:img[0])]; break
        case 2: imageView01 = [UIImageView(image:img[0]),UIImageView(image:img[1]),UIImageView(image:img[2])]; break
//        case 2: imageView01 = [UIImageView(image:img[0])]; break
        case 3: imageView01 = [UIImageView(image:img[1]),UIImageView(image:img[2]),UIImageView(image:img[0])]; break
        case 4: imageView01 = [UIImageView(image:img[2]),UIImageView(image:img[0]),UIImageView(image:img[1])]; break
        default: break
        }
        return imageView01.count
    }
    
//====================================================================================
//    matchSlideTiming スライドタイミングの調節
//====================================================================================

    @objc func matchSlideTiming(tm:Timer){
        let now = NSDate()
        let formatter = DateFormatter()
        formatter.dateFormat = "hh:mm:ss.SSS"
        let nowTime = formatter.string(from: now as Date)
//        print(nowTime)
        formatter.dateFormat = "HH"
        let hour = formatter.string(from: now as Date)
        formatter.dateFormat = "mm"
        let minute = formatter.string(from: now as Date)
        formatter.dateFormat = "ss"
        let second = formatter.string(from: now as Date)
        
        let hourInt:Int = Int(hour)!
        let minuteInt:Int = Int(minute)!
        let secondInt:Int = Int(second)!
        let total = hourInt * 3600 + minuteInt * 60 + secondInt
        do{
            setSecond = (try getSettingSlideSecond())!
        }catch{
            print("1~30以外")
        }
        
        // スライドするタイミングかどうか判断
        let decimal:Float = Float(Double(total) / Double(setSecond))
        print("decimal : " + decimal.description)
        let slideGo = compareNealyInteger(a: Float(decimal))
        
        // trueの連発を防ぐ
        if repeatPath{
            if slideGo {
                let result:Float = Float(decimal) / Float(imageView01.count)
                print("result : " + decimal.description)
                let res = compareNealyInteger(a: Float(result))
                print("pettern : " + ptn.description)
                switch ptn {
                case 0,1:
                    if res{startImageNum = 0}
                    else{startImageNum = 1}
                    break
                case 2,3,4:
                    if res{startImageNum = 0}
                    else{
                        let shisha = result - floor(result)
                        let numRound = round(shisha)
                        if numRound == 0{startImageNum = 1}
                        else if numRound == 1{startImageNum = 2}
                    }
                    break
                default:
                    break
                }
                print("startImageNum : " + startImageNum.description)
                
                repeatPath = false
                //クルクル停止
                ActivityIndicator.stopAnimating()
                //loadingTextView停止
                loadingTextViewSwitch(check:false)
                //画像スクロール
                pagingScrollView(sttNum: startImageNum)
                // タイマー停止
                matchTimer.invalidate()
                startImageNum = startImageNum + 1
                //                let sec:Int = getSettingSlideSecond()!
                loopTimer = Timer.scheduledTimer(
                    timeInterval: TimeInterval(setSecond),
                    target: self,
                    selector: #selector(self.timerMain),
                    userInfo: nil,
                    repeats:true
                )
            }
        }
        if slideGo == false{
            repeatPath = true
        }
    }
    
//====================================================================================
//    main timer
//====================================================================================

    @objc func timerMain(tm:Timer){
        slideLoop()
    }

//====================================================================================
//    現在時刻 取得
//====================================================================================

    var repeatPath:Bool = true
    var startImageNum:Int = 0
    var setSecond:Int = 30
    
    func getNowTime(){
        
        

    }

    

//====================================================================================
//    Init Loop
//====================================================================================
    var trueCount:Int = 0
    var intTag:Int = 0
    //    var imageCount:Int = 0
    
    func pagingScrollView(sttNum:Int){
        
        var xP:CGFloat = 0
        let yP:CGFloat = 0
        let widthP:CGFloat = CGFloat(tabletWidth)
        let heightP:CGFloat = CGFloat(tabletHeight)
        
        trueCount = trueCount + 1
        
        //全体のサイズ ScrollViewフレームサイズ取得
        let SVSize = scrView.frame.size
        self.scrView.contentSize = CGSize(width:SVSize.width * CGFloat(trueCount),height:SVSize.height)
        var offset: CGPoint
        offset = CGPoint(x:tabletWidth*trueCount,y:0)
        self.scrView.setContentOffset(offset,animated:true)
        
        //ScrollViewに追加
        xP = CGFloat(tabletWidth * trueCount)
        
        self.imageView01[sttNum]!.frame = CGRectMake(xP,yP,widthP,heightP)
        //tag設定
        imageView01[sttNum]?.tag = intTag
        //ScrollViewに追加
        scrView.addSubview(imageView01[sttNum]!)
        
        
        var sttNum = sttNum + 1
        if (sttNum==imageView01.count) {
            sttNum = 0
        }
    }

    
//====================================================================================
//    メインループ
//====================================================================================
    
    var viewCount:Int = 10
    var tagNum:Int = 0
    func slideLoop(){
//        print(imageView01.count)
        if (imageView01.count - 1 < startImageNum) {
            startImageNum = 0
        }
        var xS:CGFloat = 0
        let yS:CGFloat = 0
        let widthS:CGFloat = CGFloat(tabletWidth)
        let heightS:CGFloat = CGFloat(tabletHeight)
        
        trueCount = trueCount + 1
        viewCount = viewCount + 1 //subviewの数
        
        //全体のサイズ ScrollViewフレームサイズ取得
        let SVSize = scrView.frame.size
        self.scrView.contentSize = CGSize(width:SVSize.width * CGFloat(trueCount),height:SVSize.height)
        var offset: CGPoint
        offset = CGPoint(x:tabletWidth*trueCount,y:0)
        self.scrView.setContentOffset(offset,animated:true)
        
        //ScrollViewに追加
        xS = CGFloat(tabletWidth * trueCount)
        
        self.imageView01[startImageNum]!.frame = CGRectMake(xS,yS,widthS,heightS)
        
        
        
        //2つ前のSubviewを削除
        tagNum = viewCount - 2
        var fetchedView = scrView.viewWithTag(tagNum)
        fetchedView?.removeFromSuperview()
        //tag追加
        imageView01[startImageNum]?.tag = viewCount
        //ScrollViewに追加
        scrView.addSubview(imageView01[startImageNum]!)
        
        startImageNum = startImageNum + 1
        if (imageView01.count - 1 < startImageNum) {
            startImageNum = 0
        }


    }
    
//====================================================================================
// 小数点以下 有無
//====================================================================================
    func compareNealyInteger(a:Float) -> Bool {
        let b:Float = floorf(a)
        return a - b < Float.ulpOfOne
    }
    
//====================================================================================
// loadingテキストを切り替え
//====================================================================================
    func loadingTextViewSwitch(check:Bool){
        if check{
            let label = UILabel()
            label.text = "スライドタイミング調節中"
            label.font = UIFont(name: "HiraMinProN-W3", size: 30)
            label.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0)
            label.textColor = UIColor.white
            label.sizeToFit()
            label.center = CGPoint(x:375,y:400)
            label.tag = 30
            self.view.addSubview(label)
        }else{
            self.view.subviews.forEach{
                if $0.tag == 30{
                    $0.removeFromSuperview()
                }
            }
        }
    }
    
//====================================================================================
//    エラーダイアログ
//====================================================================================
    func createSimpleDialog(){
        let alert = UIAlertController(title: "設定値エラー",
                                      message: "スライド間隔秒数欄に半角で1~30の数字を入力してください。",
                                      preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { (action: UIAlertAction) in
            print("OK")
            exit(0)
            self.dismiss(animated: true, completion: nil)
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    
//====================================================================================
//    Setting.Bundle参照
//====================================================================================
    
    //設定アプリで定められたパターンの取得
    func getSettingSlidePattern()->Int?{
        var patternStr:String = ""
        let defaults = UserDefaults()
        if( defaults.object(forKey: "slide_pattern") != nil ) {
            patternStr = defaults.object(forKey:"slide_pattern" ) as! String
        }else{
            print("SlidePatternを取得できませんでした。")
            return nil
        }
        let patternNum:Int = Int(patternStr)!
        return patternNum
    }
    
    func convertFullToHalf(str:String) -> String{
        let str = NSMutableString(string: str) as CFMutableString
        CFStringTransform(str, nil, kCFStringTransformFullwidthHalfwidth, true)
        return str as String
    }
    
    //設定アプリで定められた間隔秒数の取得
    func getSettingSlideSecond()throws -> Int?{
        var secondStr:String = ""
        var hankaku:String = ""
        let defaults = UserDefaults()
        if( defaults.object(forKey: "slide_interval") != nil ) {
            // 全角数字 or 半角数字 or 文字
            secondStr = defaults.object(forKey:"slide_interval" ) as! String
            //半角化
            if #available(iOS 9.0, *) {
                hankaku = secondStr.applyingTransform(.fullwidthToHalfwidth, reverse: false)!
            } else {
//                hankaku = convertFullToHalf(str: secondStr)
                hankaku = secondStr
            }

            //数値化
            if let secondNum:Int = Int((hankaku)){
                if 1...30 ~= secondNum{
                    return secondNum
                }else{
                    print("1~30以外")
                    throw NSError(domain: "1~30以外", code: -2, userInfo: nil)
                }
            }else{
                print("文字列指定エラー")
                throw NSError(domain: "文字列指定エラー", code: -1, userInfo: nil)
            }
        }else{
            print("SlideSecondを取得できませんでした。")
            throw NSError(domain:"未指定エラー",code:-3,userInfo:nil)
        }
        return nil
    }
    
    

//====================================================================================
//    オプション
//====================================================================================

    //CGRectMake -> CGRect
    func CGRectMake(_ x: CGFloat, _ y: CGFloat, _ width: CGFloat, _ height: CGFloat) -> CGRect {
        return CGRect(x: x, y: y, width: width, height: height)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}



