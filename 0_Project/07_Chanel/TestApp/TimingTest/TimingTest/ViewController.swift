//
//  ViewController.swift
//  TimingTest
//
//  Created by AnzaiTakayuki on 2018/06/09.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    var syncSlideTimer:Timer!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        startSyncSlideTimer()
        // Do any additional setup after loading the view, typically from a nib.
    }

    func startSyncSlideTimer() {
        print("==========start syncSlideTimer==========")
        syncSlideTimer = Timer.scheduledTimer(timeInterval: 0.001, target: self, selector: #selector(self.matchSlideTiming), userInfo: nil, repeats: true)
    }
    //MARK: - スライド同期 (タイミングの調節)
    @objc func matchSlideTiming(tm:Timer){
        let now = NSDate()
        let formatter = DateFormatter()
        formatter.dateFormat = "hh:mm:ss.SSS"
        formatter.dateFormat = "HH"
        let hour = formatter.string(from: now as Date)
        formatter.dateFormat = "mm"
        let minute = formatter.string(from: now as Date)
        formatter.dateFormat = "ss"
        let second = formatter.string(from: now as Date)
        formatter.dateFormat = "SSS"
        let msecond = formatter.string(from: now as Date)
        
        let hourInt:Int = Int(hour)!
        let minuteInt:Int = Int(minute)!
        let secondInt:Int = Int(second)!
        let msecondInt:Int = Int(msecond)!
        let total = hourInt * 3600000 + minuteInt * 60000 + secondInt * 1000 + msecondInt


        let settingSecondInt = 3
        let imageCount:Int = 7
        
        // スライドするタイミングかどうか判断
        let decimal:Float = Float(Double(total) / Double(Preferences.intervalSecondInt! * 1000))

        if compareNealyInteger(a: Float(decimal)) {
            print("==================================================")
            print("現在経過秒数 : " + total.description + "秒")
            print("0:00から現在までのスライド : " + decimal.description + "回")
            let result:Float = Float(decimal) / Float(images.count)
            let ceilNum = ceil(result) //切り上げ
            print("切り上げ後数値 : " +  ceilNum.description)
            let blockNum = ceilNum * Float(images.count)
            let nokori = blockNum - decimal
            var finalImage = Float(images.count) - nokori
            finalImage = finalImage - 1
            var intNum: Int = Int(finalImage)
            print("最終結果 : " + intNum.description)
            startImageNum = intNum
        }
    }
    func compareNealyInteger(a:Float) -> Bool {
        let b:Float = floorf(a)
        return a - b < Float.ulpOfOne
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

