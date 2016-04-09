//
//  SCLoginModel.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCLoginModel.h"

@implementation SCLoginModel

+ (void)loginWithUsername:(NSString *)username password:(NSString *)password delegate:(id<SCLoginDelegate>) delegate
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
                    [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:errorCode]];
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:password}];
                    [SCLoginModel loginEaseMobWithUsername:username password:password delegate:delegate];
                }
            }else{
                [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:SCSkyWorldErrorUnknowError]];
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:SCSkyWorldErrorServerNotReachable]];
        }];
}

+ (void)loginEaseMobWithUsername:(NSString *)username password:(NSString *)password delegate:(id<SCLoginDelegate>) delegate
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
                [delegate didLoginSuccess];
            } else {
                [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobStatus:SC_LOGINUSER_NO_LOGIN];
                 switch (error.code){
                     case EMErrorNetworkUnavailable:
                         [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:SCSkyWorldErrorNetworkUnavailable]];
                         break;
                     case EMErrorServerTimeout:
                     case EMErrorServerNotReachable:
                         [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:SCSkyWorldErrorServerNotReachable]];
                         break;
                     case EMErrorUserAuthenticationFailed:
                         [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:SCSkyWorldErrorUsernameOrPasswordWrong]];
                         break;
                     default:
                         [delegate didLoginFailedWithError:[SCLoginModel buildSkyWorldErrorWithCode:SCSkyWorldErrorUnknowError]];
                         break;
                 }
            }
        });
    });
}

+ (SCSkyWorldError *)buildSkyWorldErrorWithCode:(NSInteger)code
{
//    解析失败返回：  {ret: -1}
//    action参数不支持返回：  {ret: -2}
//    参数不满足返回：  {ret: -3}
//    用户或者密码错误返回：  {ret: -201}
    SCSkyWorldError *error;
    switch (code) {
        case SCSkyWorldErrorParseFailed:
            error = [SCSkyWorldError errorWithDescription:@"内容错误" code:SCSkyWorldErrorParseFailed];
            break;
        case SCSkyWorldErrorActionNotFound:
            error = [SCSkyWorldError errorWithDescription:@"内容错误" code:SCSkyWorldErrorActionNotFound];
            break;
        case SCSkyWorldErrorParameterWrong:
            error = [SCSkyWorldError errorWithDescription:@"内容错误" code:SCSkyWorldErrorParameterWrong];
            break;
        case SCSkyWorldErrorUsernameOrPasswordWrong:
            error = [SCSkyWorldError errorWithDescription:@"用户名或密码错误，请重新输入" code:SCSkyWorldErrorUsernameOrPasswordWrong];
            break;
        default:
            error = [SCSkyWorldError errorWithDescription:@"未知错误" code:SCSkyWorldErrorUnknowError];
            break;
    }
    return error;
}

@end
