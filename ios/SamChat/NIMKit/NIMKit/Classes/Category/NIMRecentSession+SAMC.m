//
//  NIMRecentSession+SAMC.m
//  NIMKit
//
//  Created by HJ on 5/9/16.
//  Copyright © 2016 NetEase. All rights reserved.
//

#import "NIMRecentSession+SAMC.h"
#import "NIMSession.h"
#import "NIMSDK.h"

@implementation NIMRecentSession (SAMC)

- (NSInteger)unreadCountOfMessagesFromView:(NSNumber *)messagesFromView
{
    if (self.unreadCount == 0) {
        return 0;
    }
    __block NSInteger count = 0;
    if (self.session.sessionType != NIMSessionTypeP2P) {
        count = self.unreadCount;
    }else{
        // 从最新的开始读取未读数量的消息，这些消息都是收到的消息，因为如果回复过，则之前的都是已读
        NSArray *messages = [[NIMSDK sharedSDK].conversationManager messagesInSession:self.session
                                                                              message:nil
                                                                                limit:self.unreadCount];
//        for (NIMMessage *message in messages) {
//            if ([[message.remoteExt valueForKey:MESSAGE_FROM_VIEW] isEqualToString:messagesFromView]) {
//                count ++;
//            }
//        }
        if (messagesFromView == nil) {
            abort(); // TODO: delete later, just for test
        }
        if (messagesFromView != nil) {
            [messages enumerateObjectsUsingBlock:^(NIMMessage *message, NSUInteger idx, BOOL * _Nonnull stop) {
                NSNumber *messageFromView = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
                if ([messageFromView isEqualToNumber:messagesFromView]) {
                    count ++;
                }else if ((messageFromView == nil) && [messagesFromView isEqualToNumber:MESSAGE_FROM_VIEW_CHAT]){
                    count ++; // count default message to type normal chat
                }
            }];
        }
    }
    return count;
}

@end
