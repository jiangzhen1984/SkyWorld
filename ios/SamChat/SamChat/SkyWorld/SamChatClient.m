//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"

static dispatch_queue_t messageProcessQueue;

@interface SamChatClient ()<NIMChatManagerDelegate>

@end

@implementation SamChatClient

@synthesize accountManager = _accountManager;
@synthesize searchManager = _searchManager;
@synthesize producerManager = _producerManager;
@synthesize pushManager = _pushManager;
@synthesize sessionManager = _sessionManager;
@synthesize settingManager = _settingManager;

+ (instancetype)sharedClient
{
    static SamChatClient *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[SamChatClient alloc] init];
        messageProcessQueue = dispatch_queue_create("com.skyworld.samchat.messageprocess", DISPATCH_QUEUE_SERIAL);
    });
    return instance;
}

- (instancetype)init
{
    self = [super init];
    if(self){
        [[NIMSDK sharedSDK].chatManager addDelegate:self];
    }
    return self;
}

- (void)dealloc
{
    [[NIMSDK sharedSDK].chatManager removeDelegate:self];
}

#pragma mark - lazy load
- (SAMCAccountManager *)accountManager
{
    if (_accountManager == nil) {
        _accountManager = [[SAMCAccountManager alloc] init];
    }
    return _accountManager;
}

- (SAMCSearchManager *)searchManager
{
    if (_searchManager == nil) {
        _searchManager = [[SAMCSearchManager alloc] init];
    }
    return _searchManager;
}

- (SAMCProducerManager *)producerManager
{
    if (_producerManager) {
        _producerManager = [[SAMCProducerManager alloc] init];
    }
    return _producerManager;
}

- (SAMCPushManager *)pushManager
{
    if (_pushManager == nil) {
        _pushManager = [[SAMCPushManager alloc] init];
    }
    return _pushManager;
}

- (SAMCSessionManager *)sessionManager
{
    if (_sessionManager == nil) {
        _sessionManager = [[SAMCSessionManager alloc] init];
    }
    return _sessionManager;
}

- (SAMCSettingManager *)settingManager
{
    if (_settingManager == nil) {
        _settingManager = [[SAMCSettingManager alloc] init];
    }
    return _settingManager;
}


#pragma mark - NIMChatManagerDelegate
- (void)onRecvMessages:(NSArray *)messages
{
    __weak typeof(self) weakSelf = self;
    dispatch_async(messageProcessQueue, ^{
        for (NIMMessage *message in messages) {
            NIMSession *session = message.session;
            if ((session.sessionType == NIMSessionTypeP2P) && (message.remoteExt != nil)) {
                // 根据接受到的消息扩展内容，对会话进行标记
                [[SamChatClient sharedClient].sessionManager setExtOfSessionWithMessage:message];
                NSString *questionIdString = [message.remoteExt valueForKey:MESSAGE_QUESTIONS];
                if (questionIdString) {
                    // 根据收到的消息中question id内容，将question插入到会话当中
                    [weakSelf.searchManager insertQuestionWitdIdsString:questionIdString toSession:message.session];
                }
            }
        }
    });
}


@end
