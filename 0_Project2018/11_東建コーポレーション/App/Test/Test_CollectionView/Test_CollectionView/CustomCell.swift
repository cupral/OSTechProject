//
//  CustomCell.swift
//  Test_CollectionView
//
//  Created by AnzaiTakayuki on 2018/06/21.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//
import UIKit

class CustomCell: UICollectionViewCell {
    @IBOutlet var imgSample:UIImageView!
    @IBOutlet var lblSample:UILabel!
    
    override init(frame: CGRect){
        super.init(frame: frame)
    }
    required init(coder aDecoder: NSCoder){
        super.init(coder: aDecoder)!
    }
}
