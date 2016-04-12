//
//  SCProducerModel.m
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCProducerModel.h"

@implementation SCProducerModel

+ (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    NSString *area = info[SKYWORLD_AREA] ?:@"";
    NSString *location = info[SKYWORLD_LOCATION] ?:@"";
    NSString *desc = info[SKYWORLD_DESC] ?:@"";
    NSString *urlString = [SCSkyWorldAPI urlUpgradeWithArea:area
                                                   location:location
                                                description:desc];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DebugLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     if(completion){
                         if(errorCode == SCSkyWorldErrorAlreadyUpgrade){
                             completion(true, nil);
                         }else{
                             completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                         }
                     }
                 }else{
                     [[SCUserProfileManager sharedInstance] saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                                                   andOtherInfo:nil];
                     if(completion){
                         completion(true, nil);
                     }
                 }
             }else{
                 if(completion){
                     completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
                 }
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DebugLog(@"Error: %@", error);
         }];
}


@end
