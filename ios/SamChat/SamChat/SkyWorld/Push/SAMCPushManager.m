//
//  SCPushManager.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCPushManager.h"
#import "SAMCSkyWorldAPIMacro.h"
#import "SCCoreDataManager.h"
#import "ReceivedQuestion.h"
#import "ContactUser.h"

@interface SAMCPushManager ()
{
    dispatch_queue_t _pushQueue;
}

@property (nonatomic, assign) BOOL pulling;

@end

@implementation SAMCPushManager
- (instancetype)init
{
    self = [super init];
    if(self){
        _pushQueue = dispatch_queue_create("SAMCPushDispatcher", NULL);
        //self.pulling = false;
    }
    return self;
}

- (void)setPulling:(BOOL)pulling
{
    if(pulling == false){
        [self pullFromServerOnce];
    }
    _pulling = pulling;
}

- (void)asyncWaitingPush
{
    DDLogDebug(@"main Thread: %@", [NSThread currentThread]);
    self.pulling = false;
    //    [NSTimer scheduledTimerWithTimeInterval:2
    //                                     target:self
    //                                   selector:@selector(pullFromServerOnce)
    //                                   userInfo:nil
    //                                    repeats:YES];
    
    
    //    __weak typeof(self) weakSelf = self;
    //    dispatch_async(dispatch_get_main_queue(), ^{
    //        while (true) {
    //            [weakSelf pullFromServerOnce];
    //            //[NSThread sleepForTimeInterval:20];
    //            //DebugLog(@"########################################");
    //            break;
    //        }
    
    ////        dispatch_async(dispatch_get_main_queue(), ^{
    ////            [weakSelf.dataArray addObjectsFromArray:messages];
    ////            [weakSelf.tableView reloadData];
    ////            [weakSelf.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:[weakSelf.dataArray count] - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    //        });
    //    });
    
}

- (void)pullFromServerOnce
{
    if(self.pulling) return;
    self.pulling = true;
    NSURL *url = [NSURL URLWithString:SKYWORLD_API_PUSH];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
#warning 11111111111111111111111111111
  //  [request setValue:[SAMCUserProfileManager sharedInstance].token forHTTPHeaderField:@"Authorization"];
    [request setValue:@"95892612343398478" forHTTPHeaderField:@"Authorization"];
    NSURLConnection *conn = [NSURLConnection connectionWithRequest:request delegate:self];
    [conn start];
    //    NSData *data=[NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
    //    DebugLog(@"-------------data:%@", data);
    //    NSDictionary *content = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];//转换数据格式
    //    DebugLog(@"-------------content:%@", content);
    DDLogDebug(@"pull Thread: %@", [NSThread currentThread]);
}


#pragma mark- NSURLConnection Data Delegate
-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    NSLog(@"接收到服务器的响应 %@:%@", [NSThread currentThread], response);
}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // 数据大时候可能多次调用
    NSLog(@"接收到服务器的数据 %@:%@",[NSThread currentThread], data);
    if(data) {
        NSDictionary *content = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
        [self didReceivePushData:content];
    }
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    NSLog(@"服务器的数据加载完毕%@", [NSThread currentThread]);
    self.pulling = false;
}

-(void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"请求错误 %@:%@", [NSThread currentThread],error);
    self.pulling = false;
}

#pragma mark - SCPushDelegate
- (void)didReceivePushData:(NSDictionary *)pushData
{
    NSString *category = [pushData valueForKeyPath:SKYWORLD_HEADER_CATEGORY];
    NSDictionary *body = pushData[SKYWORLD_BODY];
    if([category isEqualToString:SKYWORLD_ANSWER]){
        DDLogDebug(@"######### receive answer push: %@", body);
    }else if([category isEqualToString:SKYWORLD_QUESTION]){
        DDLogDebug(@"######### receive question push: %@", body);
        [self receivedNewQuestion:body];
    }else if([category isEqualToString:SKYWORLD_EASEMOB]){
        DDLogDebug(@"######### receive easemob push: %@", body);
        //[self receivedEasemobAccountInfo:body];
    }else{
        DDLogDebug(@"######### receive what? %@", pushData);
    }
}

- (void)receivedNewQuestion:(NSDictionary *)question
{
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        ReceivedQuestion *receivedQuestion = [ReceivedQuestion receivedQuestionWithSkyWorldInfo:question
                                                                         inManagedObjectContext:privateContext];
        if ([receivedQuestion.status isEqualToNumber:RECEIVED_QUESTION_VALID]) { // new question
            NSString *questionFrom = receivedQuestion.fromWho.username;
            NIMSession *session = [NIMSession session:questionFrom type:NIMSessionTypeP2P];
            NIMMessage *message = [[NIMMessage alloc] init];
            message.text = receivedQuestion.question;
            message.from = questionFrom;
            message.remoteExt = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_SEARCH};
            [[NIMSDK sharedSDK].conversationManager saveMessage:message forSession:session completion:^(NSError *error) {
                if (error) {
                    // TODO: error handler
                }
            }];
        }
    }];

//    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
//    [mainContext performBlockAndWait:^{
//        ReceivedQuestion *receivedQuestion = [ReceivedQuestion receivedQuestionWithSkyWorldInfo:question
//                                                                         inManagedObjectContext:mainContext];
//        if([receivedQuestion.status isEqualToNumber:RECEIVED_QUESTION_VALID]){ // new question
//            [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithUnreadQuestionCountAddOne];
//        }
//    }];
//    
//    NSString *questionFrom = [question valueForKeyPath:SKYWORLD_ASKER_USERNAME];
//    EMConversation *conversation = [[EMClient sharedClient].chatManager getConversation:questionFrom
//                                                                                   type:EMConversationTypeChat
//                                                                       createIfNotExist:YES];
//    
//    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:question[SKYWORLD_QUEST]];
//    NSString *questionTo = [[EMClient sharedClient] currentUsername];
//    
//    EMMessage *message = [[EMMessage alloc] initWithConversationID:questionFrom from:questionFrom to:questionTo body:body ext:nil];
//    message.chatType = EMChatTypeChat;
//    message.direction = EMMessageDirectionReceive;
//    message.timestamp = [question[SKYWORLD_DATETIME] longLongValue];
//    message.isReadAcked = YES;
//    message.isDeliverAcked = YES;
//    message.isRead = NO;
//    message.ext = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_SEARCH};
//    
//    [conversation insertMessage:message];
//    [self setExtOfConversation:conversation withKey:MESSAGE_FROM_VIEW_SEARCH];
//    [_mainVC setupUnreadMessageCount];
//    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_RECEIVED_NEW_QUESTION object:nil];
}

@end
