//
//  SCLoginModel.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCLoginModel.h"

@implementation SCLoginModel

+ (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SCSkyWorldAPI urlLoginWithUsername:username passWord:password]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    if(completion){
                        completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                    }
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:password}];
                    [SCLoginModel loginEaseMobWithUsername:username password:password completion:completion];
                }
            }else{
                if(completion){
                    completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
                }
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            if(completion){
                completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
            }
        }];
}

+ (void)loginEaseMobWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = [[EMClient sharedClient] loginWithUsername:username
                                                           password:password];
        dispatch_async(dispatch_get_main_queue(), ^{
            if (!error) {
                //设置是否自动登录
                [[EMClient sharedClient].options setIsAutoLogin:YES];
                [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobStatus:SC_LOGINUSER_LOGIN];
                
                /*
                 //获取数据库中数据
                 [MBProgressHUD showHUDAddedTo:weakself.view animated:YES];
                 dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                 [[EMClient sharedClient] dataMigrationTo3];
                 dispatch_async(dispatch_get_main_queue(), ^{
                 [[ChatDemoHelper shareHelper] asyncGroupFromServer];
                 [[ChatDemoHelper shareHelper] asyncConversationFromDB];
                 [[ChatDemoHelper shareHelper] asyncPushOptions];
                 [MBProgressHUD hideAllHUDsForView:weakself.view animated:YES];
                 //发送自动登陆状态通知
                 [[NSNotificationCenter defaultCenter] postNotificationName:KNOTIFICATION_LOGINCHANGE object:@YES];
                 
                 //保存最近一次登录用户名
                 [weakself saveLastLoginUsername];
                 });
                 });*/
                if(completion){
                    completion(true, nil);
                }
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
                if(completion){
                    completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                }
            }
        });
    });
}

@end
