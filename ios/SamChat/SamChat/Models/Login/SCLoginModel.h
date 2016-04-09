//
//  SCLoginModel.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol SCLoginDelegate
- (void)didLoginSuccess;
- (void)didLoginFailedWithError:(SCSkyWorldError *)error;
@end


@interface SCLoginModel : NSObject

+ (void)loginWithUsername:(NSString *)username password:(NSString *)password delegate:(id<SCLoginDelegate>) delegate;

@end
