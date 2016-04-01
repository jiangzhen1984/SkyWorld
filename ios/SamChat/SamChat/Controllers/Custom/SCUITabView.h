//
//  SCUITabView.h
//  SamChat
//
//  Created by HJ on 4/1/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SCUITabDelegate;

@interface SCUITabView : UIView

@property (weak, nonatomic) id<SCUITabDelegate> tabDelegate;

- (instancetype)initWithFrame:(CGRect)frame tabViews:(NSArray *)tabViews tabBarHeight:(CGFloat)height tabColor:(UIColor *)color backgroundColor:(UIColor *)backgroundColor selectedTabIndex:(NSInteger)index;
- (instancetype)initWithFrame:(CGRect)frame tabViews:(NSArray *)tabViews tabBarHeight:(CGFloat)height tabColor:(UIColor *)color backgroundColor:(UIColor *)backgroundColor;

- (void)animateToTabAtIndex:(NSInteger)index;
- (void)animateToTabAtIndex:(NSInteger)index animated:(BOOL)animated;

@end

@protocol SCUITabDelegate <NSObject>

- (void)tabView:(SCUITabView *)tabView didSelectTabAtIndex:(NSInteger)index;

@end
