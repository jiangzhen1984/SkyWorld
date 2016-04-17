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

#import "SCCrashCatcher.h"

@interface AppDelegate ()

@end


@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    _connectionState = EMConnectionConnected;
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    
    //NSSetUncaughtExceptionHandler(&uncaughtExceptionHandler);
    
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
                 otherConfig:@{kSDKConfigEnableConsoleLogger:[NSNumber numberWithBool:NO]}];

    DebugLog(@"%@",NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0]);
    [self samchatApplication:application didFinishLaunchingWithOptions:launchOptions];
    
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    DebugLog(@"Remote Notification");
    if (_homeController) {
        [_homeController jumpToChatList];
    }
    
    NSError *parseError = nil;
    NSData  *jsonData = [NSJSONSerialization dataWithJSONObject:userInfo
                                                        options:NSJSONWritingPrettyPrinted error:&parseError];
    NSString *str =  [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"apns.content", @"Apns content")
                                                    message:str
                                                   delegate:nil
                                          cancelButtonTitle:NSLocalizedString(@"ok", @"OK")
                                          otherButtonTitles:nil];
    [alert show];
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    DebugLog(@"Local Notification");
    if (_homeController) {
        [_homeController didReceiveLocalNotification:notification];
    }
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
// UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"test"
//                                                 message:[NSString stringWithFormat:@"%@", userInfo]
//                                                delegate:nil
//                                       cancelButtonTitle:@"OK"
//                                       otherButtonTitles:nil, nil];
//    [alert show];
    // for test
    NSString *info = [NSString stringWithFormat:@"%@", userInfo];
    [info writeToFile:[NSString stringWithFormat:@"%@/Documents/info.log",NSHomeDirectory()] atomically:YES encoding:NSUTF8StringEncoding error:nil];
    completionHandler(UIBackgroundFetchResultFailed);
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
#warning add context saving
    [[SCCoreDataManager sharedInstance] saveContext];
}

@end
