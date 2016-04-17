//
//  SCCrashCatcher.m
//  SamChat
//
//  Created by HJ on 4/13/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCCrashCatcher.h"

@implementation SCCrashCatcher

#define CRASH_LOG_FILE  [NSString stringWithFormat:@"%@/Documents/error.log",NSHomeDirectory()]

void uncaughtExceptionHandler(NSException *exception)
{
    NSArray *stackArray = [exception callStackSymbols];
    NSString *reason = [exception reason];
    NSString *name = [exception name];
    NSString *exceptionInfo = [NSString stringWithFormat:@"Exception reason：%@\nException name：%@\nException stack：%@",name, reason, stackArray];
    
    NSString *crashLog = [NSString stringWithFormat:@"%@\n%@\n%@",
                          [SCCrashCatcher crashTime],
                          [SCCrashCatcher appInformation],
                          exceptionInfo];
    DebugLog(@"CrashLog: %@", crashLog);
    [SCCrashCatcher uploadCrashLog:crashLog saveToLocalIfFailed:YES];
}

+ (NSString *)crashTime
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"YYYY.MM.dd HH:mm"];
    return [NSString stringWithFormat:@"CrashTime:%@\n",[formatter stringFromDate:[NSDate date]]];
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

+ (NSString *)getLogWithFile:(NSString *)logfile
{
    NSString *log = [NSString stringWithContentsOfFile:logfile
                                              encoding:NSUTF8StringEncoding
                                                 error:NULL];
    if((log==nil) || (log.length<=0)){
        log = [NSString stringWithFormat:@"Logfile:%@ read error.", logfile];
    }
    return log;
}

+ (void)putToFileWithCrashLog:(NSString *)crashLog
{
    [crashLog writeToFile:CRASH_LOG_FILE
               atomically:YES
                 encoding:NSUTF8StringEncoding
                    error:nil];
}

+ (void)uploadCrashLog:(NSString *)crashLog saveToLocalIfFailed:(BOOL)saveFlag
{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager POST:[SCSkyWorldAPI urlLogCollection]
       parameters:nil
constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
    [formData appendPartWithFileData:[crashLog dataUsingEncoding:NSUTF8StringEncoding]
                                name:@"crashlog"
                            fileName:@"filename"
                            mimeType:@"text/plain"];
} progress:^(NSProgress * _Nonnull uploadProgress) {
} success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
    DebugLog(@"Upload Crash Log Return:%@", responseObject);
    if([responseObject isKindOfClass:[NSDictionary class]]
       && ([(NSNumber *)responseObject[SKYWORLD_RET] integerValue] == 0)){
    }else{
        if(saveFlag){
            [SCCrashCatcher putToFileWithCrashLog:crashLog];
        }
    }
} failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
    DebugLog(@"Upload Crash Log Failed:%@", error);
    if(saveFlag){
        [SCCrashCatcher putToFileWithCrashLog:crashLog];
    }
}];
    
}

+ (void)findCrashLogFileToUpload
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if([fileManager fileExistsAtPath:CRASH_LOG_FILE]){
        [SCCrashCatcher uploadCrashLog:[SCCrashCatcher getLogWithFile:CRASH_LOG_FILE] saveToLocalIfFailed:NO];
        [fileManager removeItemAtPath:CRASH_LOG_FILE error:NULL];
    }
}

@end
