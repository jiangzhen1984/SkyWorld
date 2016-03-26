//
//  Config.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCConfig.h"
#import "AppMacro.h"

@implementation SCConfig

+ (NSString *)getUserToken
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return [userDefaults valueForKey:SC_LOGINUSER_TOKEN];
}

@end
