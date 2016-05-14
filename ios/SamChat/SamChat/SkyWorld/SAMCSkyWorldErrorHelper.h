//
//  SAMCSkyWorldErrorHelper.h
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum{
    SCSkyWorldErrorParseFailed = -1, // 解析失败
    SCSkyWorldErrorActionNotFound = -2, // action参数不支持
    SCSkyWorldErrorParameterWrong = -3, // 参数不满足
    SCSkyWorldErrorTokenFormatWrong = -4, // token 格式不正确
    SCSkyWorldErrorTokenNotExist = -5, // token不存在
    SCSkyWorldErrorHandleStreamFailed = -6, // handle stream failed
    
    SCSkyWorldErrorUsernameOrPasswordAlreadyExist = -101, // 用户名或者手机号已经存在返回
    SCSkyWorldErrorPassWordMismatch = -102, // 密码不匹配
    SCSkyWorldErrorInternalError = -103, // 内部错误
    
    SCSkyWorldErrorUsernameOrPasswordWrong = -201, // 用户或者密码错误
    
    SCSkyWorldErrorLogoutTokenInvalid = -401, // token 不合法
    
    SCSkyWorldErrorUpgradeInternalError = -501, // 升级内部错误
    SCSkyWorldErrorUpgradeTokenInvalid = -502, // 不合法的用户TOKEN
    SCSkyWorldErrorAlreadyUpgrade = -503, // 用户已经升级过了
    
    SCSkyWorldErrorUserQueryOptUnsupported = -701, // opt不支持
    
    SCSkyWorldErrorCompanyQueryOptUnsupported = -1201, // opt not support
    SCSkyWorldErrorQueryedUserIsNotSkervier = -1202, // queryed user is not skervier
    
    SCSkyWorldErrorQuestionOptUnsupported = -301, // opt unsupported
    SCSkyWorldErrorQuestionInternalError = -302, // 内部错误
    SCSkyWorldErrorQuestionNotFound = -303, // 没有该问题, 针对取消， 结束问题时会返回该值
    
    SCSkyWorldErrorAnswerNoSuchQuestion = -601, // No Such Question
    SCSkyWorldErrorAnswerNotServicer = -602, // Not Servicer
    
    SCSkyWorldErrorFollowUserNotExist = -1001, // user doesn't exist
    SCSkyWorldErrorFollowUnsupportFlag = -1002, // unsupport flag
    
    SCSkyWorldErrorUpdateAvatarFailed = -801, // 上传失败
    SCSkyWorldErrorUpdateAvatarTypeUnsupported = -802, // type not support
    SCSkyWorldErrorUpdateAvatarSizeTooBig = -803, // 头像size过大
    
    SCSkyWorldErrorArticleNotExist = -901, // article not exist
    
    // network error
    SCSkyWorldErrorNetworkUnavailable = 1,
    SCSkyWorldErrorServerNotReachable = 2,
    SCSkyWorldErrorUnknowError = 3,
    SCSkyWorldErrorLogoutError = 4,
}SAMCSkyWorldErrorCode;

#define SC_SKYWORLD_ERROR_DOMAIN        @"com.SkyWorld.SamChat"

@interface SAMCSkyWorldErrorHelper : NSObject

+ (NSError *)errorWithCode:(NSInteger)code;

@end
