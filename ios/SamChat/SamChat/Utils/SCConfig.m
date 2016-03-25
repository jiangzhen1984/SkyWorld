//
//  Config.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCConfig.h"
#import "SCLoginUser.h"
#import "AppMacro.h"

@implementation SCConfig

+ (void)saveProfile:(SCLoginUser *)user
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
//    [userDefaults setInteger:user.status forKey:SC_LOGINUSER_STATUS];
    [userDefaults setObject:user.username ?:@"" forKey:SC_LOGINUSER_USERNAME];
    [userDefaults setObject:user.phonenumber ?:@"" forKey:SC_LOGINUSER_PHONENUMBER];
//    [userDefaults setObject:user.password ?:@"" forKey:SC_LOGINUSER_PASSWORD];
    [userDefaults setObject:@1 forKey:@"test"];
    [userDefaults setInteger:user.usertype forKey:SC_LOGINUSER_USERTYPE];
    [userDefaults setObject:user.imagefile ?:@"" forKey:SC_LOGINUSER_IMAGEFILE];
    [userDefaults setObject:user.userdescription ?:@"" forKey:SC_LOGINUSER_DESCRIPTION];
    [userDefaults setObject:user.area ?:@"" forKey:SC_LOGINUSER_AREA];
    [userDefaults setObject:user.location ?:@"" forKey:SC_LOGINUSER_LOCATION];
//    [userDefaults setInteger:user.logintime forKey:SC_LOGINUSER_LOGINTIME];
//    [userDefaults setInteger:user.logouttime forKey:SC_LOGINUSER_LOGOUTTIME];
    [userDefaults setInteger:user.unique_id forKey:SC_LOGINUSER_UNIQUE_ID];
    [userDefaults setObject:user.easemob_username ?:@"" forKey:SC_LOGINUSER_EASEMOB_USERNAME];
//    [userDefaults setInteger:user.easemob_status forKey:SC_LOGINUSER_EASEMOB_STATUS];
    [userDefaults setInteger:user.lastupdate forKey:SC_LOGINUSER_LASTUPDATE];
    
    [userDefaults synchronize];
}

@end
