//
//  SCCrashCatcher.h
//  SamChat
//
//  Created by HJ on 4/13/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCCrashCatcher : NSObject

void uncaughtExceptionHandler(NSException *exception);

@end
