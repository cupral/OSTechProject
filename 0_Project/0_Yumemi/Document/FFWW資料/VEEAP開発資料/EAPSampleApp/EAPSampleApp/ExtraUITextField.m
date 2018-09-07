//
//  ExtraUITextField.m
//  EAPSampleApp
//
//  Created by user on 2018/02/09.
//  Copyright © 2018年 HiICS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ExtraUITextField.h"

@implementation ExtraUITextField

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if ( action == @selector(paste:)) {
        return NO;
    }
    return [super canPerformAction:action withSender:sender];
}

@end
