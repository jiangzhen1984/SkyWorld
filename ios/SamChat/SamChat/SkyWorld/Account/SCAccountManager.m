//
//  SCAccountManager.m
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCAccountManager.h"
#import "SCSkyWorldErrorHelper.h"

@implementation SCAccountManager

+ (void)signupWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode username:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSString *urlString = [SCSkyWorldAPI urlRegisterWithCellphone:cellphone
                                                      countryCode:countrycode
                                                         username:username
                                                         password:password];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode) {
                    completion(false, [SCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:password}];
                    completion(true, nil);
                }
            }else{
                completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

+ (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SCSkyWorldAPI urlLoginWithUsername:username passWord:password]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    completion(false, [SCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:password}];
                    [SCAccountManager loginEaseMobWithUsername:username password:password completion:completion];
                }
            }else{
                completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

+ (void)loginEaseMobWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = [[EMClient sharedClient] loginWithUsername:username
                                                           password:password];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error) {
                [[EMClient sharedClient].options setIsAutoLogin:YES];
                [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobStatus:SC_LOGINUSER_LOGIN];
                completion(true, nil);
            } else {
                [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobStatus:SC_LOGINUSER_NO_LOGIN];
                NSInteger errorCode = SCSkyWorldErrorUnknowError;
                switch (error.code){
                    case EMErrorNetworkUnavailable:
                        errorCode = SCSkyWorldErrorNetworkUnavailable;
                        break;
                    case EMErrorServerTimeout:
                    case EMErrorServerNotReachable:
                        errorCode = SCSkyWorldErrorServerNotReachable;
                        break;
                    case EMErrorUserAuthenticationFailed:
                        errorCode = SCSkyWorldErrorUsernameOrPasswordWrong;
                        break;
                    default:
                        errorCode = SCSkyWorldErrorUnknowError;
                        break;
                }
                completion(false, [SCSkyWorldErrorHelper errorWithCode:errorCode]);
            }
        });
    });
}

+ (void)logoutWithCompletion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = [[EMClient sharedClient] logout:YES];
        dispatch_async(dispatch_get_main_queue(), ^{
            if(error != nil){
                completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorLogoutError]);
            }else{
                // [[ApplyViewController shareController] clear];
                AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
                [manager GET:[SCSkyWorldAPI urlLogout]
                  parameters:nil
                    progress:^(NSProgress *downloadProgress){
                    }
                     success:^(NSURLSessionDataTask *task, id responseObject){
                         if([responseObject isKindOfClass:[NSDictionary class]]) {
                             DebugLog(@"%@", responseObject);
                         }
                     }
                     failure:^(NSURLSessionDataTask *task, NSError *error){
                         DebugLog(@"Logout Error: %@", error);
                     }];
                [[SCUserProfileManager sharedInstance] logOutCurrentUser];
                completion(true, nil);
            }
        });
    });
}

@end
