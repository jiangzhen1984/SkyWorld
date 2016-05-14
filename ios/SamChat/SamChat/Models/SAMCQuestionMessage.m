//
//  SAMCQuestionMessage.m
//  SamChat
//
//  Created by HJ on 5/8/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCQuestionMessage.h"

@interface SAMCQuestionMessage ()

@property (nonatomic, assign) NSTimeInterval questionTimestamp;
@property (nonatomic, strong, readwrite) NSString *questionId;

@end

@implementation SAMCQuestionMessage

- (instancetype)initWithSendQuestion:(SendQuestion *)question
{
    self = [super init];
    if (self) {
        //self.text = question.question;
        self.text = [NSString stringWithFormat:@"我的问题：%@", question.question];
        self.from = question.senderusername;
        self.questionTimestamp = [question.sendtime doubleValue];
        self.questionId = question.question_id;
    }
    return self;
}

- (instancetype)initWithQuestionMessage:(QuestionMessage *)questionMessage
{
    self = [super init];
    if (self) {
        self.text = [NSString stringWithFormat:@"我的问题：%@", questionMessage.question];
        self.from = questionMessage.senderusername;
        self.questionTimestamp = [questionMessage.sendtime doubleValue];
        self.questionId = questionMessage.question_id;
    }
    return self;
}

- (NSTimeInterval)timestamp
{
    return self.questionTimestamp/1000; //云信的时间戳为秒，SamChat为毫秒
}

 -(NIMMessageDeliveryState)deliveryState
{
    return NIMMessageDeliveryStateDeliveried;
}

@end
