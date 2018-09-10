//
//  ViewController.swift
//  Test_CollectionView
//
//  Created by AnzaiTakayuki on 2018/06/19.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    
    // ステータスバーの高さ
    let statusBarHeight = UIApplication.shared.statusBarFrame.height
    
    // セル再利用のための固有名
    let cellId = "itemCell"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // 画面全体を緑色に設定
        self.view.backgroundColor = UIColor.green
        
        // 大きさとレイアウトを指定して UICollectionView を作成
        let collectionView = UICollectionView(
            frame: CGRect(x: 0, y: statusBarHeight, width: self.view.frame.width, height: self.view.frame.size.height - statusBarHeight),
            collectionViewLayout: UICollectionViewFlowLayout())
        
        // アイテム表示領域を白色に設定
        collectionView.backgroundColor = UIColor.white
        
        // セルの再利用のための設定
        collectionView.register(UICollectionViewCell.self, forCellWithReuseIdentifier: cellId)
        
        // デリゲート設定
        collectionView.delegate = self
        collectionView.dataSource = self
        
        
        // UICollectionView を表示
        self.view.addSubview(collectionView)
    }
    
    // 表示するアイテムの数を設定（UICollectionViewDataSource が必要）
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 24
    }
    
    // アイテムの大きさを設定（UICollectionViewDelegateFlowLayout が必要）
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        
        let size = self.view.frame.width / 4
        
        return CGSize(width: size, height: size)
    }
    
    // アイテム表示領域全体の上下左右の余白を設定（UICollectionViewDelegateFlowLayout が必要）
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        
        let inset =  (self.view.frame.width / 4) / 6
        
        return UIEdgeInsets(top: inset, left: inset, bottom: inset, right: inset)
    }
    
    // アイテムの上下の余白の最小値を設定（UICollectionViewDelegateFlowLayout が必要）
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return (self.view.frame.width / 4) / 3
    }
    
    // アイテムの表示内容（UICollectionViewDataSource が必要）
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        // アイテムを作成
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: cellId, for: indexPath)
        cell.backgroundColor = UIColor.lightGray
        
        // アイテムセルを再利用する際、前に追加していた要素（今回はラベル）を削除する
        for subview in cell.contentView.subviews {
            subview.removeFromSuperview()
        }

        // テキストラベルを設定して表示
        let label = UILabel()
        label.font = UIFont(name: "Arial", size: 24)
        label.text = "Item \(indexPath.row)"
        label.sizeToFit()
        label.center = cell.contentView.center
        cell.contentView.addSubview(label)
        
        return cell
    }
    
    // アイテムタッチ時の処理（UICollectionViewDelegate が必要）
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        print(indexPath.row)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
