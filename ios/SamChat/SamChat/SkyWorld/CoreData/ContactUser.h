//
//  ContactUser.h
//  
//
//  Created by HJ on 4/4/16.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "LoginUserInformation.h"

@class ReceivedQuestion;



NS_ASSUME_NONNULL_BEGIN

@interface ContactUser : NSManagedObject

+ (ContactUser *)contactUserWithSkyWorldInfo:(NSDictionary *)userDictionary inManagedObjectContext:(NSManagedObjectContext *)context;
+ (ContactUser *)contactUserWithLoginUserInformation:(LoginUserInformation *)loginUserInformation inManagedObjectContext:(NSManagedObjectContext *)context;
+ (ContactUser *)contactUserWithUsername:(NSString *)username inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "ContactUser+CoreDataProperties.h"
