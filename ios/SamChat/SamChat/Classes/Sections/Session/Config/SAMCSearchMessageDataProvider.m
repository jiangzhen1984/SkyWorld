//
//  SAMCSearchMessageDataProvider.m
//  SamChat
//
//  Created by HJ on 5/8/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSearchMessageDataProvider.h"
#import "SAMCQuestionMessage.h"
#import "SamChatClient.h"

@interface SAMCSearchMessageDataProvider ()

@property (nonatomic,strong) NIMSession *session;;

@end

@implementation SAMCSearchMessageDataProvider

- (instancetype)initWithSession:(NIMSession *)session
{
    self = [super init];
    if (self) {
        _session = session;
    }
    return self;
}

- (void)pullDown:(NIMMessage *)firstMessage handler:(NIMKitDataProvideHandler)handler
{
    NIMHistoryMessageSearchOption *option = [[NIMHistoryMessageSearchOption alloc] init];
    option.startTime = firstMessage.timestamp;
    option.limit = 10;
    
    NSInteger limit = 20;
    
    // 设置这个为上一次的
    NIMMessage *firstNtesMessage = firstMessage;
    if ((firstMessage!=nil) && [firstMessage isMemberOfClass:[SAMCQuestionMessage class]]) {
        firstNtesMessage = ((SAMCQuestionMessage *)firstMessage).firstNIMMessage;
    }
    
    NSArray<NIMMessage *> *ntesMessages = [[[NIMSDK sharedSDK] conversationManager] messagesInSession:self.session
                                                                                                 message:firstNtesMessage
                                                                                                   limit:limit];
    NSArray<SAMCQuestionMessage *> *questionMessages = [[SamChatClient sharedClient].searchManager messagesFromQuestionMessageWithTimeFrom:[NSNumber numberWithFloat:firstMessage.timestamp]
                                                                                  limit:limit
                                                                                session:self.session];
    
    // 两个数组里都已经按时间从小到大排列
    // 现在需要做的时取两个数组合并后时间最大的count个
    // 并且为了刷新时候的处理，需要设置question message的firstNIMMessage为最近的NIMMessage
    // 方便下次刷新获取更多的时候可以继续使用这个firstNIMMessage进行数据库查询
    NSMutableArray *mergeMessages = [[NSMutableArray alloc] init];
    int ntesIndex = (int)[ntesMessages count] - 1;
    int questionIndex = (int)[questionMessages count] - 1;
    while ((ntesIndex>=0) && (questionIndex>=0)) {
        if ([mergeMessages count] >= limit) {
            // 合并足够数量就退出
            break;
        }
        NIMMessage *ntesMessage = ntesMessages[ntesIndex];
        SAMCQuestionMessage *questionMessage = questionMessages[questionIndex];
        if (ntesMessage.timestamp > questionMessage.timestamp) {
            [mergeMessages insertObject:ntesMessage atIndex:0];
            firstNtesMessage = ntesMessage;
            ntesIndex--;
        }else{
            [mergeMessages insertObject:questionMessage atIndex:0];
            questionIndex--;
        }
    }
    
    while (ntesIndex>=0) {
        if ([mergeMessages count] >= limit) {
            break;
        }
        NIMMessage *ntesMessage = ntesMessages[ntesIndex];
        [mergeMessages insertObject:ntesMessage atIndex:0];
        firstNtesMessage = ntesMessage;
        ntesIndex--;
    }
    
    while (questionIndex>=0) {
        if ([mergeMessages count] >= limit) {
            break;
        }
        SAMCQuestionMessage *questionMessage = questionMessages[questionIndex];
        [mergeMessages insertObject:questionMessage atIndex:0];
        questionIndex--;
    }
    
    // 如果第一个是SAMCQuestionMessage，则记录下firstNtesMessage，便于刷新历史时候使用
    if ([mergeMessages count] > 0) {
        id message = [mergeMessages firstObject];
        if ([message isMemberOfClass:[SAMCQuestionMessage class]]) {
            ((SAMCQuestionMessage *)message).firstNIMMessage = firstNtesMessage;
        }
    }
 
    if (handler) {
        handler(nil, mergeMessages);
    }
}

@end
