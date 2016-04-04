//
//  SCPushDispatcher.h
//  SamChat
//
//  Created by HJ on 4/2/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol SCQuestionPushDelegate
- (void)didReceiveNewQuestion:(NSDictionary *)questionBody;
@end

@protocol SCAnswerPushDelegate
- (void)didReceiveNewAnswer:(NSDictionary *)answerBody;
@end

@interface SCPushDispatcher : NSObject

@property (nonatomic, weak) id<SCQuestionPushDelegate> questionPushDelegate;
@property (nonatomic, weak) id<SCAnswerPushDelegate> answerPushDelegate;

+ (instancetype)sharedInstance;
- (void)asyncWaitingPush;

@end
