//
//  LoginUserInformation.h
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

#define ENTITY_LOGIN_USER_INFORMATION       @"LoginUserInformation"
#define LOGIN_USER_INFORMATION_USERNAME     @"username"

NS_ASSUME_NONNULL_BEGIN

@interface LoginUserInformation : NSManagedObject

//+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response inManagedObjectContext:(NSManagedObjectContext *)context;
+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response;
+ (void)saveContext;
+ (void)saveCurrentLoginUserName:(NSString *)username;
+ (LoginUserInformation *)infoForUser:(NSString *)username;
+ (BOOL)isCurrentUserLoginStatusOK;

@end

NS_ASSUME_NONNULL_END

#import "LoginUserInformation+CoreDataProperties.h"
