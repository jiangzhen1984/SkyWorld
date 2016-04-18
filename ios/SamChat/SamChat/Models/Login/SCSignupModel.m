//
//  SCSignupModel.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSignupModel.h"

@implementation SCSignupModel

+ (void)signupWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode username:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSString *urlString = [SCSkyWorldAPI urlRegisterWithCellphone:cellphone
                                                      countryCode:countrycode
                                                         username:username
                                                         password:password];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode) {
                    completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                }else{
                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                               andOtherInfo:@{SKYWORLD_PWD:password}];
                    completion(true, nil);
                }
            }else{
                completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

@end
