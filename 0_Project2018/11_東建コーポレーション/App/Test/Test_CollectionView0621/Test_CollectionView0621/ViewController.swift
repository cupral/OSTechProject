//
//  ViewController.swift
//  Test_CollectionView0621
//
//  Created by AnzaiTakayuki on 2018/06/21.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import UIKit

class ViewController: UIViewController{
    
    @IBOutlet weak var route1Btn: UIButton!
    @IBOutlet weak var route2Btn: UIButton!
    @IBOutlet weak var route3Btn: UIButton!
    @IBOutlet weak var route4Btn: UIButton!
    @IBOutlet weak var route5Btn: UIButton!
    @IBOutlet weak var freeViewBtn: UIButton!
    @IBOutlet weak var routeViewTitle: UILabel!
    
    @IBAction func freeViewBtnAction(_ sender: Any) {
        performSegue(withIdentifier: "showFreeView", sender: nil)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "showFreeView"){
            
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("ルート選択画面起動")
        adjustsFontSize()
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func adjustsFontSize(){
        //自動フォントサイズ調節
        freeViewBtn.titleLabel?.adjustsFontSizeToFitWidth = true
        freeViewBtn.titleLabel?.minimumScaleFactor = 0.3
        route1Btn.titleLabel?.adjustsFontSizeToFitWidth = true
        route1Btn.titleLabel?.minimumScaleFactor = 0.3
        route2Btn.titleLabel?.adjustsFontSizeToFitWidth = true
        route2Btn.titleLabel?.minimumScaleFactor = 0.3
        route3Btn.titleLabel?.adjustsFontSizeToFitWidth = true
        route3Btn.titleLabel?.minimumScaleFactor = 0.3
        route4Btn.titleLabel?.adjustsFontSizeToFitWidth = true
        route4Btn.titleLabel?.minimumScaleFactor = 0.3
        route5Btn.titleLabel?.adjustsFontSizeToFitWidth = true
        route5Btn.titleLabel?.minimumScaleFactor = 0.3
        
    }

}

