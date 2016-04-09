//
//  SamChatHelper.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HomeViewController.h"
#import "ReceivedAnswer.h"
#import "ReceivedQuestion.h"

@protocol SCQuestionPushDelegate
- (void)didReceiveNewQuestion:(NSDictionary *)questionBody;
@end

@protocol SCAnswerPushDelegate
- (void)didReceiveNewAnswer:(ReceivedAnswer *)answerBody;
@end

@interface SamChatHelper : NSObject <SCPushDelegate>

@property (nonatomic, weak) id<SCQuestionPushDelegate> questionPushDelegate;
@property (nonatomic, weak) id<SCAnswerPushDelegate> answerPushDelegate;

@property (nonatomic, weak) HomeViewController *mainVC;

+ (instancetype)sharedInstance;
- (void)asyncPush;

@end
