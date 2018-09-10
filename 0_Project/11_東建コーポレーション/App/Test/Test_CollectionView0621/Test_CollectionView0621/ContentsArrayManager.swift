//
//  ContentsArrayManager.swift
//  Test_CollectionView0621
//
//  Created by AnzaiTakayuki on 2018/06/28.
//  Copyright © 2018年 OutsourcingTechnology. All rights reserved.
//

import Foundation

class ContentsArrayManager{
    static let shared = ContentsArrayManager()
    private init(){}
    
    func getCheckNum(contentsNum:Int) -> Int{
        let checkNum:Int = CONTENTS_ARRAY[contentsNum][0] as! Int
        return checkNum
    }
    
    func getData(contentsNum:Int,checkNum:Int) -> [String]{
//        let checkNum:Int = CONTENTS_ARRAY[contentsNum][0] as! Int
        var dataArray:[String] = []
        if checkNum == 0{
            dataArray.append((CONTENTS_ARRAY[contentsNum][1] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][2] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][3] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][4] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][5] as! String))
        }else if checkNum == 1{
            dataArray.append((CONTENTS_ARRAY[contentsNum][6] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][7] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][8] as! String))
            dataArray.append((CONTENTS_ARRAY[contentsNum][9] as! String))
        }else{
            print("=== ContentsArrayManager.getData : checkNum :Error ===")
            return []
        }
        return dataArray
    }
    
    func setCheckNum(contentsNum:Int,checkNum:Int){
        CONTENTS_ARRAY[contentsNum][0] = checkNum
    }
    
    func initArray(){
        var cnt:Int = 0
        
        for i in 0..<CONTENTS_ARRAY.count {
            CONTENTS_ARRAY[i][0] = 0
            cnt = cnt + 1
            print("initArray : true : " + cnt.description)
        }
    }



}
