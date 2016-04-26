//
//  SCOfficalManager.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCOfficalManager.h"
#import "SCSkyWorldErrorHelper.h"

@implementation SCOfficalManager

+ (void)makeFollow:(BOOL)flag withUser:(NSNumber *)userID completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SCSkyWorldAPI urlMakeFollow:flag withUser:userID bothSide:NO]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    completion(false, [SCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
#warning save the relation ship
                    completion(true, nil);
                }
            }else{
                completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, [SCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

@end
