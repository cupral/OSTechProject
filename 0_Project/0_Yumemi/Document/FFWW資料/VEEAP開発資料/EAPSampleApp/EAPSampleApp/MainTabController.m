//
//  MainTabController.m
//  EAPSampleApp
//
//  Created by user on 2018/02/02.
//  Copyright © 2018年 HiICS. All rights reserved.
//

#import "MainTabController.h"
#import "ViewController.h"
#import "ViewControllerEventRes.h"

@implementation MainTabBarController

 - (void)viewDidLoad
{
    [super viewDidLoad];

    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    UIViewController *firstView = [storyboard instantiateViewControllerWithIdentifier:@"firstview"];
    UIViewController *secondView = [storyboard instantiateViewControllerWithIdentifier:@"secondview"];
    
#ifdef __STAND_ALONE__
    UIViewController *thirdView = [storyboard instantiateViewControllerWithIdentifier:@"thirdview"];
    
    
    NSArray *views = [NSArray arrayWithObjects:firstView, secondView, thirdView, nil];
    [self setViewControllers:views animated:NO];
#else /* __STAND_ALONE__ */
    
    NSArray *views = [NSArray arrayWithObjects:firstView, secondView, nil];
    [self setViewControllers:views animated:NO];
#endif /* __STAND_ALONE__ */

}

@end

