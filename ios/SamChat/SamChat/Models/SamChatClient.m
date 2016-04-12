//
//  SamChatClient.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SamChatClient.h"
#import "SCLoginModel.h"
#import "SCSignupModel.h"
#import "SCUserSettingModel.h"
#import "SCAnswerQuestionModel.h"
#import "SCUserRelationModel.h"
#import "SCArticleModel.h"
#import "SCProducerModel.h"

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

- (void)sendAnswer:(NSString *)answer toQuestionID:(NSInteger)question_id completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCAnswerQuestionModel sendAnswer:answer toQuestionID:question_id completion:completion];
}

- (void)makeFollow:(BOOL)flag withUser:(NSInteger)userID completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCUserRelationModel makeFollow:flag withUser:userID completion:completion];
}

- (void)publishArticleWithImages:(NSArray *)images comment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCArticleModel publishArticleWithImages:images comment:comment completion:completion];
}


- (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    [SCProducerModel upgradeToProducerWithInformationDictionary:info completion:completion];
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
