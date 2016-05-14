//
//  SAMCProducerManager.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCProducerManager.h"
#import "SAMCSkyWorldAPI.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "AFNetworking.h"

@implementation SAMCProducerManager

+ (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSString *area = info[SKYWORLD_AREA] ?:@"";
    NSString *location = info[SKYWORLD_LOCATION] ?:@"";
    NSString *desc = info[SKYWORLD_DESC] ?:@"";
    NSString *urlString = [SAMCSkyWorldAPI urlUpgradeWithArea:area
                                                     location:location
                                                  description:desc];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DDLogDebug(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     if(errorCode == SCSkyWorldErrorAlreadyUpgrade){
                         completion(true, nil);
                     }else{
                         completion(false, [SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
                     }
                 }else{
#warning 11111111111111111111111111111
//                     [[SCUserProfileManager sharedInstance] saveCurrentLoginUserInformationWithSkyWorldResponse:response
//                                                                                                   andOtherInfo:nil];
                     completion(true, nil);
                 }
             }else{
                 completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DDLogDebug(@"Error: %@", error);
             completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
         }];
}

@end
