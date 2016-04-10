//
//  ContactUser.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ReceivedAnswer, ReceivedQuestion;



NS_ASSUME_NONNULL_BEGIN

@interface ContactUser : NSManagedObject

+ (ContactUser *)contactUserWithSkyWorldInfo:(NSDictionary *)userDictionary inManagedObjectContext:(NSManagedObjectContext *)context;
+ (ContactUser *)contactUserWithLoginUserInformation:(LoginUserInformation *)loginUserInformation inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "ContactUser+CoreDataProperties.h"
