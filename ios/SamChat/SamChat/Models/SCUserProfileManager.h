//
//  SCUserProfileManager.h
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ContactUser.h"

@interface SCUserProfileManager : NSObject

+ (instancetype)sharedInstance;

// 当前用户
@property (nonatomic, strong) NSString *username;
@property (nonatomic, strong) NSString *token;

- (LoginUserInformation *)currentLoginUserInformation;
//- (void)saveLoginUserInformation;
- (BOOL)isCurrentUserLoginStatusOK;
- (void)logOutCurrentUser;
- (void)saveCurrentLoginUserInformationWithSkyWorldResponse:(NSDictionary *)response andOtherInfo:(NSDictionary *)otherInfo;
- (void)updateCurrentLoginUserInformationWithEaseMobStatus:(NSInteger)status;
- (void)updateCurrentLoginUserInformationWithEaseMobPushInfo:(NSDictionary *)info;

- (void)updateCurrentLoginUserInformationWithUnreadQuestionCountAddOne;
- (void)clearCurrentLoginUserInformationUnreadQuestionCount;

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion;



//- (void)updateUserProfileInBackground:(NSDictionary*)param
//                           completion:(void (^)(BOOL success, NSError *error))completion;


- (void)loadUserProfileInBackground:(NSArray*)usernames
                       saveToLoacal:(BOOL)save
                         completion:(void (^)(BOOL success, NSError *error))completion;

- (void)loadUserProfileInBackgroundWithBuddy:(NSArray*)buddyList
                                saveToLoacal:(BOOL)save
                                  completion:(void (^)(BOOL success, NSError *error))completion;


- (ContactUser *)getUserProfileByUsername:(NSString*)username;
- (ContactUser *)getCurUserProfile;
- (NSString*)getNickNameWithUsername:(NSString*)username;


@end
