//
//  ReceivedQuestion.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "ISCChatMessageModel.h"
#import "ISCTableCellModel.h"

@class ContactUser;

NS_ASSUME_NONNULL_BEGIN

@interface ReceivedQuestion : NSManagedObject  <ISCChatMessageModel, ISCTableCellModel>
@property (nonatomic) CGFloat i_cellHeight;
+ (ReceivedQuestion *)receivedQuestionWithSkyWorldInfo:(NSDictionary *)questionDictionary inManagedObjectContext:(NSManagedObjectContext *)context;
+ (ReceivedQuestion *)receivedQuestionWithQuestionID:(NSString *)questionId inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "ReceivedQuestion+CoreDataProperties.h"
