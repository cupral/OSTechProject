//
//  FreeView.swift
//  Test_CollectionView0621
//
//  Created by AnzaiTakayuki on 2018/06/22.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import Foundation
import UIKit

class FreeView: UIViewController,UIViewControllerTransitioningDelegate,UICollectionViewDataSource,UICollectionViewDelegate,UICollectionViewDelegateFlowLayout{
    
    @IBOutlet var baseView: UIView!
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var giveUp: UIButton!
    @IBOutlet weak var freeTitle: UILabel!
    @IBOutlet weak var beaconBtn: UIButton!
    let navigation = UINavigationController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("viewDidLoad")
        
        // フォント自動調整
        adjustsFontSize()
        //コレクションビュー更新
        collectionView.reloadData()
        

    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        print("viewWillAppear")
        collectionView.reloadData()
    }

    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        print("viewWillLayoutSubviews")
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        print("viewDidLayoutSubviews")
    }
    
    override func viewDidAppear(_ animated:Bool) {
        print("viewDidAppear")
    }
    
    override func viewWillDisappear(_ animated:Bool) {
        print("viewWillDisappear")
    }
    
    override func viewDidDisappear(_ animated:Bool) {
        print("viewDidDisappear")
        
    }
    
    //MARK: - ボタン廻り
    //やめるボタン押下時の反応
    @IBAction func touchGiveUpBtn(_ sender: UIButton) {
        AlertManager.sharedInstance.showAlertView(title:DELETE_TITLE ,message: DELETE_MESSAGE, actionTitles: ["OK","Cancel"], actions: [{
                ContentsArrayManager.shared.initArray()
                // FreeViewを閉じる
                self.dismiss(animated: false, completion: nil)
            },{}])
    }
    //beaconを受信したと仮定するテスト用ボタン
    @IBAction func beaconTest(_ sender: UIButton) {
        let random = (Int)(arc4random_uniform(45))
        let checkNum = ContentsArrayManager.shared.getCheckNum(contentsNum: random)
        if checkNum == 0{
            //確認数値を変更
            ContentsArrayManager.shared.setCheckNum(contentsNum: random, checkNum: 1)
            //コレクションビュー更新
            collectionView.reloadData()
            //ExplainModalViewを表示
            showExplainModalView(contentsNum: random)
        }
    }

    
    //データの個数を返すメソッド
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 45
    }
    
    //データを返すメソッド
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        //コレクションビューから識別子「TestCell」のセルを取得する。
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "iconCell", for: indexPath) as UICollectionViewCell
        let checkNum = ContentsArrayManager.shared.getCheckNum(contentsNum: indexPath.row)
        let dataArray:[String] = ContentsArrayManager.shared.getData(contentsNum: indexPath.row, checkNum: checkNum)
        cell.backgroundColor = UIColor(patternImage: UIImage(named: dataArray[0])!)
        return cell
    }
    
    //触った時の反応
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        //ヒントか説明かを判断する数値を取得
        let checkNum = ContentsArrayManager.shared.getCheckNum(contentsNum: indexPath.row)
        
        //ダイアログを出力
        if checkNum == 0{
            showHintModalView(contentsNum:indexPath.row)
        }else if checkNum == 1{
            showExplainModalView(contentsNum: indexPath.row)
        }
    }
    
    
    //MARK:- ModalView関連
    func showHintModalView(contentsNum:Int){
        ModalViewManager.shared.setHintDataArray(contentsNum:contentsNum)   //情報番号をマネージャにセット

        let sb = UIStoryboard(name: "HintView", bundle: nil)
        let vc = sb.instantiateInitialViewController() as! HintModalView
        vc.modalPresentationStyle = .overCurrentContext
        present(vc,animated:false)
        
//        let modal = HintModalView(nibName: nil, bundle: nil)    // 表示するモーダルビューを作成
//        modal.modalTransitionStyle = .crossDissolve // モーダルビューの現れ方を指定
//        present(modal, animated: true, completion: nil) // モーダルビューを表示する
    }
    
    func showExplainModalView(contentsNum:Int){
        ModalViewManager.shared.setExplainDataArray(contentsNum:contentsNum)   //情報番号をマネージャにセット
        
        let sb = UIStoryboard(name: "ExplainView", bundle: nil)
        let vc = sb.instantiateInitialViewController() as! ExplainModalView
        vc.modalPresentationStyle = .overCurrentContext
        present(vc,animated:false)
//        ModalViewManager.shared.setExplainDataArray(contentsNum:contentsNum)   //情報番号をマネージャにセット
//        let modal = ExplainModalView(nibName: nil, bundle: nil)    // 表示するモーダルビューを作成
//        modal.modalTransitionStyle = .crossDissolve // モーダルビューの現れ方を指定
//        present(modal, animated: true, completion: nil) // モーダルビューを表示する
    }

    func adjustsFontSize(){
        //自動フォントサイズ調節
        freeTitle.adjustsFontSizeToFitWidth = true
        freeTitle.minimumScaleFactor = 0.3
        beaconBtn.titleLabel?.adjustsFontSizeToFitWidth = true
        beaconBtn.titleLabel?.minimumScaleFactor = 0.3
        giveUp.titleLabel?.adjustsFontSizeToFitWidth = true
        giveUp.titleLabel?.minimumScaleFactor = 0.3
    }
    
    
    
    
    
    
    
}

