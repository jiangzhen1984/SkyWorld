//
//  SCChatToolBar.h
//  SamChat
//
//  Created by HJ on 4/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EaseFaceView.h"
#import "EaseTextView.h"
#import "EaseRecordView.h"
#import "EaseChatBarMoreView.h"
#import "EaseChatToolbarItem.h"

@protocol SCChatToolbarDelegate;
@interface SCChatToolBar : UIView

@property (weak, nonatomic) id<SCChatToolbarDelegate> delegate;

@property (nonatomic) UIImage *backgroundImage;

@property (nonatomic, readonly) CGFloat inputViewMaxHeight;

@property (nonatomic, readonly) CGFloat inputViewMinHeight;

@property (nonatomic, readonly) CGFloat horizontalPadding;

@property (nonatomic, readonly) CGFloat verticalPadding;

/**
 *  用于输入文本消息的输入框
 */
@property (strong, nonatomic) EaseTextView *inputTextView;


- (instancetype)initWithFrame:(CGRect)frame;

/**
 *  初始化chat bar
 * @param horizontalPadding  default 8
 * @param verticalPadding    default 5
 * @param inputViewMinHeight default 36
 * @param inputViewMaxHeight default 150
 * @param type               default EMChatToolbarTypeGroup
 */
- (instancetype)initWithFrame:(CGRect)frame
            horizontalPadding:(CGFloat)horizontalPadding
              verticalPadding:(CGFloat)verticalPadding
           inputViewMinHeight:(CGFloat)inputViewMinHeight
           inputViewMaxHeight:(CGFloat)inputViewMaxHeight;

/**
 *  默认高度
 *
 *  @return 默认高度
 */
+ (CGFloat)defaultHeight;
@end

@protocol SCChatToolbarDelegate <NSObject>
@required
- (void)didSendText:(NSString *)text;
- (void)chatToolbarDidChangeFrameToHeight:(CGFloat)toHeight;

@end
