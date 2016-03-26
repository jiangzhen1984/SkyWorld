//
//  SCSkyWorldAPI.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCSkyWorldAPIMacro.h"

@interface SCSkyWorldAPI : NSObject

- (instancetype)initAPI:(NSString *)type WithHeader:(NSDictionary *)header andBody:(NSDictionary *)body;
- (NSURL *)generateUrl;
- (NSString *)generateUrlString;

@end
