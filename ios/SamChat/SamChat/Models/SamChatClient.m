//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"

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


- (void)loginWithUsername:(NSString *)username password:(NSString *)password delegate:(id<SCLoginDelegate>) delegate
{
    [SCLoginModel loginWithUsername:username password:password delegate:delegate];
}

- (void)signupWithUserinfoDictionary:(NSDictionary *)info delegate:(id<SCSignupDelegate, SCLoginDelegate>) delegate
{
    [SCSignupModel signupWithUserinfoDictionary:info delegate:delegate];
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
