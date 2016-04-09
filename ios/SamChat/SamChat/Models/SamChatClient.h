//
//  SamChatClient.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCLoginModel.h"
#import "SCSignupModel.h"
#import "SCPushManager.h"

@interface SamChatClient : NSObject

@property (nonatomic, strong) SCPushManager *pushManager;

+ (instancetype)sharedInstance;

- (void)loginWithUsername:(NSString *)username password:(NSString *)password delegate:(id<SCLoginDelegate>)delegate;
- (void)signupWithUserinfoDictionary:(NSDictionary *)info delegate:(id<SCSignupDelegate, SCLoginDelegate>)delegate;

- (void)asyncWaitingPush;

@end
