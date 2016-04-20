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
        ReceivedQuestion *receivedQuestion = [ReceivedQuestion receivedQuestionWithSkyWorldInfo:question
                                    inManagedObjectContext:mainContext];
        if([receivedQuestion.status isEqualToNumber:RECEIVED_QUESTION_VALID]){ // new question
            [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithUnreadQuestionCountAddOne];
            [_mainVC setupUnreadMessageCount];
            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_RECEIVED_NEW_QUESTION object:nil];
            [self addNewQuestionLocalNotification:receivedQuestion];
        }
    }];
    
    NSString *questionFrom = [question valueForKeyPath:SKYWORLD_ASKER_USERNAME];
    EMConversation *conversation = [[EMClient sharedClient].chatManager getConversation:questionFrom
                                                                                   type:EMConversationTypeChat
                                                                       createIfNotExist:YES];
    
    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:question[SKYWORLD_QUEST]];
    NSString *questionTo = [[EMClient sharedClient] currentUsername];
    
    EMMessage *message = [[EMMessage alloc] initWithConversationID:questionFrom from:questionFrom to:questionTo body:body ext:nil];
    message.chatType = EMChatTypeChat;
    message.direction = EMMessageDirectionReceive;
    message.timestamp = [question[SKYWORLD_DATETIME] longLongValue];
    message.ext = @{@"from":@"question"};
    //message.isRead = false;
    
    [conversation insertMessage:message];
    NSMutableDictionary *convertsationExtDic = [[NSMutableDictionary alloc] initWithDictionary:conversation.ext];
    if(convertsationExtDic == nil){
        convertsationExtDic = [[NSMutableDictionary alloc] initWithDictionary:@{CONVERSATION_TYPE_KEY_QUESTION:[NSNumber numberWithBool:NO],
                                                                                CONVERSATION_TYPE_KEY_ANSWER:[NSNumber numberWithBool:NO],
                                                                                CONVERSATION_TYPE_KEY_NORMAL:[NSNumber numberWithBool:NO]}];
    }
    [convertsationExtDic setValue:[NSNumber numberWithBool:YES] forKey:CONVERSATION_TYPE_KEY_QUESTION];
    conversation.ext = convertsationExtDic;
    [conversation updateConversationExtToDB];
}

#pragma mark - Receive Easemob Account Info
- (void)receivedEasemobAccountInfo:(NSDictionary *)info
{
    [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobPushInfo:info];
}

- (void)addNewQuestionLocalNotification:(ReceivedQuestion *)receivedQuestion
{
    UILocalNotification *notification = [[UILocalNotification alloc] init];
    notification.fireDate = [NSDate date];
    notification.alertBody = [NSString stringWithFormat:@"收到新的问题:%@(%@)",receivedQuestion.question, receivedQuestion.fromWho.username];
    notification.alertAction = @"打开";
    notification.timeZone = [NSTimeZone defaultTimeZone];
    notification.soundName=UILocalNotificationDefaultSoundName;
    notification.userInfo=@{LOCAL_NOTIFICATION_TYPE:LOCAL_NOTIFICATION_TYPE_NEW_QUESTION,
                            LOCAL_NOTIFICATION_QUESTION_ID:receivedQuestion.question_id};
    [[UIApplication sharedApplication] scheduleLocalNotification:notification];
}

@end
