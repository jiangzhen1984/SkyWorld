//
//  SAMCMainViewController.m
//  SamChat
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCMainViewController.h"
#import "SAMCAppDelegate.h"
#import "NTESSessionListViewController.h"
#import "NTESContactViewController.h"
#import "NIMSDK.h"
#import "UIImage+NTESColor.h"
#import "NTESCustomNotificationDB.h"
#import "NTESNotificationCenter.h"
#import "NTESNavigationHandler.h"
#import "NTESNavigationAnimator.h"
#import "NTESBundleSetting.h"

#define TabbarVC    @"vc"
#define TabbarTitle @"title"
#define TabbarImage @"image"
#define TabbarSelectedImage @"selectedImage"
#define TabbarItemBadgeValue @"badgeValue"
#define TabBarCount 5

typedef NS_ENUM(NSInteger, SAMCMainTabType) {
    SAMCMainTabTypeSearch,
    SAMCMainTabTypeChat,
    SAMCMainTabTypePublic,
    SAMCMainTabTypeService,
    SAMCMainTabTypeSetting
};

@interface SAMCMainViewController ()<NIMSystemNotificationManagerDelegate,NIMConversationManagerDelegate>

@property (nonatomic,strong) NSArray *navigationHandlers;

@property (nonatomic,strong) NTESNavigationAnimator *animator;

@property (nonatomic,assign) NSInteger sessionUnreadCount;

@property (nonatomic,assign) NSInteger systemUnreadCount;

@property (nonatomic,assign) NSInteger customSystemUnreadCount;

@property (nonatomic,copy)  NSDictionary *configs;

@end

@implementation SAMCMainViewController

+ (instancetype)instance{
    SAMCAppDelegate *delegete = (SAMCAppDelegate *)[UIApplication sharedApplication].delegate;
    UIViewController *vc = delegete.window.rootViewController;
    if ([vc isKindOfClass:[SAMCMainViewController class]]) {
        return (SAMCMainViewController *)vc;
    }else{
        return nil;
    }
}


- (void)viewDidLoad {
    [super viewDidLoad];
    [self setUpSubNav];
    [[NIMSDK sharedSDK].systemNotificationManager addDelegate:self];
    [[NIMSDK sharedSDK].conversationManager addDelegate:self];
    extern NSString *NTESCustomNotificationCountChanged;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onCustomNotifyChanged:) name:NTESCustomNotificationCountChanged object:nil];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self setUpStatusBar];
}

-(void)viewWillLayoutSubviews
{
    self.view.frame = [UIScreen mainScreen].bounds;
}


- (void)dealloc{
    [[NIMSDK sharedSDK].systemNotificationManager removeDelegate:self];
    [[NIMSDK sharedSDK].conversationManager removeDelegate:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray*)tabbars{
    self.sessionUnreadCount  = [NIMSDK sharedSDK].conversationManager.allUnreadCount;
    self.systemUnreadCount   = [NIMSDK sharedSDK].systemNotificationManager.allUnreadCount;
    self.customSystemUnreadCount = [[NTESCustomNotificationDB sharedInstance] unreadCount];
    NSMutableArray *items = [[NSMutableArray alloc] init];
    for (NSInteger tabbar = 0; tabbar < TabBarCount; tabbar++) {
        [items addObject:@(tabbar)];
    }
    return items;
}


- (void)setUpSubNav{
    NSMutableArray *handleArray = [[NSMutableArray alloc] init];
    NSMutableArray *vcArray = [[NSMutableArray alloc] init];
    [self.tabbars enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        NSDictionary * item =[self vcInfoForTabType:[obj integerValue]];
        NSString *vcName = item[TabbarVC];
        NSString *title  = item[TabbarTitle];
        NSString *imageName = item[TabbarImage];
        NSString *imageSelected = item[TabbarSelectedImage];
        Class clazz = NSClassFromString(vcName);
        UIViewController *vc = [[clazz alloc] initWithNibName:nil bundle:nil];
        vc.hidesBottomBarWhenPushed = NO;
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:vc];
        nav.tabBarItem = [[UITabBarItem alloc] initWithTitle:title
                                                       image:[UIImage imageNamed:imageName]
                                               selectedImage:[UIImage imageNamed:imageSelected]];
        nav.tabBarItem.tag = idx;
//        NSInteger badge = [item[TabbarItemBadgeValue] integerValue];
//        if (badge) {
//            nav.tabBarItem.badgeValue = [NSString stringWithFormat:@"%zd",badge];
//        }
        NTESNavigationHandler *handler = [[NTESNavigationHandler alloc] initWithNavigationController:nav];
        nav.delegate = handler;
        
        [vcArray addObject:nav];
        [handleArray addObject:handler];
    }];
    self.viewControllers = [NSArray arrayWithArray:vcArray];
    self.navigationHandlers = [NSArray arrayWithArray:handleArray];
    [self refreshSessionBadge];
    [self refreshContactBadge];
    [self refreshSettingBadge];
}


