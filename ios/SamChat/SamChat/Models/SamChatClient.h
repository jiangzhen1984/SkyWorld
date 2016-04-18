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
- (void)signupWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode username:(NSString *)username password:(NSString *)password completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)logoutWithCompletion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion;

- (void)sendAnswer:(NSString *)answer toQuestionID:(NSString *)question_id completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)makeFollow:(BOOL)flag withUser:(NSNumber *)userID completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

- (void)publishArticleWithImages:(NSArray *)images comment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)queryArticleWithTimeFrom:(NSTimeInterval)from to:(NSTimeInterval)to count:(NSInteger)count completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)recommendArticleWithId:(NSNumber *)articleId flag:(BOOL)flag completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
- (void)commentArticleWithId:(NSNumber *)articleId comment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

- (void)upgradeToProducerWithInformationDictionary:(NSDictionary *)info completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType reset:(BOOL)flag completion:(void (^)(BOOL success, NSArray *topics, SCSkyWorldError *error))completion;

- (void)asyncWaitingPush;



@end
