//
//  AppDelegate+SamChat.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "AppDelegate+SamChat.h"
#import "LoginUserInformation.h"

@implementation AppDelegate (SamChat)


- (void)samchatApplication:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(loginStateChange:)
                                                 name:NOTIFICATION_LOGIN_STATE_CHANGE
                                               object:nil];
    
    NSString *appkey = @"skyworld#skyworld";
    NSString *apnsCertName = @"";
    
    [[EaseSDKHelper shareHelper] easemobApplication:application
                      didFinishLaunchingWithOptions:launchOptions
                                             appkey:appkey
                                       apnsCertName:apnsCertName
                                        otherConfig:@{kSDKConfigEnableConsoleLogger:[NSNumber numberWithBool:YES]}];
    
    BOOL isCurrentUserLoginOK = [[SCUserProfileManager sharedInstance] isCurrentUserLoginStatusOK];
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
        UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
        viewController = [storyBoard instantiateViewControllerWithIdentifier:@"HomeView"];
    } else {
        UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"LoginCtrl" bundle:[NSBundle mainBundle]];
        viewController = [storyBoard instantiateViewControllerWithIdentifier:@"LoginNavController"];
    }
    self.window.rootViewController = viewController;
}


@end
