//
//  ReceivedAnswer.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "ISCTableCellModel.h"

@class ContactUser;

#define ENTITY_RECEIVED_ANSWER          @"ReceivedAnswer"

NS_ASSUME_NONNULL_BEGIN

@interface ReceivedAnswer : NSManagedObject <ISCTableCellModel>

+ (ReceivedAnswer *)receivedAnswerWithSkyWorldInfo:(NSDictionary *)answerDictionary inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "ReceivedAnswer+CoreDataProperties.h"
