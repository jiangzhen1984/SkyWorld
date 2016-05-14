//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"
#import "SCCoreDataManager.h"
#import "SendQuestion.h"
#import "SessionExtension.h"
#import "QuestionMessage.h"

@interface SamChatClient ()<NIMChatManagerDelegate>

@end

@implementation SamChatClient

@synthesize accountManager = _accountManager;
@synthesize searchManager = _searchManager;
@synthesize pushManager = _pushManager;

+ (instancetype)sharedClient
{
    static SamChatClient *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[SamChatClient alloc] init];
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

- (SAMCPushManager *)pushManager
{
    if (_pushManager == nil) {
        _pushManager = [[SAMCPushManager alloc] init];
    }
    return _pushManager;
}

#pragma mark - NIMChatManagerDelegate
- (void)onRecvMessages:(NSArray *)messages
{
    [messages enumerateObjectsUsingBlock:^(NIMMessage *message, NSUInteger idx, BOOL * _Nonnull stop) {
        NIMSession *session = message.session;
        if ((session.sessionType == NIMSessionTypeP2P) && (message.remoteExt != nil)) {
            // 根据接受到的消息扩展内容，对会话进行标记
            [self setExtOfSessionWithMessage:message];
            NSString *questionIdString = [message.remoteExt valueForKey:MESSAGE_QUESTIONS];
            if (questionIdString) {
                // 根据收到的消息中question id内容，将question插入到会话当中
                // TODO: delete?
                [self insertQuestionWitdIdsString:questionIdString toSession:message.session];
            }
        }
    }];
}

#pragma mark - Private
- (void)setExtOfSessionWithMessage:(NIMMessage *)message
{
    NSNumber *sessionType = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
    if (sessionType == nil) {
        return;
    }
    //TODO: should add conversation list change notification ?
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateChildObjectContextOfmainContext];
    if ([sessionType isEqualToNumber:MESSAGE_FROM_VIEW_SEARCH]) {
        [privateContext performBlockAndWait:^{
            [SessionExtension updateSession:message.session.sessionId
                                 serviceTag:YES
                     inManagedObjectContext:privateContext];
        }];
    }else if([sessionType isEqualToNumber:MESSAGE_FROM_VIEW_CHAT]) {
        [privateContext performBlockAndWait:^{
            [SessionExtension updateSession:message.session.sessionId
                                    chatTag:YES
                     inManagedObjectContext:privateContext];
        }];
    }else if([sessionType isEqualToNumber:MESSAGE_FROM_VIEW_VENDOR]) {
        [privateContext performBlockAndWait:^{
            [SessionExtension updateSession:message.session.sessionId
                                  searchTag:YES
                     inManagedObjectContext:privateContext];
        }];
    }
}

- (void)insertQuestionWitdIdsString:(NSString *)questionIdsString toSession:(NIMSession *)session
{
    NSArray *questionIds = [questionIdsString componentsSeparatedByString:@" "];
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        [QuestionMessage insertQuestionWithIds:questionIds
                                     sessionId:session.sessionId
                        inManagedObjectContext:privateContext];
    }];
//    NSArray *questionIds = [questionIdsString componentsSeparatedByString:@" "];
//    for (NSString *questionId in questionIds) {
//        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
//        SendQuestion *question = [SendQuestion sendQuestionWithId:questionId inManagedObjectContext:mainContext];
//        NSString *text = [NSString stringWithFormat:@"我的问题：%@", question.question];
//        NIMMessage *message = [[NIMMessage alloc] init];
//        message.text = text;
//        [[NIMSDK sharedSDK].conversationManager saveMessage:message
//                                                 forSession:session
//                                                 completion:NULL];
//    }
}

@end
