//
//  LoginUserInformation.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "LoginUserInformation.h"
#import "AppDelegate.h"

@implementation LoginUserInformation

+ (LoginUserInformation *)loginUserInformationWithUserName:(NSString *)username inManagedObjectContext:(NSManagedObjectContext *)context
{
    LoginUserInformation *loginUserInformation = nil;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_LOGIN_USER_INFORMATION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %@", LOGIN_USER_INFORMATION_USERNAME, username];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)) {
        
    } else if([matches count]) {
        loginUserInformation = [matches firstObject];
    } else {
        loginUserInformation = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_LOGIN_USER_INFORMATION
                                             inManagedObjectContext:context];
        loginUserInformation.username = username;
    }
    return loginUserInformation;
}


@end