- (void)setUpStatusBar{
    UIStatusBarStyle style = UIStatusBarStyleDefault;
    [[UIApplication sharedApplication] setStatusBarStyle:style
                                                animated:NO];
}


#pragma mark - NIMConversationManagerDelegate
- (void)didAddRecentSession:(NIMRecentSession *)recentSession
           totalUnreadCount:(NSInteger)totalUnreadCount{
    self.sessionUnreadCount = totalUnreadCount;
    [self refreshSessionBadge];
}


- (void)didUpdateRecentSession:(NIMRecentSession *)recentSession
              totalUnreadCount:(NSInteger)totalUnreadCount{
    self.sessionUnreadCount = totalUnreadCount;
    [self refreshSessionBadge];
}


- (void)didRemoveRecentSession:(NIMRecentSession *)recentSession totalUnreadCount:(NSInteger)totalUnreadCount{
    self.sessionUnreadCount = totalUnreadCount;
    [self refreshSessionBadge];
}

- (void)messagesDeletedInSession:(NIMSession *)session{
    self.sessionUnreadCount = [NIMSDK sharedSDK].conversationManager.allUnreadCount;
    [self refreshSessionBadge];
}

- (void)allMessagesDeleted{
    self.sessionUnreadCount = 0;
    [self refreshSessionBadge];
}

#pragma mark - NIMSystemNotificationManagerDelegate
- (void)onSystemNotificationCountChanged:(NSInteger)unreadCount
{
    self.systemUnreadCount = unreadCount;
    [self refreshContactBadge];
}

#pragma mark - Notification
- (void)onCustomNotifyChanged:(NSNotification *)notification
{
    NTESCustomNotificationDB *db = [NTESCustomNotificationDB sharedInstance];
    self.customSystemUnreadCount = db.unreadCount;
    [self refreshSettingBadge];
}

- (void)refreshSessionBadge{
    __block NSInteger searchUnreadCount = 0;
    __block NSInteger chatUnreadCount = 0;
    __block NSInteger serviceUnreadCount = 0;
    NSArray *recentSessions = [[NIMSDK sharedSDK].conversationManager.allRecentSessions copy];
    for (NIMRecentSession *recent in recentSessions) {
        if (recent.session.sessionType != NIMSessionTypeP2P) {
            chatUnreadCount += recent.unreadCount;
        }else{
            if (recent.unreadCount == 0) {
                continue;
            }
            NSArray *messages = [[NIMSDK sharedSDK].conversationManager messagesInSession:recent.session
                                                                                  message:nil
                                                                                    limit:recent.unreadCount];
//            for (NIMMessage *message in messages) {
//                NSString *messageFromView = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
//                if ([messageFromView isEqualToString:MESSAGE_FROM_VIEW_VENDOR]) {
//                    searchUnreadCount ++;
//                }else if([messageFromView isEqualToString:MESSAGE_FROM_VIEW_CHAT]) {
//                    chatUnreadCount ++;
//                }else if([messageFromView isEqualToString:MESSAGE_FROM_VIEW_SEARCH]) {
//                    serviceUnreadCount ++;
//                }
//            }
            [messages enumerateObjectsUsingBlock:^(NIMMessage *message, NSUInteger idx, BOOL * _Nonnull stop) {
                NSNumber *messageFromView = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
                if ([messageFromView isEqualToNumber:MESSAGE_FROM_VIEW_VENDOR]) {
                    searchUnreadCount ++;
                }else if([messageFromView isEqualToNumber:MESSAGE_FROM_VIEW_CHAT]) {
                    chatUnreadCount ++;
                }else if([messageFromView isEqualToNumber:MESSAGE_FROM_VIEW_SEARCH]) {
                    serviceUnreadCount ++;
                }else{
                    chatUnreadCount ++; // default set to type normal chat
                }
            }];
        }

    }
    UINavigationController *nav = self.viewControllers[SAMCMainTabTypeSearch];
    nav.tabBarItem.badgeValue = searchUnreadCount ? @(searchUnreadCount).stringValue : nil;
    
    nav = self.viewControllers[SAMCMainTabTypeChat];
    nav.tabBarItem.badgeValue = chatUnreadCount ? @(chatUnreadCount).stringValue : nil;
    
    nav = self.viewControllers[SAMCMainTabTypeService];
    nav.tabBarItem.badgeValue = serviceUnreadCount ? @(serviceUnreadCount).stringValue : nil;
    //UINavigationController *nav = self.viewControllers[SAMCMainTabTypeChat];
    //nav.tabBarItem.badgeValue = self.sessionUnreadCount ? @(self.sessionUnreadCount).stringValue : nil;
}

