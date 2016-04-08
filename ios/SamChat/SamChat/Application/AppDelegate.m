//
//  AppDelegate.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "AppDelegate.h"
#import "MainViewController.h"
#import "LoginViewController.h"

#import "AppDelegate+EaseMob.h"
#import "AppDelegate+SamChat.h"


@interface AppDelegate ()

@end


@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    _connectionState = EMConnectionConnected;
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    
//    if ([UIDevice currentDevice].systemVersion.floatValue >= 7.0) {
//        [[UINavigationBar appearance] setBarTintColor:RGBACOLOR(30, 167, 252, 1)];
//        [[UINavigationBar appearance] setTitleTextAttributes:
//         [NSDictionary dictionaryWithObjectsAndKeys:RGBACOLOR(245, 245, 245, 1), NSForegroundColorAttributeName, [UIFont fontWithName:@ "HelveticaNeue-CondensedBlack" size:21.0], NSFontAttributeName, nil]];
//    }
    
#warning 初始化环信SDK，详细内容在AppDelegate+EaseMob.m 文件中
#warning SDK注册 APNS文件的名字, 需要与后台上传证书时的名字一一对应
    NSString *apnsCertName = nil;

//    apnsCertName = @"chatdemoui";

    [self easemobApplication:application
didFinishLaunchingWithOptions:launchOptions
                      appkey:@"skyworld#skyworld"
                apnsCertName:apnsCertName
                 otherConfig:@{kSDKConfigEnableConsoleLogger:[NSNumber numberWithBool:YES]}];

    DebugLog(@"%@",NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0]);
    [self samchatApplication:application didFinishLaunchingWithOptions:launchOptions];
    
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    if (_homeController) {
        [_homeController jumpToChatList];
    }
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    if (_homeController) {
        [_homeController didReceiveLocalNotification:notification];
    }
}

- (void)applicationWillTerminate:(UIApplication *)application
{
#warning add context saving
    [[SCCoreDataManager sharedInstance] saveContext];
}

@end
