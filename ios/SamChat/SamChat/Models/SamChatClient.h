//
//  SamChatClient.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SCPushManager.h"

@interface SamChatClient : NSObject

@property (nonatomic, strong) SCPushManager *pushManager;

+ (instancetype)sharedInstance;

- (void)loginWithUsername:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)signupWithUserinfoDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)logoutWithCompletion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)sendAnswer:(NSString *)answer toQuestionID:(NSInteger)question_id completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;


- (void)asyncWaitingPush;



@end
