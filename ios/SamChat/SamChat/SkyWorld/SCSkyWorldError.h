//
//  SCSkyWorldError.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCSkyWorldErrorCode.h"

@interface SCSkyWorldError : NSObject

// 错误码
@property (nonatomic) SCSkyWorldErrorCode code;

// 错误描述
@property (nonatomic, strong) NSString *errorDescription;


/*!
 *  初始化错误实例
 *
 *  @param aDescription  错误描述
 *  @param aCode         错误码
 *
 *  @result 错误实例
 */
- (instancetype)initWithDescription:(NSString *)aDescription
                               code:(SCSkyWorldErrorCode)aCode;

/*!
 *  创建错误实例
 *
 *  @param aDescription  错误描述
 *  @param aCode         错误码
 *
 *  @result 对象实例
 */
+ (instancetype)errorWithDescription:(NSString *)aDescription
                                code:(SCSkyWorldErrorCode)aCode;

@end
