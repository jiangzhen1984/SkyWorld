//
//  SCSkyWorldUserAPI.h
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 skyworld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCSkyWorldAPI.h"

@interface SCSkyWorldUserAPI : NSObject

- (instancetype)initWithHeader: (NSDictionary *)header andBody: (NSDictionary *)body;
- (NSURL *)generateUrl;
- (NSString *)generateUrlString;

@end
