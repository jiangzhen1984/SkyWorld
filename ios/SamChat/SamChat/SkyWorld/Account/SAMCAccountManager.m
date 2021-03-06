//
//  SAMCAccountManager.m
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
#import "SAMCAccountManager.h"
#import "SAMCSkyWorldAPI.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "AFNetworking.h"
#import "SAMCUserProfileManager.h"
#import "NIMLoginManagerProtocol.h"
#import "GCDMulticastDelegate.h"

@interface SAMCAccountManager () <NIMLoginManagerDelegate>

@property (nonatomic, strong) GCDMulticastDelegate<SAMCLoginManagerDelegate> *multicastDelegate;

@end

@implementation SAMCAccountManager

- (instancetype)init
{
    if (self = [super init]) {
        [[[NIMSDK sharedSDK] loginManager] addDelegate:self];
    }
    return self;
}

- (void)dealloc
{
    [[[NIMSDK sharedSDK] loginManager] removeDelegate:self];
}

- (void)signup:(NSString *)account
      password:(NSString *)password
     cellphone:(NSString *)cellphone
   countryCode:(NSNumber *)countrycode
    completion:(void (^)(NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSString *urlString = [SAMCSkyWorldAPI urlRegisterWithCellphone:cellphone
                                                        countryCode:countrycode
                                                           username:account
                                                           password:password];
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil progress:^(NSProgress *downloadProgress) {
    } success:^(NSURLSessionDataTask *task, id responseObject) {
        if([responseObject isKindOfClass:[NSDictionary class]]){
            NSDictionary *response = responseObject;
            NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
            if(errorCode) {
                completion([SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
            }else{
#warning 11111111111111111111111111111
                //                    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                //                    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                //                                                                               andOtherInfo:@{SKYWORLD_PWD:password}];
                completion(nil);
            }
        }else{
            completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
    }];
}

- (void)login:(NSString *)account
     password:(NSString *)password
   completion:(void (^)(NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SAMCSkyWorldAPI urlLoginWithUsername:account passWord:@"pass00001"] // TODO: change to password
      parameters:nil progress:^(NSProgress *downloadProgress) {
    } success:^(NSURLSessionDataTask *task, id responseObject) {
        if([responseObject isKindOfClass:[NSDictionary class]]){
            NSDictionary *response = responseObject;
            NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
            if(errorCode){
                completion([SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
            }else{
                DDLogDebug(@"login: %@", response);
                [self loginNetEase:account
                             token:password // TODO:change to token
                        completion:completion];
#warning 11111111111111111111111111111
                //SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
                //[userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                //   andOtherInfo:@{SKYWORLD_PWD:password}];
                //[SAMCAccountManager loginEaseMobWithUsername:username password:password completion:completion];
            }
        }else{
            completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
    }];
}

- (void)autoLogin:(NSString *)account
            token:(NSString *)token
{
//TODO: add skyworld autologin
    [[[NIMSDK sharedSDK] loginManager] autoLogin:account
                                           token:token];
}

- (void)logout:(void (^)(NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    [[[NIMSDK sharedSDK] loginManager] logout:^(NSError *error) {
        extern NSString *NTESNotificationLogout;
        AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
        [manager GET:[SAMCSkyWorldAPI urlLogout]
          parameters:nil progress:^(NSProgress *downloadProgress){
        } success:^(NSURLSessionDataTask *task, id responseObject){
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DDLogDebug(@"%@", responseObject);
             }
         } failure:^(NSURLSessionDataTask *task, NSError *error){
             DDLogDebug(@"Logout Error: %@", error);
         }];
        [[SAMCUserProfileManager sharedManager] setCurrentLoginData:nil];
        completion(nil);
    }];
}


- (void)kickOtherClient:(NIMLoginClient *)client
             completion:(NIMLoginHandler)completion
{
    [[[NIMSDK sharedSDK] loginManager] kickOtherClient:client
                                            completion:completion];
}

- (NSString *)currentAccount
{
    return [[[NIMSDK sharedSDK] loginManager] currentAccount];
}

- (BOOL)isLogined
{
    return [[[NIMSDK sharedSDK] loginManager] isLogined];
}

- (NSArray *)currentLoginClients
{
    return [[[NIMSDK sharedSDK] loginManager] currentLoginClients];
}

- (void)addDelegate:(id<SAMCLoginManagerDelegate>)delegate
{
    [self.multicastDelegate addDelegate:delegate delegateQueue:dispatch_get_main_queue()];
}

- (void)removeDelegate:(id<SAMCLoginManagerDelegate>)delegate
{
    [self.multicastDelegate removeDelegate:delegate];
}

#pragma mark - NIMLoginManagerDelegate
- (void)onKick:(NIMKickReason)code clientType:(NIMLoginClientType)clientType
{
    [self.multicastDelegate onKick:code clientType:clientType];
}

- (void)onLogin:(NIMLoginStep)step
{
    [self.multicastDelegate onLogin:step];
}

- (void)onAutoLoginFailed:(NSError *)error
{
    [self.multicastDelegate onAutoLoginFailed:error];
}

- (void)onMultiLoginClientsChanged
{
    [self.multicastDelegate onMultiLoginClientsChanged];
}

#pragma mark - private
- (void)loginNetEase:(NSString *)account
               token:(NSString *)token
          completion:(void (^)(NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    [[[NIMSDK sharedSDK] loginManager] login:account
                                       token:token completion:^(NSError *error) {
      if (error == nil) {
          LoginData *sdkData = [[LoginData alloc] init];
          sdkData.account = account;
          sdkData.token = token;
          [[SAMCUserProfileManager sharedManager] setCurrentLoginData:sdkData];
          completion(nil);
      }else{
          completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
      }
    }];

}

#pragma mark - lazy load
- (GCDMulticastDelegate<SAMCLoginManagerDelegate> *)multicastDelegate
{
    if (_multicastDelegate == nil) {
        _multicastDelegate = (GCDMulticastDelegate <SAMCLoginManagerDelegate> *)[[GCDMulticastDelegate alloc] init];
    }
    return _multicastDelegate;
}

@end
