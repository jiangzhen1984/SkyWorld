//
//  SCCrashCatcher.m
//  SamChat
//
//  Created by HJ on 4/13/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCCrashCatcher.h"

@implementation SCCrashCatcher

void uncaughtExceptionHandler(NSException *exception)
{
    NSArray *stackArray = [exception callStackSymbols];
    NSString *reason = [exception reason];
    NSString *name = [exception name];
    NSString *exceptionInfo = [NSString stringWithFormat:@"Exception reason：%@\nException name：%@\nException stack：%@",name, reason, stackArray];
    
    NSString *crashLog = [NSString stringWithFormat:@"CrashTime:%@\n%@\n%@",
                          [SCCrashCatcher crashTime],
                          [SCCrashCatcher appInformation],
                          exceptionInfo];
    DebugLog(@"CrashLog: %@", crashLog);
    [crashLog writeToFile:[NSString stringWithFormat:@"%@/Documents/error.log",NSHomeDirectory()]  atomically:YES encoding:NSUTF8StringEncoding error:nil];
}

+ (NSString *)crashTime
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"YYYY.MM.dd HH:mm"];
    return [formatter stringFromDate:[NSDate date]];
}

+ (NSString *)appInformation
{
    NSString *appInfo = [NSString stringWithFormat:@"SamChat:\nBundleDisplayName:%@\nBundle Version:%@(%@)\nDevice:%@\nOS Version:%@ %@\n",
                         [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"],
                         [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"],
                         [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"],
                         [UIDevice currentDevice].model,
                         [UIDevice currentDevice].systemName,
                         [UIDevice currentDevice].systemVersion];
    return appInfo;
}
@end
