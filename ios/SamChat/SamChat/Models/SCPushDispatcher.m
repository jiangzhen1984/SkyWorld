//
//  SCPushDispatcher.m
//  SamChat
//
//  Created by HJ on 4/2/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCPushDispatcher.h"
#import "SCCoreDataManager.h"
#import "ReceivedAnswer.h"
#import "ReceivedQuestion.h"

static SCPushDispatcher *sharedInstance = nil;

@interface SCPushDispatcher ()
{
    dispatch_queue_t _pushQueue;
//    BOOL pulling;
}

@property (nonatomic, assign) BOOL pulling;

@end

@implementation SCPushDispatcher


+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
    if(self){
        _pushQueue = dispatch_queue_create("SCPushDispatcher", NULL);
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
    DebugLog(@"main Thread: %@", [NSThread currentThread]);
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
    [request setValue:[SCUserProfileManager sharedInstance].token forHTTPHeaderField:@"Authorization"];
    NSURLConnection *conn = [NSURLConnection connectionWithRequest:request delegate:self];
    [conn start];
//    NSData *data=[NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
//    DebugLog(@"-------------data:%@", data);
//    NSDictionary *content = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];//转换数据格式
//    DebugLog(@"-------------content:%@", content);
    DebugLog(@"pull Thread: %@", [NSThread currentThread]);
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
        NSString *category = [content valueForKeyPath:SKYWORLD_HEADER_CATEGORY];
        NSDictionary *body = content[SKYWORLD_BODY];
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
            DebugLog(@"######### receive what? %@", content);
        }
        //NSLog(@"接收到服务器的数据: %@", content);
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

#pragma mark - Receive New Answer
- (void)receivedNewAnswer:(NSDictionary *)answer
{
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
    [privateContext performBlockAndWait:^{
        ReceivedAnswer *receivedAnswer = [ReceivedAnswer receivedAnswerWithSkyWorldInfo:answer
                                    inManagedObjectContext:privateContext];
        NSManagedObjectID *objId = [receivedAnswer objectID];
        dispatch_async(dispatch_get_main_queue(), ^{
            ReceivedAnswer *answer = [[[SCCoreDataManager sharedInstance] managedObjectContext] existingObjectWithID:objId
                                                                                      error:NULL];
            if(answer){
                [self.answerPushDelegate didReceiveNewAnswer:answer];
            }
        });
    }];
}

#pragma mark - Receive New Question
- (void)receivedNewQuestion:(NSDictionary *)question
{
//    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
//    [privateContext performBlockAndWait:^{
//        [ReceivedQuestion receivedQuestionWithSkyWorldInfo:question
//                                    inManagedObjectContext:privateContext];
//        dispatch_async(dispatch_get_main_queue(), ^{
//            [self.questionPushDelegate didReceiveNewQuestion:nil];
//        });
//    }];
    
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].managedObjectContext;
    [mainContext performBlockAndWait:^{
        [ReceivedQuestion receivedQuestionWithSkyWorldInfo:question
                                    inManagedObjectContext:mainContext];
    }];
}

#pragma mark - Receive Easemob Account Info
- (void)receivedEasemobAccountInfo:(NSDictionary *)info
{
    [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobPushInfo:info];
}

@end
