//
//  ModalViewManager.swift
//  Test_CollectionView0621
//
//  Created by AnzaiTakayuki on 2018/06/28.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import Foundation
import UIKit

class ModalViewManager{
    static let shared = ModalViewManager()

    var dataHintArray:[String] = []
    var dataExplainArray:[String] = []
    let navigation = UINavigationController()
    

    func setHintDataArray(contentsNum:Int){
        dataHintArray = ContentsArrayManager.shared.getData(contentsNum: contentsNum, checkNum: 0)
    }
    
    func getHintDataArray() -> [String]{
        return dataHintArray
    }
    
    func setExplainDataArray(contentsNum:Int){
        dataExplainArray = ContentsArrayManager.shared.getData(contentsNum: contentsNum, checkNum: 1)
    }
    
    func getExplainDataArray() -> [String]{
        return dataExplainArray
    }
    
    
    func closeHintView(){
        print("HintModalView : close")
//         HintModalViewを閉じる
        let storyboard = UIStoryboard(name: "HintView",bundle:nil)
        let vc = storyboard.instantiateViewController(withIdentifier: "hintView")
        vc.dismiss(animated: true, completion: nil)
//        UIApplication.shared.keyWindow?.rootViewController = vc
    }
    
    func closeExplainView(){
        print("HintModalView : close")
    }

    func changeBtnActive(activeNum:Int){
        print("changeBtnActive : activeNum : " + activeNum.description)
    }
    
//    func transitionExplainModal(contentsNum:Int){
//        //説明モーダルビューを出力
//        ExplainModalView.shared.showView(contentsNum:contentsNum)
//        //ヒントモーダルビューを閉じる
//        closeHintView()
//        print("transitionExplainModal : to ExplainModalView")
//    }
    

    
    
}
