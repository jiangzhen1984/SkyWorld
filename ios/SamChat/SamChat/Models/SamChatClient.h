//
//  SamChatClient.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SCPushManager.h"


@interface SamChatClient : NSObject

@property (nonatomic, strong) SCPushManager *pushManager;

+ (instancetype)sharedInstance;

// SCAccountManager
- (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, NSError *error))completion;
- (void)signupWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode username:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, NSError *error))completion;
- (void)logoutWithCompletion:(void (^)(BOOL success, NSError *error))completion;

// SCProducerManager
- (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, NSError *error))completion;

// SCOfficalManager
- (void)makeFollow:(BOOL)flag withUser:(NSNumber *)userID completion:(void (^)(BOOL success, NSError *error))completion;

// SCSettingManager
- (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, NSError *error))completion;
- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion;
- (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion;

// SCServiceManager
- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType currentCount:(NSInteger)currentCount updateTimePre:(NSTimeInterval)updateTimePre completion:(void (^)(BOOL success, NSDictionary *response, NSError *error))completion;

- (void)asyncWaitingPush;

@end
