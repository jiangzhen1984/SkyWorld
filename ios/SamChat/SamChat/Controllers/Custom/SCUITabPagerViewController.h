//
//  SCUITabPagerViewController.h
//  SamChat
//
//  Created by HJ on 4/1/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SCUITabPagerDataSource;
@protocol SCUITabPagerDelegate;

@interface SCUITabPagerViewController : UIViewController

@property (weak, nonatomic) id<SCUITabPagerDataSource> dataSource;
@property (weak, nonatomic) id<SCUITabPagerDelegate> delegate;

- (void)reloadData;
- (NSInteger)selectedIndex;

- (void)selectTabbarIndex:(NSInteger)index;
- (void)selectTabbarIndex:(NSInteger)index animation:(BOOL)animation;

@end

@protocol SCUITabPagerDataSource <NSObject>

@required
- (NSInteger)numberOfViewControllers;
- (UIViewController *)viewControllerForIndex:(NSInteger)index;

@optional
- (UIView *)viewForTabAtIndex:(NSInteger)index;
- (NSString *)titleForTabAtIndex:(NSInteger)index;
- (CGFloat)tabHeight;
- (UIColor *)tabColor;
- (UIColor *)tabBackgroundColor;
- (UIFont *)titleFont;
- (UIColor *)titleColor;

@end

@protocol SCUITabPagerDelegate <NSObject>

@optional
- (void)tabPager:(SCUITabPagerViewController *)tabPager willTransitionToTabAtIndex:(NSInteger)index;
- (void)tabPager:(SCUITabPagerViewController *)tabPager didTransitionToTabAtIndex:(NSInteger)index;

@end