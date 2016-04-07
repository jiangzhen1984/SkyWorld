//
//  SCChatToolBar.m
//  SamChat
//
//  Created by HJ on 4/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCChatToolBar.h"

@interface SCChatToolBar () <UITextViewDelegate>

@property (nonatomic) CGFloat version;

@property (strong, nonatomic) UIImageView *toolbarBackgroundImageView;
@property (strong, nonatomic) UIImageView *backgroundImageView;

@property (strong, nonatomic) UIView *toolbarView;
@property (strong, nonatomic) UIButton *sendButton;

@property (nonatomic) CGFloat previousTextViewContentHeight;//上一次inputTextView的contentSize.height
@property (nonatomic) NSLayoutConstraint *inputViewWidthItemsLeftConstraint;
@property (nonatomic) NSLayoutConstraint *inputViewWidthoutItemsLeftConstraint;

@end

@implementation SCChatToolBar

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [self initWithFrame:frame horizontalPadding:8 verticalPadding:5 inputViewMinHeight:36 inputViewMaxHeight:150];
    if (self) {
        
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame
            horizontalPadding:(CGFloat)horizontalPadding
              verticalPadding:(CGFloat)verticalPadding
           inputViewMinHeight:(CGFloat)inputViewMinHeight
           inputViewMaxHeight:(CGFloat)inputViewMaxHeight
{
    if (frame.size.height < (verticalPadding * 2 + inputViewMinHeight)) {
        frame.size.height = verticalPadding * 2 + inputViewMinHeight;
    }
    self = [super initWithFrame:frame];
    if (self) {
        _horizontalPadding = horizontalPadding;
        _verticalPadding = verticalPadding;
        _inputViewMinHeight = inputViewMinHeight;
        _inputViewMaxHeight = inputViewMaxHeight;
        
        _version = [[[UIDevice currentDevice] systemVersion] floatValue];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(chatKeyboardWillChangeFrame:) name:UIKeyboardWillChangeFrameNotification object:nil];
        
        [self _setupSubviews];
    }
    return self;
}

#pragma mark - setup subviews

- (void)_setupSubviews
{
//    //backgroundImageView
//    _backgroundImageView = [[UIImageView alloc] initWithFrame:self.bounds];
//    _backgroundImageView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
//    _backgroundImageView.backgroundColor = [UIColor clearColor];
//    _backgroundImageView.image = [[UIImage imageNamed:@"EaseUIResource.bundle/messageToolbarBg"] stretchableImageWithLeftCapWidth:0.5 topCapHeight:10];
//    [self addSubview:_backgroundImageView];
    
    //toolbar
    _toolbarView = [[UIView alloc] initWithFrame:self.bounds];
    _toolbarView.backgroundColor = [UIColor whiteColor];
    [self addSubview:_toolbarView];
    
//    _toolbarBackgroundImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, _toolbarView.frame.size.width, _toolbarView.frame.size.height)];
//    _toolbarBackgroundImageView.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
//    _toolbarBackgroundImageView.backgroundColor = [UIColor clearColor];
//    [_toolbarView addSubview:_toolbarBackgroundImageView];
    

    _inputTextView = [[EaseTextView alloc] initWithFrame:CGRectMake(self.horizontalPadding, self.verticalPadding, self.frame.size.width - self.verticalPadding * 2, self.frame.size.height - self.verticalPadding * 2)];
    _inputTextView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
    _inputTextView.scrollEnabled = YES;
    _inputTextView.returnKeyType = UIReturnKeyNext;
    _inputTextView.enablesReturnKeyAutomatically = YES; // UITextView内部判断send按钮是否可以用
    _inputTextView.placeHolder = @"请输入回复";
    _inputTextView.delegate = self;
    _inputTextView.backgroundColor = [UIColor clearColor];
    _inputTextView.layer.borderColor = [UIColor colorWithWhite:0.8f alpha:1.0f].CGColor;
    _inputTextView.layer.borderWidth = 0.65f;
    _inputTextView.layer.cornerRadius = 6.0f;
    _previousTextViewContentHeight = [self _getTextViewContentH:_inputTextView];
    [_toolbarView addSubview:_inputTextView];
    
    self.sendButton = [[UIButton alloc] init];
    self.sendButton.autoresizingMask = UIViewAutoresizingFlexibleTopMargin;
    self.sendButton.backgroundColor = [UIColor lightGrayColor];
    [self.sendButton setTitle:@"回答" forState:UIControlStateNormal];
    [self.sendButton addTarget:self action:@selector(sendButtonAction:) forControlEvents:UIControlEventTouchUpInside];
    
    CGFloat itemHeight = self.toolbarView.frame.size.height - self.verticalPadding * 2;
    CGRect itemFrame = self.sendButton.frame;
    if (itemFrame.size.height == 0) {
        itemFrame.size.height = itemHeight;
    }
    if (itemFrame.size.width == 0) {
        itemFrame.size.width = itemFrame.size.height;
    }
    itemFrame.size.width += 10; // inset padding
    self.sendButton.frame = itemFrame;
    CGFloat x = self.toolbarView.frame.size.width - self.horizontalPadding;
    x -= itemFrame.size.width;
    itemFrame.origin.x = x;
    itemFrame.origin.y = (self.toolbarView.frame.size.height - itemFrame.size.height) / 2;
    self.sendButton.frame = itemFrame;
    x -= self.horizontalPadding;
    
    [self.toolbarView addSubview:self.sendButton];
    CGRect inputFrame = self.inputTextView.frame;
    CGFloat value = x - CGRectGetMaxX(inputFrame);
    inputFrame.size.width += value;
    self.inputTextView.frame = inputFrame;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillChangeFrameNotification object:nil];
    
    _delegate = nil;
    _inputTextView.delegate = nil;
    _inputTextView = nil;
}

