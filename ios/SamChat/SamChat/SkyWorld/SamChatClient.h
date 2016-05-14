//
//  SamChatClient.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SAMCAccountManager.h"
#import "SAMCSearchManager.h"
#import "SAMCPushManager.h"

@interface SamChatClient : NSObject<NIMChatManagerDelegate>

@property (nonatomic, strong, readonly) SAMCAccountManager *accountManager;
@property (nonatomic, strong, readonly) SAMCSearchManager *searchManager;
@property (nonatomic, strong, readonly) SAMCPushManager *pushManager;

+ (instancetype)sharedClient;

@end
