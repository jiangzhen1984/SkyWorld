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


- (instancetype)initWithDescription:(NSString *)aDescription
                               code:(SCSkyWorldErrorCode)aCode;


+ (instancetype)errorWithDescription:(NSString *)aDescription
                                code:(SCSkyWorldErrorCode)aCode;

+ (instancetype)errorWithCode:(NSInteger)aCode;

@end
