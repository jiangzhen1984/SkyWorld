//
//  SCSignupModel.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSignupModel.h"

@implementation SCSignupModel

+ (void)signupWithUserinfoDictionary:(NSDictionary *)info delegate:(id<SCSignupDelegate, SCLoginDelegate>) delegate
{
    if(delegate == nil){
        return;
    }
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
                    [delegate didSignupFailedWithError:[SCSkyWorldError errorWithCode:errorCode]];
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:info[SKYWORLD_PWD]}];
                    [delegate didSignupSuccess];
                    [SCLoginModel loginEaseMobWithUsername:info[SKYWORLD_USERNAME]
                                                  password:info[SKYWORLD_PWD]
                                                  delegate:delegate];
                }
            }else{
                [delegate didSignupFailedWithError:[SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]];
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            [delegate didSignupFailedWithError:[SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]];
        }];
}

@end
