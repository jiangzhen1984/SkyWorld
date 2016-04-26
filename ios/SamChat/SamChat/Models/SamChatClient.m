//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"
#import "SCUserSettingModel.h"

#import "SCServiceSearchModel.h"

#import "SCAccountManager.h"
#import "SCProducerManager.h"
#import "SCOfficalManager.h"

static SamChatClient *sharedInstance = nil;

@interface SamChatClient ()

@property (nonatomic, strong) SCServiceSearchModel *serviceSearchModel;

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
        _serviceSearchModel = [SCServiceSearchModel new];
        [_serviceSearchModel resetModel];
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

//-----------------

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCUserSettingModel uploadUserAvatarInBackground:image completion:completion];
}

- (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCUserSettingModel feedbackWithComment:comment completion:completion];
}

- (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion
{
    [SCUserSettingModel checkVersionCompletion:completion];
}



- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType reset:(BOOL)flag completion:(void (^)(BOOL success, NSArray *topics, SCSkyWorldError *error))completion
{
    if(flag){
        [self.serviceSearchModel resetModel];
    }
    [self.serviceSearchModel queryTopicListWithOptType:optType topicType:topicType completion:completion];
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
