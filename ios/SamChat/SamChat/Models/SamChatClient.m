//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"
#import "SCUserSettingModel.h"

static SamChatClient *sharedInstance = nil;

@interface SamChatClient ()

@end

@implementation SamChatClient

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

- (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCLoginModel loginWithUsername:username password:password completion:completion];
}

- (void)signupWithUserinfoDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCSignupModel signupWithUserinfoDictionary:info completion:completion];
}

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCUserSettingModel uploadUserAvatarInBackground:image completion:completion];
}

- (void)logoutWithCompletion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCUserSettingModel logoutWithCompletion:completion];
}

- (void)asyncWaitingPush
{
    [self.pushManager asyncWaitingPush];
}

#pragma mark - Lazy initialization
- (SCPushManager *)pushManager
{
    if(_pushManager == nil){
        _pushManager = [[SCPushManager alloc] init];
    }
    return _pushManager;
}

@end
