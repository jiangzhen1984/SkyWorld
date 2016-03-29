//
//  LoginUserInformation.h
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

#define ENTITY_LOGIN_USER_INFORMATION           @"LoginUserInformation"
#define LOGIN_USER_INFORMATION_USERNAME         @"username"
#define LOGIN_USER_INFORMATION_EASEMOB_STATUS   @"easemob_status"

NS_ASSUME_NONNULL_BEGIN

@interface LoginUserInformation : NSManagedObject

//+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response inManagedObjectContext:(NSManagedObjectContext *)context;
+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response;
+ (void)saveContext;

+ (LoginUserInformation *)loginUserInformationForUser:(NSString *)username;


@end

NS_ASSUME_NONNULL_END

#import "LoginUserInformation+CoreDataProperties.h"
