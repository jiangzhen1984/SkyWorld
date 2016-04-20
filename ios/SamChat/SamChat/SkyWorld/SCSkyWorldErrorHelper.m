//
//  SCSkyWorldErrorHelper.m
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSkyWorldErrorHelper.h"

@implementation SCSkyWorldErrorHelper

+ (NSError *)errorWithCode:(NSInteger)code
{
    NSString *localizedDescription;
    switch (code) {
        case SCSkyWorldErrorParseFailed:
            localizedDescription = @"解析失败";
            break;
        case SCSkyWorldErrorActionNotFound:
            localizedDescription = @"Action参数不支持";
            break;
        case SCSkyWorldErrorParameterWrong:
            localizedDescription = @"参数不满足";
            break;
        case SCSkyWorldErrorTokenFormatWrong:
            localizedDescription = @"token 格式不正确";
            break;
        case SCSkyWorldErrorTokenNotExist:
            localizedDescription = @"token不存在";
            break;
        case SCSkyWorldErrorHandleStreamFailed:// handle stream failed
            localizedDescription = @"服务器错误";
            break;
        case SCSkyWorldErrorUsernameOrPasswordAlreadyExist:// 用户名或者手机号已经存在返回
            localizedDescription = @"用户名或密码已经存在，请重新输入";
            break;
        case SCSkyWorldErrorPassWordMismatch: // 密码不匹配
            localizedDescription = @"密码不匹配，请重新输入";
            break;
        case SCSkyWorldErrorInternalError: // 内部错误
            localizedDescription = @"服务器错误";
            break;
        case SCSkyWorldErrorUsernameOrPasswordWrong://  = -201, // 用户或者密码错误
            localizedDescription = @"用户名或密码错误，请重新输入";
            break;
        case SCSkyWorldErrorLogoutTokenInvalid: // = -401, // token 不合法
            localizedDescription = @"token 不合法";
            break;
        case SCSkyWorldErrorUpgradeInternalError://  = -501, // 升级内部错误
            localizedDescription = @"升级内部错误";
            break;
        case SCSkyWorldErrorUpgradeTokenInvalid: // = -502, // 不合法的用户TOKEN
            localizedDescription = @"token 不合法";
            break;
        case SCSkyWorldErrorAlreadyUpgrade://  = -503, // 用户已经升级过了
            localizedDescription = @"已经升级成为服务者";
            break;
        case SCSkyWorldErrorUserQueryOptUnsupported: //  = -701, // opt不支持
            localizedDescription = @"用户查询失败";
            break;
        case SCSkyWorldErrorCompanyQueryOptUnsupported: // = -1201, // opt not support
            localizedDescription = @"company 查询失败";
            break;
        case SCSkyWorldErrorQueryedUserIsNotSkervier: // = -1202, // queryed user is not skervier
            localizedDescription = @"被查询用户不是服务者";
            break;
        case SCSkyWorldErrorQuestionOptUnsupported: // = -301, // opt unsupported
            localizedDescription = @"查询失败";
            break;
        case SCSkyWorldErrorQuestionInternalError: // = -302, // 内部错误
            localizedDescription = @"发布问题内部错误";
            break;
        case SCSkyWorldErrorQuestionNotFound: // = -303, // 没有该问题, 针对取消， 结束问题时会返回该值
            localizedDescription = @"问题不存在";
            break;
        case SCSkyWorldErrorAnswerNoSuchQuestion: // = -601, // No Such Question
            localizedDescription = @"问题不存在";
            break;
        case SCSkyWorldErrorAnswerNotServicer: // = -602, // Not Servicer
            localizedDescription = @"不是服务者";
            break;
        case SCSkyWorldErrorFollowUserNotExist: // = -1001, // user doesn't exist
            localizedDescription = @"用户不存在";
            break;
        case SCSkyWorldErrorFollowUnsupportFlag:// = -1002, // unsupport flag
            localizedDescription = @"参数不支持";
            break;
        case SCSkyWorldErrorUpdateAvatarFailed: // = -801, // 上传失败
            localizedDescription = @"头像上传失败";
            break;
        case SCSkyWorldErrorUpdateAvatarTypeUnsupported: // = -802, // type not support
            localizedDescription = @"头像格式不支持";
            break;
        case SCSkyWorldErrorUpdateAvatarSizeTooBig: // = -803, // 头像size过大
            localizedDescription = @"头像尺寸太大";
            break;
        case SCSkyWorldErrorArticleNotExist: // = -901, // article not exist
            localizedDescription = @"内容不存在";
            break;
            // network error
        case SCSkyWorldErrorNetworkUnavailable: // = 1,
            localizedDescription = @"网络连接断开";
            break;
        case SCSkyWorldErrorServerNotReachable: // = 2,
            localizedDescription = @"连接服务器失败";
            break;
        case SCSkyWorldErrorUnknowError: // = 3
            localizedDescription = @"未知不错";
            break;
        case SCSkyWorldErrorLogoutError: // = 4
            localizedDescription = @"退出失败";
            break;
        default:
            localizedDescription = @"未知错误2";
            break;
    }
    NSDictionary *userInfo = @{NSLocalizedDescriptionKey:localizedDescription};
    return [NSError errorWithDomain:SC_SKYWORLD_ERROR_DOMAIN code:code userInfo:userInfo];
}


@end