#pragma mark - setter

- (void)setDelegate:(id<SCChatToolbarDelegate>)delegate
{
    _delegate = delegate;
}

#pragma mark - private input view

- (CGFloat)_getTextViewContentH:(UITextView *)textView
{
    if (self.version >= 7.0)
    {
        return ceilf([textView sizeThatFits:textView.frame.size].height);
    } else {
        return textView.contentSize.height;
    }
}

- (void)_willShowInputTextViewToHeight:(CGFloat)toHeight
{
    if (toHeight < self.inputViewMinHeight) {
        toHeight = self.inputViewMinHeight;
    }
    if (toHeight > self.inputViewMaxHeight) {
        toHeight = self.inputViewMaxHeight;
    }
    
    if (toHeight == _previousTextViewContentHeight)
    {
        return;
    }
    else{
        CGFloat changeHeight = toHeight - _previousTextViewContentHeight;
        
        CGRect rect = self.frame;
        rect.size.height += changeHeight;
        rect.origin.y -= changeHeight;
        self.frame = rect;
        
        rect = self.toolbarView.frame;
        rect.size.height += changeHeight;
        self.toolbarView.frame = rect;
        
        if (self.version < 7.0) {
            [self.inputTextView setContentOffset:CGPointMake(0.0f, (self.inputTextView.contentSize.height - self.inputTextView.frame.size.height) / 2) animated:YES];
        }
        _previousTextViewContentHeight = toHeight;
        
        if (_delegate && [_delegate respondsToSelector:@selector(chatToolbarDidChangeFrameToHeight:)]) {
            [_delegate chatToolbarDidChangeFrameToHeight:self.frame.size.height];
        }
    }
}

#pragma mark - private bottom view
- (void)_willShowBottomHeight:(CGFloat)bottomHeight
{
    CGRect fromFrame = self.frame;
    CGFloat toHeight = self.toolbarView.frame.size.height + bottomHeight;
    CGRect toFrame = CGRectMake(fromFrame.origin.x, fromFrame.origin.y + (fromFrame.size.height - toHeight), fromFrame.size.width, toHeight);
    
    //如果需要将所有扩展页面都隐藏，而此时已经隐藏了所有扩展页面，则不进行任何操作
    if(bottomHeight == 0 && self.frame.size.height == self.toolbarView.frame.size.height)
    {
        return;
    }
    
    self.frame = toFrame;
    
    if (_delegate && [_delegate respondsToSelector:@selector(chatToolbarDidChangeFrameToHeight:)]) {
        [_delegate chatToolbarDidChangeFrameToHeight:toHeight];
    }
}

- (void)_willShowKeyboardFromFrame:(CGRect)beginFrame toFrame:(CGRect)toFrame
{
    if(beginFrame.origin.y < toFrame.origin.y){
        [self _willShowBottomHeight:0];
    }else{
        [self _willShowBottomHeight:toFrame.size.height];
    }
}

#pragma mark - UITextViewDelegate
//- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
//{
//    if ([self.delegate respondsToSelector:@selector(inputTextViewWillBeginEditing:)]) {
//        [self.delegate inputTextViewWillBeginEditing:self.inputTextView];
//    }
//    return YES;
//}
//
//- (void)textViewDidBeginEditing:(UITextView *)textView
//{
//    [textView becomeFirstResponder];
//    
//    if ([self.delegate respondsToSelector:@selector(inputTextViewDidBeginEditing:)]) {
//        [self.delegate inputTextViewDidBeginEditing:self.inputTextView];
//    }
//}

//- (void)textViewDidEndEditing:(UITextView *)textView
//{
//    [textView resignFirstResponder];
//}

//- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
//{
//    if ([text isEqualToString:@"\n"]) {
//        if ([self.delegate respondsToSelector:@selector(didSendText:)]) {
//            [self.delegate didSendText:textView.text];
//            self.inputTextView.text = @"";
//            [self _willShowInputTextViewToHeight:[self _getTextViewContentH:self.inputTextView]];;
//        }
//        
//        return NO;
//    }
//    return YES;
//}

- (void)textViewDidChange:(UITextView *)textView
{
    [self _willShowInputTextViewToHeight:[self _getTextViewContentH:textView]];
}

#pragma mark - UIKeyboardNotification
- (void)chatKeyboardWillChangeFrame:(NSNotification *)notification
{
    NSDictionary *userInfo = notification.userInfo;
    CGRect endFrame = [userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGRect beginFrame = [userInfo[UIKeyboardFrameBeginUserInfoKey] CGRectValue];
    CGFloat duration = [userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    UIViewAnimationCurve curve = [userInfo[UIKeyboardAnimationCurveUserInfoKey] integerValue];
    
    void(^animations)() = ^{
        [self _willShowKeyboardFromFrame:beginFrame toFrame:endFrame];
    };
    
    [UIView animateWithDuration:duration delay:0.0f options:(curve << 16 | UIViewAnimationOptionBeginFromCurrentState) animations:animations completion:nil];
}

#pragma mark - action
- (void)sendButtonAction:(id)sender
{
    if ([self.delegate respondsToSelector:@selector(didSendText:)]) {
        [self.delegate didSendText:self.inputTextView.text];
        self.inputTextView.text = @"";
        [self _willShowInputTextViewToHeight:[self _getTextViewContentH:self.inputTextView]];;
    }
    [self.inputTextView resignFirstResponder];
}


#pragma mark - public
+ (CGFloat)defaultHeight
{
    return 5 * 2 + 36;
}


@end
