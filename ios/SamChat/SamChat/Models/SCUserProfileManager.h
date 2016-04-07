//
//  SCUserProfileManager.h
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCUserProfileManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, strong) NSString *username;
@property (nonatomic, strong) NSString *token;

- (LoginUserInformation *)currentLoginUserInformation;
//- (void)saveLoginUserInformation;
- (BOOL)isCurrentUserLoginStatusOK;
- (void)saveCurrentLoginUserInformationWithSkyWorldResponse:(NSDictionary *)response andOtherInfo:(NSDictionary *)otherInfo;
- (void)updateCurrentLoginUserInformationWithEaseMobStatus:(NSInteger)status;
- (void)updateCurrentLoginUserInformationWithEaseMobPushInfo:(NSDictionary *)info;

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion;
@end
