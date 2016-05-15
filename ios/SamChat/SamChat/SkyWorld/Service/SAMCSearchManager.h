//
//  SAMCSearchManager.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SAMCHotTopicCellModel.h"
#import "SAMCQuestionMessage.h"

@interface SAMCSearchManager : NSObject

- (void)queryTopicListWithOptType:(NSInteger)optType
                        topicType:(NSInteger)topicType
                     currentCount:(NSInteger)currentCount
                    updateTimePre:(NSTimeInterval)updateTimePre
                       completion:(void (^)(NSDictionary *response, NSError *error))completion;

- (void)sendNewQuestion:(NSString *)question
             completion:(void (^)(NSError *error))completion;

- (NSArray *)hotTopicsWithType:(NSInteger)type;

- (void)updateHotTopicsWithArray:(NSArray<SAMCHotTopicCellModel*> *)topics;

- (void)insertQuestionWitdIdsString:(NSString *)questionIdsString
                          toSession:(NIMSession *)session;

- (NSArray<SAMCQuestionMessage*> *)messagesFromQuestionMessageWithTimeFrom:(NSNumber *)timefrom
                                                                     limit:(NSInteger)limit
                                                                   session:(NIMSession *)session;

- (void)deleteQuestionMessageWithId:(NSString *)questionId
                          sessionId:(NSString *)sessionId;

- (void)deleteAllQuestionMessagesWithSessionId:(NSString *)sessionId;

@end
