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


@end
