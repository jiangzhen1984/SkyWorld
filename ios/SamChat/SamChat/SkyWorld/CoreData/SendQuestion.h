//
//  SendQuestion.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class LoginUserInformation;

NS_ASSUME_NONNULL_BEGIN

@interface SendQuestion : NSManagedObject

+ (SendQuestion *)sendQuestionWithInfo:(NSDictionary *)questionInfo inManagedObjectContext:(NSManagedObjectContext *)context;
+ (SendQuestion *)sendQuestionWithId:(NSString *)questionId inManagedObjectContext:(NSManagedObjectContext *)context;
+ (NSArray *)messagesFromQuestionWithTimeFrom:(NSNumber *)timefrom limit:(NSInteger)limit session:(NIMSession *)session inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "SendQuestion+CoreDataProperties.h"
