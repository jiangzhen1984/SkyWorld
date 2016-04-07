//
//  AppDelegate+SamChat.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "AppDelegate+SamChat.h"
#import "LoginUserInformation.h"
#import "ChatDemoHelper.h"
#import "SCPushDispatcher.h"

@implementation AppDelegate (SamChat)


- (void)samchatApplication:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(loginStateChange:)
                                                 name:NOTIFICATION_LOGIN_STATE_CHANGE
                                               object:nil];
    
//    NSString *appkey = @"skyworld#skyworld";
//    NSString *apnsCertName = @"";
//    
//    [[EaseSDKHelper shareHelper] easemobApplication:application
//                      didFinishLaunchingWithOptions:launchOptions
//                                             appkey:appkey
//                                       apnsCertName:apnsCertName
//                                        otherConfig:@{kSDKConfigEnableConsoleLogger:[NSNumber numberWithBool:YES]}];
    
    BOOL isCurrentUserLoginOK = [[SCUserProfileManager sharedInstance] isCurrentUserLoginStatusOK];
    //isCurrentUserLoginOK = YES;
    if (isCurrentUserLoginOK){
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
    }
    else
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@NO];
    }
}

- (void)loginStateChange:(NSNotification *)notification
{
    BOOL loginSuccess = [notification.object boolValue];
    UIViewController *viewController = nil;
    if(loginSuccess) {
        DebugLog(@"SAMCHAT_Token:%@", [SCUserProfileManager sharedInstance].token);
        //-----------
//        UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
//        viewController = [storyBoard instantiateViewControllerWithIdentifier:@"HomeView"];
        //------------
        //加载申请通知的数据
        [[ApplyViewController shareController] loadDataSourceFromLocalDB];
//        if (self.homeController == nil) {
//            self.homeController = [[HomeViewController alloc] init];
//            viewController = [[UINavigationController alloc] initWithRootViewController:self.homeController];
//        }else{
//            viewController  = self.mainController.navigationController;
//        }
        
//        [ChatDemoHelper shareHelper].mainVC = self.mainController;
//        
//        [[ChatDemoHelper shareHelper] asyncGroupFromServer];
//        [[ChatDemoHelper shareHelper] asyncConversationFromDB];
//        [[ChatDemoHelper shareHelper] asyncPushOptions];
        //--------------
        
//        UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
//        viewController = [storyBoard instantiateViewControllerWithIdentifier:@"HomeView2"];
//        [ChatDemoHelper shareHelper].mainVC = self.homeController;
        
        if(self.homeController == nil) {
            //self.homeController = [[HomeViewController alloc] init];
            //viewController = [[UINavigationController alloc] initWithRootViewController:self.homeController];
            UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
            self.homeController = [storyBoard instantiateViewControllerWithIdentifier:@"HomeView"];
            viewController = [[UINavigationController alloc] initWithRootViewController:self.homeController];
        }else{
            viewController = self.homeController.navigationController;
        }
        [ChatDemoHelper shareHelper].mainVC = self.homeController;
        
        [[ChatDemoHelper shareHelper] asyncGroupFromServer];
        [[ChatDemoHelper shareHelper] asyncConversationFromDB];
        [[ChatDemoHelper shareHelper] asyncPushOptions];
        
        [[SCPushDispatcher sharedInstance] asyncWaitingPush];
        
    } else {
        UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"LoginCtrl" bundle:[NSBundle mainBundle]];
        viewController = [storyBoard instantiateViewControllerWithIdentifier:@"LoginNavController"];
    }
    self.window.rootViewController = viewController;
}


@end
