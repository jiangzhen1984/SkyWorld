//
//  LoginUserInformation.h
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class SendQuestion;
@class SendAnswer;


NS_ASSUME_NONNULL_BEGIN

@interface LoginUserInformation : NSManagedObject

+ (LoginUserInformation *)loginUserInformationWithUserName:(NSString *)username inManagedObjectContext:(NSManagedObjectContext *)context;
+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response;
+ (void)saveContext;

+ (LoginUserInformation *)loginUserInformationForUser:(NSString *)username;


@end

NS_ASSUME_NONNULL_END

#import "LoginUserInformation+CoreDataProperties.h"
