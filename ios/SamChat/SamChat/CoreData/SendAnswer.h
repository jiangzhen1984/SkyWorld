//
//  SendAnswer.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "ISCChatMessageModel.h"

@class LoginUserInformation;

NS_ASSUME_NONNULL_BEGIN

@interface SendAnswer : NSManagedObject <ISCChatMessageModel>
@property (nonatomic) CGFloat i_cellHeight;
+ (SendAnswer *)sendAnswerWithInfo:(NSDictionary *)answerInfo inManagedObjectContext:(NSManagedObjectContext *)context;
+ (NSArray *)loadCurrentUsersAnswersOfQuestionID:(NSString *)question_id inManagedObjectContext:(NSManagedObjectContext *)context;
- (void)updateStatus:(NSNumber *)status inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "SendAnswer+CoreDataProperties.h"
