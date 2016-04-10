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

+ (void)signupWithUserinfoDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

@end
