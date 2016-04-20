//
//  SCSearchManager.m
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSearchManager.h"
#import "HotTopic.h"
#import "SCSkyWorldErrorHelper.h"

@interface SCSearchManager ()

@property (nonatomic, assign) NSTimeInterval updateTimePre;
@property (nonatomic, assign) NSInteger currentCount;

@end

@implementation SCSearchManager

- (NSArray *)convertJsonArrayToTopicsArray:(NSArray *)topicsJson
{
    NSMutableArray *topics = [[NSMutableArray alloc] init];
    for (id topicDictionary in topicsJson) {
        if([topicDictionary isKindOfClass:[NSDictionary class]]){
            HotTopicCellModel *topic = [[HotTopicCellModel alloc] init];
            topic.type = [((NSDictionary *)topicDictionary)[SKYWORLD_TOPIC_TYPE] integerValue];
            topic.name = ((NSDictionary *)topicDictionary)[SKYWORLD_NAME];
            if((topic.name) && (topic.name.length > 0)){
                [topics addObject:topic];
            }
        }
    }
    return topics;
}

- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType reset:(BOOL)resetflag completion:(void (^)(BOOL success, NSArray *topics, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    if(resetflag){
        self.updateTimePre = 0;
        self.currentCount = 0;
    }
    NSString *urlString = [SCSkyWorldAPI urlQueryTopicListWithOptType:optType
                                                            topicType:topicType
                                                         currentCount:self.currentCount
                                                        updateTimePre:self.updateTimePre];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    completion(false, nil, [SCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
                    NSArray *topics = [self convertJsonArrayToTopicsArray:response[SKYWORLD_TOPICS]];
                    self.currentCount += [topics count];
                    self.updateTimePre = [response[SKYWORLD_QUERY_TIME] doubleValue];
                    completion(true, topics, nil);
                    DebugLog(@"topics: %@, %@", topics, response[SKYWORLD_QUERY_TIME]);
                }
            }else{
                completion(false, nil, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, nil, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

@end
