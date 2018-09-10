//
//  ViewController.swift
//  FontListCheck
//
//  Created by AnzaiTakayuki on 2018/06/11.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let view = UIScrollView(frame: self.view.bounds)
        view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.contentSize = .zero
        
        UIFont.familyNames.forEach { (familyName) in
            let fontsInFamily = UIFont.fontNames(forFamilyName: familyName)
            fontsInFamily.forEach({ (fontName) in
                print(fontName)
                let label = UILabel()
                label.text = "フォント：" + fontName
                label.font = UIFont(name: fontName, size: UIFont.labelFontSize)
                label.sizeToFit()
                label.frame.origin.y = view.contentSize.height
                view.contentSize.width = max(view.contentSize.width, label.frame.width)
                view.contentSize.height += label.frame.height + 10
                view.addSubview(label)
            })
        }
        
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

