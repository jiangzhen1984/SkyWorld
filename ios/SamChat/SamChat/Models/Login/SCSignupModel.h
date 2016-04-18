//
//  SCSignupModel.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCLoginModel.h"

@interface SCSignupModel : NSObject

+ (void)signupWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode username:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

@end
