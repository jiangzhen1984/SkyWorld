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
#import "SCCoreDataManager.h"

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
                 
                 NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
                 [mainContext performBlockAndWait:^{
                     [SendQuestion sendQuestionWithInfo:questionInfo
                                 inManagedObjectContext:mainContext];
                 }];
                 completion(nil);
             }
         }
     }failure:^(NSURLSessionDataTask *task, NSError *error){
         DDLogDebug(@"Error: %@", error);
         completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
     }];
}

@end
