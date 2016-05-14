//
//  SAMCAccountManager.h
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol SAMCLoginManagerDelegate <NSObject>

@optional

- (void)onKick:(NIMKickReason)code clientType:(NIMLoginClientType)clientType;

- (void)onLogin:(NIMLoginStep)step;

- (void)onAutoLoginFailed:(NSError *)error;

- (void)onMultiLoginClientsChanged;

@end

@interface SAMCAccountManager : NSObject

- (void)signup:(NSString *)account
      password:(NSString *)password
     cellphone:(NSString *)cellphone
   countryCode:(NSNumber *)countrycode
    completion:(void (^)(NSError *error))completion;

- (void)login:(NSString *)account
     password:(NSString *)password
   completion:(void (^)(NSError *error))completion;

- (void)autoLogin:(NSString *)account
            token:(NSString *)token;

- (void)logout:(void (^)(NSError *error))completion;

- (void)kickOtherClient:(NIMLoginClient *)client
             completion:(NIMLoginHandler)completion;

- (NSString *)currentAccount;

- (BOOL)isLogined;

- (NSArray *)currentLoginClients;

- (void)addDelegate:(id<SAMCLoginManagerDelegate>)delegate;

- (void)removeDelegate:(id<SAMCLoginManagerDelegate>)delegate;

@end
