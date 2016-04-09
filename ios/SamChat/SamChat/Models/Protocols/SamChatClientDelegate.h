//
//  SamChatClientDelegate.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum{
    ESamChatConnectionConnected = 0,
    ESamChatConnectionDisconnected,
}ESamChatConnectionState;

@protocol SamChatClientDelegate <NSObject>

@optional

/*!
 *  SDK连接服务器的状态变化时会接收到该回调
 *
 *  有以下几种情况, 会引起该方法的调用:
 *  1. 登录成功后, 手机无法上网时, 会调用该回调
 *  2. 登录成功后, 网络状态变化时, 会调用该回调
 *
 *  @param aConnectionState 当前状态
 */
- (void)didConnectionStateChanged:(EMConnectionState)aConnectionState;

/*!
 *  自动登录失败时的回调
 *
 *  @param aError 错误信息
 */
- (void)didAutoLoginWithError:(NSError *)aError;

/*!
 *  当前登录账号在其它设备登录时会接收到该回调
 */
- (void)didLoginFromOtherDevice;

/*!
 *  当前登录账号已经被从服务器端删除时会收到该回调
 */
- (void)didRemovedFromServer;

@end
