//
//  SCSkyWorldErrorHelper.h
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

#define SC_SKYWORLD_ERROR_DOMAIN        @"com.SkyWorld.SamChat"

@interface SCSkyWorldErrorHelper : NSObject

+ (NSError *)errorWithCode:(NSInteger)code;

@end
