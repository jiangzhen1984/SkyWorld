//
//  SCSkyWorldError.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSkyWorldError.h"

@implementation SCSkyWorldError


- (instancetype)initWithDescription:(NSString *)aDescription
                               code:(SCSkyWorldErrorCode)aCode
{
    self = [super init];
    if(self){
        _errorDescription = aDescription;
        _code = aCode;
    }
    return self;
}


+ (instancetype)errorWithDescription:(NSString *)aDescription
                                code:(SCSkyWorldErrorCode)aCode
{
    SCSkyWorldError *error = [[SCSkyWorldError alloc] initWithDescription:aDescription
                                                                     code:aCode];
    return error;
}

+ (instancetype)errorWithCode:(NSInteger)aCode
{
    SCSkyWorldError *error = [SCSkyWorldError errorWithDescription:nil code:aCode];
    switch (aCode) {
        case SCSkyWorldErrorParseFailed:
            error.errorDescription = @"解析失败";
            break;
        case SCSkyWorldErrorActionNotFound:
            error.errorDescription = @"Action参数不支持";
            break;
        case SCSkyWorldErrorParameterWrong:
            error.errorDescription = @"参数不满足";
            break;
        case SCSkyWorldErrorTokenFormatWrong:
            error.errorDescription = @"token 格式不正确";
            break;
        case SCSkyWorldErrorTokenNotExist:
            error.errorDescription = @"token不存在";
            break;
        case SCSkyWorldErrorHandleStreamFailed:// handle stream failed
            error.errorDescription = @"服务器错误";
            break;
        case SCSkyWorldErrorUsernameOrPasswordAlreadyExist:// 用户名或者手机号已经存在返回
            error.errorDescription = @"用户名或密码已经存在，请重新输入";
            break;
        case SCSkyWorldErrorPassWordMismatch: // 密码不匹配
            error.errorDescription = @"密码不匹配，请重新输入";
            break;
        case SCSkyWorldErrorInternalError: // 内部错误
            error.errorDescription = @"服务器错误";
            break;
        case SCSkyWorldErrorUsernameOrPasswordWrong://  = -201, // 用户或者密码错误
            error.errorDescription = @"用户名或密码错误，请重新输入";
            break;
        case SCSkyWorldErrorLogoutTokenInvalid: // = -401, // token 不合法
            error.errorDescription = @"token 不合法";
            break;
        case SCSkyWorldErrorUpgradeInternalError://  = -501, // 升级内部错误
            error.errorDescription = @"升级内部错误";
            break;
        case SCSkyWorldErrorUpgradeTokenInvalid: // = -502, // 不合法的用户TOKEN
            error.errorDescription = @"token 不合法";
            break;
        case SCSkyWorldErrorAlreadyUpgrade://  = -503, // 用户已经升级过了
            error.errorDescription = @"已经升级成为服务者";
            break;
        case SCSkyWorldErrorUserQueryOptUnsupported: //  = -701, // opt不支持
            error.errorDescription = @"用户查询失败";
            break;
        case SCSkyWorldErrorCompanyQueryOptUnsupported: // = -1201, // opt not support
            error.errorDescription = @"company 查询失败";
            break;
        case SCSkyWorldErrorQueryedUserIsNotSkervier: // = -1202, // queryed user is not skervier
            error.errorDescription = @"被查询用户不是服务者";
            break;
        case SCSkyWorldErrorQuestionOptUnsupported: // = -301, // opt unsupported
            error.errorDescription = @"查询失败";
            break;
        case SCSkyWorldErrorQuestionInternalError: // = -302, // 内部错误
            error.errorDescription = @"发布问题内部错误";
            break;
        case SCSkyWorldErrorQuestionNotFound: // = -303, // 没有该问题, 针对取消， 结束问题时会返回该值
            error.errorDescription = @"问题不存在";
            break;
        case SCSkyWorldErrorAnswerNoSuchQuestion: // = -601, // No Such Question
            error.errorDescription = @"问题不存在";
            break;
        case SCSkyWorldErrorAnswerNotServicer: // = -602, // Not Servicer
            error.errorDescription = @"不是服务者";
            break;
        case SCSkyWorldErrorFollowUserNotExist: // = -1001, // user doesn't exist
            error.errorDescription = @"用户不存在";
            break;
        case SCSkyWorldErrorFollowUnsupportFlag:// = -1002, // unsupport flag
            error.errorDescription = @"参数不支持";
            break;
        case SCSkyWorldErrorUpdateAvatarFailed: // = -801, // 上传失败
            error.errorDescription = @"头像上传失败";
            break;
        case SCSkyWorldErrorUpdateAvatarTypeUnsupported: // = -802, // type not support
            error.errorDescription = @"头像格式不支持";
            break;
        case SCSkyWorldErrorUpdateAvatarSizeTooBig: // = -803, // 头像size过大
            error.errorDescription = @"头像尺寸太大";
            break;
        case SCSkyWorldErrorArticleNotExist: // = -901, // article not exist
            error.errorDescription = @"内容不存在";
            break;
        // network error
        case SCSkyWorldErrorNetworkUnavailable: // = 1,
            error.errorDescription = @"网络连接断开";
            break;
        case SCSkyWorldErrorServerNotReachable: // = 2,
            error.errorDescription = @"连接服务器失败";
            break;
        case SCSkyWorldErrorUnknowError: // = 3
            error.errorDescription = @"未知不错";
            break;
        case SCSkyWorldErrorLogoutError: // = 4
            error.errorDescription = @"退出失败";
            break;
        default:
            error.errorDescription = @"未知错误2";
            break;
    }
    return error;
}

@end
