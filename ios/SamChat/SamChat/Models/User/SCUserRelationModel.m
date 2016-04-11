//
//  SCUserRelationModel.m
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUserRelationModel.h"

@implementation SCUserRelationModel

+ (void)makeFollow:(BOOL)flag withUser:(NSInteger)userID completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SCSkyWorldAPI urlMakeFollow:flag withUser:userID bothSide:NO]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    if(completion){
                        completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                    }
                }else{
#warning save the relation ship
                    if(completion){
                        completion(true, nil);
                    }
                }
            }else{
                if(completion){
                    completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
                }
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            if(completion){
                completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
            }
        }];
}


@end
