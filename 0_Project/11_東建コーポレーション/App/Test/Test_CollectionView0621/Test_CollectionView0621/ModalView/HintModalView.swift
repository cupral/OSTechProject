//
//  HintModalView.swift
//  Test_CollectionView0621
//
//  Created by AnzaiTakayuki on 2018/06/28.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import Foundation
import UIKit

class HintModalView:UIViewController{
    
    @IBOutlet weak var baseView: UIView!
    @IBOutlet weak var hintView: UIView!
    @IBOutlet weak var hintImage: UIImageView!
    @IBOutlet weak var mapView: UIImageView!
    @IBOutlet weak var messageView: UITextView!
    @IBOutlet weak var titleView: UITextField!
    @IBOutlet weak var transitionBtn: UIButton!
    @IBOutlet weak var closeBtn: UIButton!

    
    
    override func viewWillAppear(_ animated: Bool) {
        print("HintModal : viewWillAppear")
        
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("HintModal : viewWillLayoutSubviews")
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        print("HintModal : viewDidLayoutSubviews")
    }
    
    override func viewDidAppear(_ animated:Bool) {
        print("HintModal : viewDidAppear")
    }
    
    override func viewWillDisappear(_ animated:Bool) {
        print("HintModal : viewWillDisappear")
        presentingViewController?.viewWillAppear(true)
    }
    
    override func viewDidDisappear(_ animated:Bool) {
        print("HintModal : viewDidDisappear")
        
    }
    
    
    @IBAction func beacapTest(_ sender: UIButton) {
        transitionBtn.isEnabled = true
    }
    
    
    @IBAction func transitionBtn(_ sender: Any) {
        let random = (Int)(arc4random_uniform(45))
        let checkNum = ContentsArrayManager.shared.getCheckNum(contentsNum: random)
        if checkNum == 0{
            //確認数値を変更
            ContentsArrayManager.shared.setCheckNum(contentsNum: random, checkNum: 1)
            ModalViewManager.shared.setExplainDataArray(contentsNum: random)
            
            //ExplainModalViewを表示
            let sb = UIStoryboard(name: "ExplainView", bundle: nil)
            let vc = sb.instantiateInitialViewController() as! ExplainModalView
            vc.modalPresentationStyle = .overCurrentContext
            present(vc,animated:false)
            
            let storyboard = UIStoryboard(name: "HintView",bundle:nil)
            let viewCon = storyboard.instantiateViewController(withIdentifier: "hintView")
            viewCon.dismiss(animated: false, completion: nil)
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        transitionBtn.isEnabled = false
        
        adjustsFontSize()
        
        let dataArray:[String] = ModalViewManager.shared.getHintDataArray()
        print(dataArray)
        
        hintImage.image = UIImage(named:dataArray[1])
        titleView.text = dataArray[2]
        messageView.text = dataArray[3]
        mapView.image = UIImage(named: dataArray[4])
        
    }
    
    @IBAction func closeButton(_ sender: Any) {
        dismiss(animated: false,completion: {
            [presentingViewController]() -> Void in
                //閉じた時に行いたい処理
                presentingViewController?.viewWillAppear(true)
        })
    }
    
    func closeView(){
        dismiss(animated: false)
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func adjustsFontSize(){
        //自動フォントサイズ調節
        titleView.adjustsFontSizeToFitWidth = true
        titleView.minimumFontSize = 0.3
        transitionBtn.titleLabel?.adjustsFontSizeToFitWidth = true
        transitionBtn.titleLabel?.minimumScaleFactor = 0.3
        closeBtn.titleLabel?.adjustsFontSizeToFitWidth = true
        closeBtn.titleLabel?.minimumScaleFactor = 0.3
        

    }
    
    
    
}
