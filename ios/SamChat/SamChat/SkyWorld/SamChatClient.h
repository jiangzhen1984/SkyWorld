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
#import "SAMCProducerManager.h"
#import "SAMCPushManager.h"
#import "SAMCSessionManager.h"
#import "SAMCSettingManager.h"

@interface SamChatClient : NSObject<NIMChatManagerDelegate>

@property (nonatomic, strong, readonly) SAMCAccountManager *accountManager;
@property (nonatomic, strong, readonly) SAMCSearchManager *searchManager;
@property (nonatomic, strong, readonly) SAMCProducerManager *producerManager;
@property (nonatomic, strong, readonly) SAMCPushManager *pushManager;
@property (nonatomic, strong, readonly) SAMCSessionManager *sessionManager;
@property (nonatomic, strong, readonly) SAMCSettingManager *settingManager;

+ (instancetype)sharedClient;

@end
