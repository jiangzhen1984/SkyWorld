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

@end

NS_ASSUME_NONNULL_END

#import "SendQuestion+CoreDataProperties.h"
