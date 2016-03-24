//
//  SCLoginUser.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCLoginUser.h"
#import "AppMacro.h"

@implementation SCLoginUser

- (instancetype)initWithDictionary: (NSDictionary *)dict andStatus: (NSInteger)status
{
    self = [super init];
    if(self) {
        _status = status;
        _username = dict[SC_LOGINUSER_USERNAME];
        _phonenumber = dict[SC_LOGINUSER_PHONENUMBER];
        _password = dict[SC_LOGINUSER_PASSWORD];
        _usertype = [dict[SC_LOGINUSER_USERTYPE] integerValue];
        _imagefile = dict[SC_LOGINUSER_IMAGEFILE];
        _userdescription = dict[SC_LOGINUSER_DESCRIPTION];
        _area = dict[SC_LOGINUSER_AREA];
        _location = dict[SC_LOGINUSER_LOCATION];
        _logintime = [dict[SC_LOGINUSER_LOGINTIME] integerValue];
        _logouttime = [dict[SC_LOGINUSER_LOGOUTTIME] integerValue];
        _unique_id = [dict[SC_LOGINUSER_UNIQUE_ID] integerValue];
        _easemob_username = dict[SC_LOGINUSER_EASEMOB_USERNAME];
        _easemob_status = [dict[SC_LOGINUSER_EASEMOB_STATUS] integerValue];
        _lastupdate = [dict[SC_LOGINUSER_LASTUPDATE] integerValue];
    }
    return self;
}

@end
