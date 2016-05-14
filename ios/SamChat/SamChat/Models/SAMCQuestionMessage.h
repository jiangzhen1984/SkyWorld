//
//  SAMCQuestionMessage.h
//  SamChat
//
//  Created by HJ on 5/8/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "NIMMessage.h"
#import "SendQuestion.h"
#import "QuestionMessage.h"

@interface SAMCQuestionMessage : NIMMessage

@property (nonatomic, strong) NIMMessage *firstNIMMessage;
@property (nonatomic, strong, readonly) NSString *questionId;
- (instancetype)initWithSendQuestion:(SendQuestion *)question;
- (instancetype)initWithQuestionMessage:(QuestionMessage *)questionMessage;

@end
