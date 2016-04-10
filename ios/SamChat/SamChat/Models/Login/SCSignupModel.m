//
//  SCSignupModel.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSignupModel.h"

@implementation SCSignupModel

+ (void)signupWithUserinfoDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    NSString *urlString = [SCSkyWorldAPI urlRegisterWithCellphone:info[SKYWORLD_CELLPHONE]
                                                      countryCode:info[SKYWORLD_COUNTRY_CODE]
                                                         userName:info[SKYWORLD_USERNAME]
                                                         passWord:info[SKYWORLD_PWD]];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode) {
                    if(completion){
                        completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                    }
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:info[SKYWORLD_PWD]}];
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
