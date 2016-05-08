//
//  EMConversation+MessagesCount.m
//  SamChat
//
//  Created by HJ on 4/25/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "EMConversation+MessagesCount.h"

@implementation EMConversation (MessagesCount)

- (int)messagesCountOfConversationFromView:(NSString *)messgeFromView
{
    int unreadCount = 0;
    // 从最新的开始读取未读数量的消息，这些消息都是收到的消息，因为如果回复过，则之前的都是已读
    NSArray *messages = [self loadMoreMessagesFromId:nil limit:self.unreadMessagesCount];
    for (EMMessage *message in messages) {
        if ([[message.ext valueForKey:MESSAGE_FROM_VIEW] isEqualToString:messgeFromView]) {
            unreadCount ++;
        }
    }
    return unreadCount;
}

@end
