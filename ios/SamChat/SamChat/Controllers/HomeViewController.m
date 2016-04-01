//
//  HomeViewController.m
//  SamChat
//
//  Created by HJ on 3/30/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "HomeViewController.h"
#import "ConversationListController.h"
#import "ChatDemoHelper.h"

@interface HomeViewController () <SCUITabPagerDataSource, SCUITabPagerDelegate>
//{
//    NSArray *items;
//}

@property (nonatomic, strong) ConversationListController *chatListVC;
@end

@implementation HomeViewController

#pragma mark - View Controller Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self setDataSource:self];
    [self setDelegate:self];
    [self navigationBarStyle];
    [self reloadData];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
 //   [self reloadData];
}

- (void)navigationBarStyle
{
    self.navigationController.navigationBar.translucent = NO;
    [self.navigationController.navigationBar setBackgroundImage:[SCUtils createImageWithColor:SC_MAIN_COLOR] forBarMetrics:UIBarMetricsDefault];
    [self.navigationController.navigationBar setBackIndicatorImage:[SCUtils createImageWithColor:SC_MAIN_COLOR]];
    [self.navigationController.navigationBar setShadowImage:[SCUtils createImageWithColor:[UIColor clearColor]]];
    
    // NavigationBar
    UILabel *titleView = [[UILabel alloc] initWithFrame:CGRectZero];
    titleView.font = [UIFont fontWithName:@"Futura-Medium" size:19];
    titleView.textColor = [UIColor colorWithRed:0.333333 green:0.333333 blue:0.333333 alpha:1.0];
    titleView.text = @"Menu";
    [titleView sizeToFit];
    titleView.backgroundColor = [UIColor colorWithRed:((2) / 255.0) green:((168) / 255.0) blue:((244) / 255.0) alpha:1.0];
    self.navigationItem.titleView = titleView;
}

#pragma mark - Tab Pager Data Source

- (NSInteger)numberOfViewControllers {
    return 4;
}

- (UIViewController *)viewControllerForIndex:(NSInteger)index
{
    UIViewController *viewController = nil;
    switch (index) {
        case 1:
            viewController = [ChatDemoHelper shareHelper].conversationListVC;
            break;
        default:
            viewController = [UIViewController new];
            [[viewController view] setBackgroundColor:[UIColor colorWithRed:arc4random_uniform(255) / 255.0f
                                                          green:arc4random_uniform(255) / 255.0f
                                                           blue:arc4random_uniform(255) / 255.0f alpha:1]];
            break;
    }
    return viewController;
}

// Implement either viewForTabAtIndex: or titleForTabAtIndex:
- (UIView *)viewForTabAtIndex:(NSInteger)index
{
    //CGRect frame = CGRectMake(0, 0, self.view.frame.size.width/4, 50);
    //UIButton *buttonView = [[UIButton alloc] initWithFrame:frame];
    UIButton *buttonView = [[UIButton alloc] init];
    buttonView.backgroundColor = SC_MAIN_COLOR;
    [buttonView setTitle:[NSString stringWithFormat:@"%ld", index] forState:UIControlStateNormal];
    return buttonView;
}

//- (NSString *)titleForTabAtIndex:(NSInteger)index
//{
//  return [NSString stringWithFormat:@"Tab #%ld", (long) index + 1];
//}

- (CGFloat)tabHeight
{
    return 60.0f;
}

- (UIColor *)tabColor
{
    return [UIColor whiteColor];
}

- (UIColor *)tabBackgroundColor
{
    return SC_MAIN_COLOR;
}

- (UIFont *)titleFont
{
    return [UIFont fontWithName:@"HelveticaNeue-Bold" size:20.0f];
}

- (UIColor *)titleColor
{
    return [UIColor whiteColor];
}

#pragma mark - Tab Pager Delegate

- (void)tabPager:(SCUITabPagerViewController *)tabPager willTransitionToTabAtIndex:(NSInteger)index {
    NSLog(@"Will transition from tab %ld to %ld", [self selectedIndex], (long)index);
}

- (void)tabPager:(SCUITabPagerViewController *)tabPager didTransitionToTabAtIndex:(NSInteger)index {
    NSLog(@"Did transition to tab %ld", (long)index);
}

@end