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

+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response inManagedObjectContext:(NSManagedObjectContext *)context
{
    LoginUserInformation *info = nil;
    NSString *username = [response valueForKeyPath:SKYWORLD_USER_USERNAME];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_LOGIN_USER_INFORMATION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %@", LOGIN_USER_INFORMATION_USERNAME, username];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)) {
    
    } else if([matches count]) {
        info = [matches firstObject];
    } else {
        info = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_LOGIN_USER_INFORMATION
                                             inManagedObjectContext:context];
        info.area = @"";
        info.discription = @"";
        info.easemob_status = @0;
        info.easemob_username = @"";
        info.imagefile = @"";
        info.lastupdate = @0;
        info.location = @"";
        info.logintime = @0;
        info.logouttime = @0;
        info.password = @"";
        info.phonenumber = @"";
        info.status = @0;
        info.unique_id = @0;
        info.username = username;
        info.usertype = @0;
        info.countrycode = @"";
    }
    
    NSDictionary *user = response[SKYWORLD_USER];
    info.area = user[SKYWORLD_AREA] ?:@"";
    info.discription = user[SKYWORLD_DESC] ?:@"";
    //info.easemob_status ;
    info.easemob_username = username;
    info.imagefile = user[SKYWORLD_DESC] ?:@"";
    info.lastupdate = user[SKYWORLD_LASTUPDATE];
    info.location = user[SKYWORLD_LOCATION] ?:@"";
    //info.logintime ;
    //info.logouttime;
    //info.password;
    info.phonenumber = user[SKYWORLD_CELLPHONE] ?:@"";
    info.status = @SC_LOGINUSER_LOGIN;
    info.unique_id = user[SKYWORLD_ID];
    info.usertype = user[SKYWORLD_TYPE];
    //info.countrycode = user[];
    
    return info;
}

+ (LoginUserInformation *)infoWithServerResponse:(NSDictionary *)response
{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    return [LoginUserInformation infoWithServerResponse:response inManagedObjectContext:context];
}

+ (void)saveContext:(NSManagedObjectContext *)managedObjectContext
{
    NSError *error;
    if(managedObjectContext) {
        if([managedObjectContext hasChanges] && [managedObjectContext save:&error]) {
            DebugLog(@"context save error: %@", error);
        }
    }
}

+ (void)saveContext
{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    [appDelegate saveContext];
}

+ (LoginUserInformation *)loginUserInformationForUser:(NSString *)username
{
    AppDelegate *appDelegate = [UIApplication sharedApplication].delegate;
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    LoginUserInformation *info = nil;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_LOGIN_USER_INFORMATION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %@", LOGIN_USER_INFORMATION_USERNAME, username];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if(matches && ([matches count] == 1)) {
        info = [matches firstObject];
    }
    return info;
}


@end
