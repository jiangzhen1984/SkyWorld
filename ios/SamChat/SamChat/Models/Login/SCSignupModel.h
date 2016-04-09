//
//  SCSignupModel.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCLoginModel.h"

@protocol SCSignupDelegate
- (void)didSignupSuccess;
- (void)didSignupFailedWithError:(SCSkyWorldError *)error;
@end

@interface SCSignupModel : NSObject

+ (void)signupWithUserinfoDictionary:(NSDictionary *)info delegate:(id<SCSignupDelegate, SCLoginDelegate>) delegate;

@end
