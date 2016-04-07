//
//  SCBubbleView.h
//  SamChat
//
//  Created by HJ on 4/7/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SCBubbleView : UIView

@property (nonatomic) BOOL isSender;
@property (nonatomic, strong) UIImageView *backgroundImageView;
@property (strong, nonatomic) UILabel *textLabel;

- (instancetype)initWithMargin:(UIEdgeInsets)margin
                      isSender:(BOOL)isSender;

- (void)updateTextMargin:(UIEdgeInsets)margin;

@end