//
//  SCSkyWorldAPI.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCSkyWorldAPIMacro.h"

@interface SCSkyWorldAPI : NSObject

#pragma mark - Register JSON Protocol 
+ (NSString *)urlRegisterWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode userName:(NSString *)username passWord:(NSString *)password;

#pragma mark - Login JSON Protocol
+ (NSString *)urlLoginWithUsername:(NSString *)username passWord:(NSString *)password;

#pragma mark - Logout JSON Protocol
+ (NSString *)urlLogout;

#pragma mark - Upgrade JSON Protocol
+ (NSString *)urlUpgradeWithArea:(NSString *)area location:(NSString *)location description:(NSString *)description;

#pragma mark - Query JSON Protocol
+ (NSString *)urlQueryUser:(NSString *)username;
+ (NSString *)urlQueryUserList:(NSArray *)usernameArray;
+ (NSString *)urlQueryUserWithoutToken:(NSString *)username;

#pragma mark - Relation Query JSON Protocol


#pragma mark - Question JSON Protocol
+ (NSString *)urlNewQuestionWithQuestion: (NSString *)question;
+ (NSString *)urlCancleQuestionWithQuestionID:(NSInteger)questionID;
+ (NSString *)urlEndQuestionWithQuestionID:(NSInteger)questionID;
+ (NSString *)urlQueryQuestionWithQuestionID:(NSInteger)questionID;
+ (NSString *)urlQueryQuestionWithAskerID:(NSInteger)askerID;

#pragma mark - Answer JSON Protocol
+ (NSString *)urlAnswerQuestion:(NSInteger)questionID withAnswer:(NSString *)answer;

#pragma mark - Follow JSON Protocol
+ (NSString *)urlMakeFollow:(BOOL)flag WithUser:(NSInteger)userID bothSide:(BOOL)both;

#pragma mark - Feedback JSON Protocol
+ (NSString *)urlFeedbackWithComment:(NSString *)comment;

#pragma mark - Update UserAvatar Protocol
+ (NSString *)urlUpdateUserAvatar;


@end
