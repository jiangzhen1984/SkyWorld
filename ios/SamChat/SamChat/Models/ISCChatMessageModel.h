//
//  ISCChatMessageModel.h
//  SamChat
//
//  Created by HJ on 4/7/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
#import <Foundation/Foundation.h>

typedef enum{
    SCChatMessageStatusSending  = 0,
    SCChatMessageStatusSucceed,
    SCChatMessageStatusFailed,
}SCChatMessageStatus;

@protocol ISCChatMessageModel <NSObject>

//缓存数据模型对应的cell的高度，只需要计算一次并赋值，以后就无需计算了
@property (nonatomic) CGFloat i_cellHeight;
@property (nonatomic, readonly) SCChatMessageStatus i_messageStatus;
@property (nonatomic, readonly) BOOL i_isSender;
@property (strong, nonatomic, readonly) NSString *i_avatarURLPath;
@property (strong, nonatomic, readonly) UIImage *i_avatarImage;
@property (strong, nonatomic, readonly) NSString *i_text;

@end