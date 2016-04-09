//
//  HomeViewController.h
//  SamChat
//
//  Created by HJ on 3/30/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SCUITabPagerViewController.h"
#import "EMClientDelegate.h"
#import "EMMessage.h"

@interface HomeViewController : SCUITabPagerViewController
{
    EMConnectionState _connectionState;
}

- (void)jumpToChatList;

- (void)setupUntreatedApplyCount;

- (void)setupUnreadMessageCount;

- (void)networkChanged:(EMConnectionState)connectionState;

- (void)didReceiveLocalNotification:(UILocalNotification *)notification;

- (void)playSoundAndVibration;

- (void)showNotificationWithMessage:(EMMessage *)message;

@end
