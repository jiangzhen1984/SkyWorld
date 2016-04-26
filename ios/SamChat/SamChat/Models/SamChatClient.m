//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"


#import "SCAccountManager.h"
#import "SCProducerManager.h"
#import "SCOfficalManager.h"
#import "SCSettingManager.h"
#import "SCServiceManager.h"

static SamChatClient *sharedInstance = nil;

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

- (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL, NSError *))completion
{
    [SCAccountManager loginWithUsername:username password:password completion:completion];
}

- (void)signupWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode username:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, NSError *error))completion
{
    [SCAccountManager signupWithCellphone:cellphone countryCode:countrycode username:username password:password completion:completion];
}

- (void)logoutWithCompletion:(void (^)(BOOL success, NSError *error))completion
{
    [SCAccountManager logoutWithCompletion:completion];
}

- (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, NSError *error))completion
{
    [SCProducerManager upgradeToProducerWithInformationDictionary:info completion:completion];
}

- (void)makeFollow:(BOOL)flag withUser:(NSNumber *)userID completion:(void (^)(BOOL success, NSError *error))completion
{
    [SCOfficalManager makeFollow:flag withUser:userID completion:completion];
}


- (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, NSError *error))completion
{
    [SCSettingManager feedbackWithComment:comment completion:completion];
}

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion
{
    [SCSettingManager uploadUserAvatarInBackground:image completion:completion];
}

- (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion
{
    [SCSettingManager checkVersionCompletion:completion];
}

- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType currentCount:(NSInteger)currentCount updateTimePre:(NSTimeInterval)updateTimePre completion:(void (^)(BOOL success, NSDictionary *response, NSError *error))completion
{
    [SCServiceManager queryTopicListWithOptType:optType
                                      topicType:topicType
                                   currentCount:currentCount
                                  updateTimePre:updateTimePre
                                     completion:completion];
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
