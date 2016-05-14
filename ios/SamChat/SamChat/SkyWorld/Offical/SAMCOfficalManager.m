//
//  SAMCOfficalManager.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCOfficalManager.h"
#import "SAMCSkyWorldAPI.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "AFNetworking.h"

@implementation SAMCOfficalManager

+ (void)makeFollow:(BOOL)flag withUser:(NSNumber *)userID completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SAMCSkyWorldAPI urlMakeFollow:flag withUser:userID bothSide:NO]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    completion(false, [SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
#warning save the relation ship
                    completion(true, nil);
                }
            }else{
                completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

@end
