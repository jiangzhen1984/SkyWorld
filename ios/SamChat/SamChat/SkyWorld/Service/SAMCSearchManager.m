//
//  SAMCSearchManager.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSearchManager.h"
#import "SAMCSkyWorldAPI.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "AFNetworking.h"
#import "SendQuestion.h"
#import "SAMCCoreDataManager.h"
#import "HotTopic.h"
#import "QuestionMessage.h"
#import "SAMCQuestionMessage.h"

@interface SAMCSearchManager ()

@end

@implementation SAMCSearchManager

- (void)queryTopicListWithOptType:(NSInteger)optType
                        topicType:(NSInteger)topicType
                     currentCount:(NSInteger)currentCount
                    updateTimePre:(NSTimeInterval)updateTimePre
                       completion:(void (^)(NSDictionary *response, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSString *urlString = [SAMCSkyWorldAPI urlQueryTopicListWithOptType:optType
                                                            topicType:topicType
                                                         currentCount:currentCount
                                                        updateTimePre:updateTimePre];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    completion(nil, [SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
                    completion(response, nil);
                }
            }else{
                completion(nil, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(nil, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

- (void)sendNewQuestion:(NSString *)question completion:(void (^)(NSError *))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSString *urlString = [SAMCSkyWorldAPI urlNewQuestionWithQuestion:question];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
    progress:^(NSProgress *downloadProgress){
    }success:^(NSURLSessionDataTask *task, id responseObject) {
         if([responseObject isKindOfClass:[NSDictionary class]]) {
             DDLogDebug(@"%@", responseObject);
             NSDictionary *response = responseObject;
             NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
             if(errorCode) {
                 completion([SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
             }else{
                 NSString *questionId = [response[SKYWORLD_QUESTION_ID] stringValue];
                 
                 NSDictionary *questionInfo = @{SEND_QUESTION_QUESTION:question,
                                                SEND_QUESTION_QUESTION_ID:questionId};
                 
                 NSManagedObjectContext *privateContext = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
                 [privateContext performBlock:^{
                     [SendQuestion sendQuestionWithInfo:questionInfo
                                 inManagedObjectContext:privateContext];
                     if ([privateContext hasChanges]) {
                         [privateContext save:NULL];
                         [[SAMCCoreDataManager sharedManager] saveContext];
                     }
                 }];
                 completion(nil);
             }
         }
     }failure:^(NSURLSessionDataTask *task, NSError *error){
         DDLogDebug(@"Error: %@", error);
         completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
     }];
}

#pragma mark - HotTopic Core Data 
- (NSArray *)hotTopicsWithType:(NSInteger)type
{
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    __block NSArray *topics = nil;
    [context performBlockAndWait:^{
        topics = [HotTopic hotTopicsWithType:type inManagedObjectContext:context];
    }];
    return topics;
}

- (void)updateHotTopicsWithArray:(NSArray<SAMCHotTopicCellModel*> *)topics
{
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [context performBlock:^{
        [HotTopic updateHotTopicsWithArray:topics inManagedObjectContext:context];
        if ([context hasChanges]) {
            [context save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
}

#pragma mark - QuestionMessage Core Data
- (void)insertQuestionWitdIdsString:(NSString *)questionIdsString toSession:(NIMSession *)session
{
    NSArray *questionIds = [questionIdsString componentsSeparatedByString:@" "];
    NSManagedObjectContext *privateContext = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        [QuestionMessage insertQuestionWithIds:questionIds
                                     sessionId:session.sessionId
                        inManagedObjectContext:privateContext];
        if ([privateContext hasChanges]) {
            [privateContext save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
}

- (NSArray<SAMCQuestionMessage*> *)messagesFromQuestionMessageWithTimeFrom:(NSNumber *)timefrom
                                                                     limit:(NSInteger)limit
                                                                   session:(NIMSession *)session
{
    __block NSArray<SAMCQuestionMessage *> *questionMessages = nil;
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [context performBlockAndWait:^{
        questionMessages = [QuestionMessage messagesFromQuestionMessageWithTimeFrom:timefrom
                                                                              limit:limit
                                                                            session:session
                                                             inManagedObjectContext:context];
    }];
    return questionMessages;
}

- (void)deleteQuestionMessageWithId:(NSString *)questionId
                          sessionId:(NSString *)sessionId
{
    NSManagedObjectContext *privateContext = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        [QuestionMessage deleteQuestionMessageWithId:questionId
                                           sessionId:sessionId
                              inManagedObjectContext:privateContext];
        if ([privateContext hasChanges]) {
            [privateContext save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
}

- (void)deleteAllQuestionMessagesWithSessionId:(NSString *)sessionId
{
    NSManagedObjectContext *privateContext = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        [QuestionMessage deleteAllQuestionMessagesWithSessionId:sessionId
                                         inManagedObjectContext:privateContext];
        if ([privateContext hasChanges]) {
            [privateContext save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
}


@end
