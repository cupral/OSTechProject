//
//  HintModalView.swift
//  Test_CollectionView0621
//
//  Created by AnzaiTakayuki on 2018/06/28.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import UIKit

class ExplainModalView:UIViewController{
    
    
    var hintModalView: HintModalView!
    
    @IBOutlet weak var baseView: UIView!
    @IBOutlet weak var explainView: UIView!
    
    @IBOutlet weak var explainImage: UIImageView!
    @IBOutlet weak var titleView: UITextField!
    @IBOutlet weak var messageView: UITextView!
    let navigation = UINavigationController()
    
    
    override func viewWillAppear(_ animated: Bool) {
        print("ExplainModal : viewWillAppear")
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("ExplainModal : viewWillLayoutSubviews")
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        print("ExplainModal : viewDidLayoutSubviews")
    }
    
    override func viewDidAppear(_ animated:Bool) {
        print("ExplainModal : viewDidAppear")
    }
    
    override func viewWillDisappear(_ animated:Bool) {
        print("ExplainModal : viewWillDisappear")
    }
    
    override func viewDidDisappear(_ animated:Bool) {
        print("ExplainModal : viewDidDisappear")
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let dataArray:[String] = ModalViewManager.shared.getExplainDataArray()
        print(dataArray)
        explainImage.image = UIImage(named:dataArray[1])
        titleView.text = dataArray[2]
        messageView.text = dataArray[3]
        
    }
    
    @IBAction func closeButton(_ sender: UIButton) {
//        HintModalViewを閉じる
//        UIApplication.shared.keyWindow?.rootViewController?
//            .dismiss(animated: true, completion: nil)
//
//        // rootViewをFreeViewに設定する
//        let storyboard = UIStoryboard(name: "Main",bundle:nil)
//        let vc = storyboard.instantiateViewController(withIdentifier: "freeView")
//        UIApplication.shared.keyWindow?.rootViewController = vc
//
//        // rootViewへ戻る
//        navigation.popToRootViewController(animated: true)
        
        // ExplainViewを閉じる
        let viewTitle = presentingViewController?.title
        if viewTitle == "HintView"{
            dismiss(animated: false,completion: {
                [presentingViewController]() -> Void in
                // 閉じた時に行いたい処理
                presentingViewController?.dismiss(animated: false, completion: nil)
            })
        }else if viewTitle == "FreeViewPage"{
            dismiss(animated: false, completion: nil)
        }
    }

    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}
