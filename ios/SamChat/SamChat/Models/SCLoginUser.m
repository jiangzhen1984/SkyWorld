//
//  SCLoginUser.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCLoginUser.h"
#import "AppMacro.h"
#import "SCSkyWorldAPI.h"

@implementation SCLoginUser

- (instancetype)initWithDictionary: (NSDictionary *)dict
{
    self = [super init];
    if(self) {
        _username = dict[SKYWORLD_USERNAME];
        _phonenumber = dict[SKYWORLD_CELLPHONE];
        _usertype = [dict[SKYWORLD_TYPE] integerValue];
        _imagefile = [dict valueForKeyPath:SKYWORLD_AVATAR_ORIGIN];
        _userdescription = dict[SKYWORLD_DESC];
        _area = dict[SKYWORLD_AREA];
        _location = dict[SKYWORLD_LOCATION];
        _unique_id = [dict[SKYWORLD_ID] integerValue];
        _easemob_username = [dict valueForKeyPath:SKYWORLD_EASEMOB_USERNAME];
        _lastupdate = [dict[SKYWORLD_LASTUPDATE] integerValue];
    }
    return self;
}

@end
