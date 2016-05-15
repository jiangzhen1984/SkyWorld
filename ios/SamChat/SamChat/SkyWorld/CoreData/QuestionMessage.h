//
//  QuestionMessage.h
//  SamChat
//
//  Created by HJ on 5/13/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

NS_ASSUME_NONNULL_BEGIN

@interface QuestionMessage : NSManagedObject

+ (void)insertQuestionWithIds:(NSArray *)questionIds
                    sessionId:(NSString *)sessionId
       inManagedObjectContext:(NSManagedObjectContext *)context;

+ (NSArray *)messagesFromQuestionMessageWithTimeFrom:(NSNumber *)timefrom
                                               limit:(NSInteger)limit
                                             session:(NIMSession *)session
                              inManagedObjectContext:(NSManagedObjectContext *)context;

+ (void)deleteQuestionMessageWithId:(NSString *)questionId sessionId:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)deleteAllQuestionMessagesWithSessionId:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context;;

@end

NS_ASSUME_NONNULL_END

#import "QuestionMessage+CoreDataProperties.h"
