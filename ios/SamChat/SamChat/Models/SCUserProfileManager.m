//
//  SCUserProfileManager.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUserProfileManager.h"

static SCUserProfileManager *sharedInstance = nil;

@interface SCUserProfileManager ()

@end

@implementation SCUserProfileManager

@synthesize username = _username;
@synthesize token = _token;

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
    if(self){
        
    }
    return self;
}

- (NSString *)username
{
    if(!_username) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        _username = [userDefaults objectForKey:SC_CURRENT_LOGIN_USERNAME];
    }
    return _username;
}

- (void)setUsername:(NSString *)username
{
    if(![_username isEqualToString:username]) {
        _username = username;
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:_username forKey:SC_CURRENT_LOGIN_USERNAME];
    }
}

- (NSString *)token
{
    if(!_token) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        _token = [userDefaults objectForKey:SC_CURRENT_LOGIN_TOKEN];
    }
    return _token;
}

- (void)setToken:(NSString *)token
{
    if(![_token isEqualToString:token]) {
        _token = token;
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:_token forKey:SC_CURRENT_LOGIN_TOKEN];
    }
}

- (LoginUserInformation *)currentLoginUserInformation
{
    LoginUserInformation *currentLoginUserInformation = nil;
    if(self.username) {
        currentLoginUserInformation = [LoginUserInformation loginUserInformationForUser:self.username];
    }
    return currentLoginUserInformation;
}

- (BOOL)isCurrentUserLoginStatusOK
{
    BOOL status = FALSE;
    LoginUserInformation *currentUserInformation = [self currentLoginUserInformation];
    status = ([currentUserInformation.status integerValue] == SC_LOGINUSER_LOGIN)
             && ([currentUserInformation.easemob_status integerValue] == SC_LOGINUSER_LOGIN)
             && [EMClient sharedClient].isAutoLogin;
    return status;
}

- (void)saveCurrentLoginUserInfoWithServerResponse: (NSDictionary *)response andOtherInfo:(NSDictionary *)otherInfo
{
    //SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
    self.username = [response valueForKeyPath:SKYWORLD_USER_USERNAME];
    self.token = response[SKYWORLD_TOKEN];
    LoginUserInformation *loginUserInformation = [LoginUserInformation infoWithServerResponse:response];
    loginUserInformation.password = otherInfo[SKYWORLD_PWD];
    
    // only logintime
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970];
    int64_t timestamp = [[NSNumber numberWithDouble:timeInterval] longLongValue];
    //DebugLog(@"time: %lld", timestamp);
    loginUserInformation.logintime = [NSNumber numberWithLongLong:timestamp];
    
    [LoginUserInformation saveContext];
}

@end
