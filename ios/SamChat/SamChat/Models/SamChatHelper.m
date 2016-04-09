//
//  SamChatHelper.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatHelper.h"
#import "SamChatClient.h"

static SamChatHelper *sharedInstance = nil;

@implementation SamChatHelper
+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[SamChatHelper alloc] init];
    });
    return sharedInstance;
}

- (void)dealloc
{
    [SamChatClient sharedInstance].pushManager.delegate = nil;
}

- (id)init
{
    self = [super init];
    if (self) {
        [self initHelper];
    }
    return self;
}

- (void)initHelper
{
    [SamChatClient sharedInstance].pushManager.delegate = self;
}



#pragma mark - SamChatClientDelegate
- (void)didConnectionStateChanged:(EMConnectionState)aConnectionState
{
}


- (void)didAutoLoginWithError:(NSError *)aError
{
}


- (void)didLoginFromOtherDevice
{
}


- (void)didRemovedFromServer
{
}

- (void)asyncPush
{
    [[SamChatClient sharedInstance] asyncWaitingPush];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = nil;
        [[EMClient sharedClient] getPushOptionsFromServerWithError:&error];
    });
}

#pragma mark - SCPushDelegate
- (void)didReceivePushData:(NSDictionary *)pushData
{
    NSString *category = [pushData valueForKeyPath:SKYWORLD_HEADER_CATEGORY];
    NSDictionary *body = pushData[SKYWORLD_BODY];
    if([category isEqualToString:SKYWORLD_ANSWER]){
        DebugLog(@"######### receive answer push: %@", body);
        [self receivedNewAnswer:body];
    }else if([category isEqualToString:SKYWORLD_QUESTION]){
        DebugLog(@"######### receive question push: %@", body);
        [self receivedNewQuestion:body];
    }else if([category isEqualToString:SKYWORLD_EASEMOB]){
        DebugLog(@"######### receive easemob push: %@", body);
        [self receivedEasemobAccountInfo:body];
    }else{
        DebugLog(@"######### receive what? %@", pushData);
    }
}

#pragma mark - Receive New Answer
- (void)receivedNewAnswer:(NSDictionary *)answer
{
    NSManagedObjectContext *mainContext = [[SCCoreDataManager sharedInstance] mainObjectContext];
    [mainContext performBlockAndWait:^{
        ReceivedAnswer *receivedAnswer = [ReceivedAnswer receivedAnswerWithSkyWorldInfo:answer
                                                                 inManagedObjectContext:mainContext];
        [self.answerPushDelegate didReceiveNewAnswer:receivedAnswer];
    }];
}

#pragma mark - Receive New Question
- (void)receivedNewQuestion:(NSDictionary *)question
{
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        [ReceivedQuestion receivedQuestionWithSkyWorldInfo:question
                                    inManagedObjectContext:mainContext];
        [_mainVC setupUnreadMessageCount];
        [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_RECEIVED_NEW_QUESTION object:nil];
    }];
}

#pragma mark - Receive Easemob Account Info
- (void)receivedEasemobAccountInfo:(NSDictionary *)info
{
    [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobPushInfo:info];
}


@end
