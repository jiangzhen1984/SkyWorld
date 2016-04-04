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

#define ENTITY_CONTACT_USER             @"ContactUser"
#define CONTACT_USER_UNIQUE_ID          @"unique_id"


NS_ASSUME_NONNULL_BEGIN

@interface ContactUser : NSManagedObject

+ (ContactUser *)contactUserWithSkyWorldInfo:(NSDictionary *)userDictionary inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "ContactUser+CoreDataProperties.h"
