//
//  SCChatMessageCell.h
//  SamChat
//
//  Created by HJ on 4/7/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SCBubbleView.h"
#import "ISCChatMessageModel.h"

#define SCCHAT_MESSAGE_IMAGESIZE_WIDTH  120
#define SCCHAT_MESSAGE_IMAGESIZE_HEIGHT 120

@protocol SCChatMessageCellDelegate;
@interface SCChatMessageCell : UITableViewCell

@property (weak, nonatomic) id<SCChatMessageCellDelegate> delegate;
@property (nonatomic, strong) UIActivityIndicatorView *activity;
@property (strong, nonatomic) UIImageView *avatarView;
@property (strong, nonatomic) UILabel *nameLabel;
@property (strong, nonatomic) UIButton *statusButton;
@property (strong, nonatomic) SCBubbleView *bubbleView;
@property (strong, nonatomic) id<ISCChatMessageModel> model;


@property (nonatomic) CGFloat statusSize UI_APPEARANCE_SELECTOR; //default 20;
@property (nonatomic) CGFloat activitySize UI_APPEARANCE_SELECTOR; //default 20;


@property (nonatomic) CGFloat bubbleMaxWidth UI_APPEARANCE_SELECTOR; //default 200;
@property (nonatomic) UIEdgeInsets bubbleMargin UI_APPEARANCE_SELECTOR; //default UIEdgeInsetsMake(8, 0, 8, 0);


@property (nonatomic) UIEdgeInsets leftBubbleMargin UI_APPEARANCE_SELECTOR; //default UIEdgeInsetsMake(8, 15, 8, 10);
@property (nonatomic) UIEdgeInsets rightBubbleMargin UI_APPEARANCE_SELECTOR; //default UIEdgeInsetsMake(8, 10, 8, 15);


@property (strong, nonatomic) UIImage *sendBubbleBackgroundImage UI_APPEARANCE_SELECTOR;
@property (strong, nonatomic) UIImage *recvBubbleBackgroundImage UI_APPEARANCE_SELECTOR;


@property (nonatomic) UIFont *messageTextFont UI_APPEARANCE_SELECTOR; //default [UIFont systemFontOfSize:15];
@property (nonatomic) UIColor *messageTextColor UI_APPEARANCE_SELECTOR; //default [UIColor blackColor];


- (instancetype)initWithStyle:(UITableViewCellStyle)style
              reuseIdentifier:(NSString *)reuseIdentifier
                        model:(id<ISCChatMessageModel>)model;

+ (NSString *)cellIdentifierWithModel:(id<ISCChatMessageModel>)model;

+ (CGFloat)cellHeightWithModel:(id<ISCChatMessageModel>)model;

@end


@protocol SCChatMessageCellDelegate <NSObject>

@optional

- (void)messageCellSelected:(id<ISCChatMessageModel>)model;

- (void)statusButtonSelcted:(id<ISCChatMessageModel>)model withMessageCell:(SCChatMessageCell*)messageCell;

- (void)avatarViewSelcted:(id<ISCChatMessageModel>)model;

@end