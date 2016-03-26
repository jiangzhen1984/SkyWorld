//
//  SCUserProfile.m
//  SamChat
//
//  Created by HJ on 3/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUserProfile.h"
#import "SCSkyWorldAPIMacro.h"
#import "AppMacro.h"

@implementation SCUserProfile

- (instancetype)initWithLoginSuccessServerResponse: (NSDictionary *)response
{
    self = [super init];
    if(self) {
        _status = SC_LOGINUSER_LOGIN;
        _token = response[SKYWORLD_TOKEN];
        NSDictionary *user = response[SKYWORLD_USER];
        if(user) {
            _username = user[SKYWORLD_USERNAME];
            _phonenumber = user[SKYWORLD_CELLPHONE];
            _usertype = [user[SKYWORLD_TYPE] integerValue];
            _imagefile = [user valueForKeyPath:SKYWORLD_AVATAR_ORIGIN];
            _userdescription = user[SKYWORLD_DESC];
            _area = user[SKYWORLD_AREA];
            _location = user[SKYWORLD_LOCATION];
            _unique_id = [user[SKYWORLD_ID] integerValue];
            _easemob_username = [user valueForKeyPath:SKYWORLD_EASEMOB_USERNAME];
            _lastupdate = [user[SKYWORLD_LASTUPDATE] integerValue];
        }
    }
    return self;
}

- (void)saveProfileForLoginSuccess
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setInteger:_status forKey:SC_LOGINUSER_STATUS];
    [userDefaults setObject:_username ?:@"" forKey:SC_LOGINUSER_USERNAME];
    [userDefaults setObject:_phonenumber ?:@"" forKey:SC_LOGINUSER_PHONENUMBER];
    [userDefaults setObject:_password ?:@"" forKey:SC_LOGINUSER_PASSWORD];
    [userDefaults setInteger:_usertype forKey:SC_LOGINUSER_USERTYPE];
    [userDefaults setObject:_imagefile ?:@"" forKey:SC_LOGINUSER_IMAGEFILE];
    [userDefaults setObject:_userdescription ?:@"" forKey:SC_LOGINUSER_DESCRIPTION];
    [userDefaults setObject:_area ?:@"" forKey:SC_LOGINUSER_AREA];
    [userDefaults setObject:_location ?:@"" forKey:SC_LOGINUSER_LOCATION];
    
    // only logintime
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970];
    int64_t timestamp = [[NSNumber numberWithDouble:timeInterval] longLongValue];
    //NSLog(@"time: %lld", timestamp);
    [userDefaults setInteger:timestamp forKey:SC_LOGINUSER_LOGINTIME];
    
    [userDefaults setInteger:_unique_id forKey:SC_LOGINUSER_UNIQUE_ID];
    [userDefaults setObject:_easemob_username ?:@"" forKey:SC_LOGINUSER_EASEMOB_USERNAME];
    [userDefaults setInteger:_easemob_status forKey:SC_LOGINUSER_EASEMOB_STATUS];
    [userDefaults setInteger:_lastupdate forKey:SC_LOGINUSER_LASTUPDATE];
    [userDefaults setObject:_token ?:@"" forKey:SC_LOGINUSER_TOKEN];
    
    [userDefaults synchronize];
}

@end
