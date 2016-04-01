//
//  SCUITabBarController.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUITabBarController.h"
#import "SCUtils.h"

#define NAVBAR_HEGHT            44

@interface SCUITabBarController ()

@end

@implementation SCUITabBarController

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillLayoutSubviews
{
    [super viewWillLayoutSubviews];
    [self moveTabBarToTop];
}

- (void)moveTabBarToTop
{
    //CGFloat yNavBar = self.navigationController.navigationBar.frame.size.height;
    CGFloat yStatusBar = [UIApplication sharedApplication].statusBarFrame.size.height;
    //self.tabBar.frame = CGRectMake(0, yNavBar+yStatusBar+self.tabBar.frame.size.height-HEIGHT_GAP_ADJUST_SIZE, self.tabBar.frame.size.width, self.tabBar.frame.size.height);
    
    CGRect tabFrame = self.tabBar.frame;
    tabFrame.origin.y = yStatusBar + NAVBAR_HEGHT;
    self.tabBar.frame = tabFrame;
    
    // indicator
    CGRect indicatorFrame = self.tabBar.frame;
    
    indicatorFrame.origin.y = indicatorFrame.origin.y + indicatorFrame.size.height - 2;
    indicatorFrame.size.height = 2;
    indicatorFrame.size.width /= 4;
    UIView *tabIndicator = [[UIView alloc] initWithFrame:indicatorFrame];
    //[tabIndicator setTranslatesAutoresizingMaskIntoConstraints:NO];
    [self.view addSubview:tabIndicator];
    [tabIndicator setBackgroundColor:[UIColor whiteColor]];
    
    [self.tabBar setBackgroundImage:[SCUtils createImageWithColor:SC_MAIN_COLOR]];
    [self.tabBar setShadowImage:[SCUtils createImageWithColor:[UIColor clearColor]]];
    
    // Set the translucent property to NO then back to YES to
    // force the UITabBar to reblur, otherwise part of the
    // new frame will be completely transparent if we rotate
    // from a landscape orientation to a portrait orientation.
    //self.tabBar.translucent = NO;
    //self.tabBar.translucent = YES;
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
