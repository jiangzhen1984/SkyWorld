//
//  ReceivedQuestion.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ContactUser;

NS_ASSUME_NONNULL_BEGIN

@interface ReceivedQuestion : NSManagedObject

+ (ReceivedQuestion *)receivedQuestionWithSkyWorldInfo:(NSDictionary *)questionDictionary inManagedObjectContext:(NSManagedObjectContext *)context;
//+ (ReceivedQuestion *)receivedQuestionWithQuestionID:(NSString *)questionId inManagedObjectContext:(NSManagedObjectContext *)context;

//+ (NSArray *)receivedQuestionIDsFrom:(NSString *)username inManagedObjectContext:(NSManagedObjectContext *)context;
+ (NSArray *)unresponsedQuestionIdsFrom:(NSString *)username markResponsed:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "ReceivedQuestion+CoreDataProperties.h"
