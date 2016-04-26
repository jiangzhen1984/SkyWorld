//
//  IConversationModel.h
//  ChatDemo-UI3.0
//
//  Created by dhc on 15/6/25.
//  Copyright (c) 2015年 easemob.com. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@class EMConversation;
@protocol IConversationModel <NSObject>

@property (strong, nonatomic, readonly) EMConversation *conversation;
@property (strong, nonatomic) NSString *title;
@property (strong, nonatomic) NSString *avatarURLPath;
@property (strong, nonatomic) UIImage *avatarImage;

//SAMC_BEGIN
@property (strong, nonatomic) NSString *messagesFromView;
//SAMC_END

- (instancetype)initWithConversation:(EMConversation *)conversation;

@end