- (void)refreshContactBadge{
//    UINavigationController *nav = self.viewControllers[NTESMainTabTypeContact];
//    NSInteger badge = self.systemUnreadCount;
//    nav.tabBarItem.badgeValue = badge ? @(badge).stringValue : nil;
}

- (void)refreshSettingBadge{
    UINavigationController *nav = self.viewControllers[SAMCMainTabTypeSetting];
    NSInteger badge = self.customSystemUnreadCount;
    nav.tabBarItem.badgeValue = badge ? @(badge).stringValue : nil;
}


- (UIStatusBarStyle)preferredStatusBarStyle {
    return UIStatusBarStyleDefault;
}

#pragma mark - NTESNavigationGestureHandlerDataSource
- (UINavigationController *)navigationController
{
    return self.selectedViewController;
}


#pragma mark - Rotate

- (BOOL)shouldAutorotate{
    BOOL enableRotate = [NTESBundleSetting sharedConfig].enableRotate;
    return enableRotate ? [self.selectedViewController shouldAutorotate] : NO;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations{
    BOOL enableRotate = [NTESBundleSetting sharedConfig].enableRotate;
    return enableRotate ? [self.selectedViewController supportedInterfaceOrientations] : UIInterfaceOrientationMaskPortrait;
}


#pragma mark - VC
- (NSDictionary *)vcInfoForTabType:(SAMCMainTabType)type{
    
    if (_configs == nil)
    {
        _configs = @{
                     @(SAMCMainTabTypeSearch) : @{
                             TabbarVC: @"SAMCSearchListViewController",
                             TabbarTitle: @"搜索",
                             TabbarImage: @"icon_message_normal",
                             TabbarSelectedImage: @"icon_message_pressed",
                             TabbarItemBadgeValue: @(self.sessionUnreadCount)
                             },
                     @(SAMCMainTabTypeChat) : @{
                             TabbarVC: @"SAMCChatListViewController",
                             TabbarTitle: @"聊天",
                             TabbarImage: @"icon_message_normal",
                             TabbarSelectedImage: @"icon_message_pressed",
                             TabbarItemBadgeValue: @(self.sessionUnreadCount)
                             },
                     @(SAMCMainTabTypePublic) : @{
                             TabbarVC: @"NTESContactViewController",
                             TabbarTitle: @"公众",
                             TabbarImage: @"icon_message_normal",
                             TabbarSelectedImage: @"icon_message_pressed",
                             TabbarItemBadgeValue: @(self.sessionUnreadCount)
                             },
                     @(SAMCMainTabTypeService) : @{
                             TabbarVC: @"SAMCServiceListViewController",
                             TabbarTitle: @"服务",
                             TabbarImage: @"icon_message_normal",
                             TabbarSelectedImage: @"icon_message_pressed",
                             TabbarItemBadgeValue: @(self.sessionUnreadCount)
                             },
                     @(SAMCMainTabTypeSetting) : @{
                             TabbarVC: @"NTESSettingViewController",
                             TabbarTitle: @"设置",
                             TabbarImage: @"icon_setting_normal",
                             TabbarSelectedImage: @"icon_setting_pressed",
                             TabbarItemBadgeValue: @(self.customSystemUnreadCount)
                             }
                     };
    }
    return _configs[@(type)];
}

@end
