//
//  SCRefreshHeader.m
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCRefreshHeader.h"
#import "UIView+SDAutoLayout.h"

#define kSCRefreshHeaderHeight 44

@implementation SCRefreshHeader

+ (instancetype)refreshHeaderWithRefreshingText:(NSString *)text
{
    SCRefreshHeader *footer = [SCRefreshHeader new];
    footer.indicatorLabel.text = text;
    return footer;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)addToScrollView:(UIScrollView *)scrollView
{
    self.scrollView = scrollView;
}

- (void)setupView
{
    UIView *containerView = [UIView new];
    [self addSubview:containerView];
    
    _indicatorLabel = [UILabel new];
    _indicatorLabel.textColor = [UIColor lightGrayColor];
    _indicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    [_indicator startAnimating];
    
    [containerView sd_addSubviews:@[self.indicatorLabel, self.indicator]];
    
    containerView.sd_layout
    .heightIs(20)
    .centerYEqualToView(self)
    .centerXEqualToView(self);
    [containerView setupAutoWidthWithRightView:self.indicatorLabel rightMargin:0]; // 宽度自适应
    
    self.indicator.sd_layout
    .leftEqualToView(containerView)
    .topEqualToView(containerView); // ActivityIndicatorView 宽高固定不用约束
    
    self.indicatorLabel.sd_layout
    .leftSpaceToView(self.indicator, 5)
    .topEqualToView(containerView)
    .bottomEqualToView(containerView);
    [self.indicatorLabel setSingleLineAutoResizeWithMaxWidth:250]; // label宽度自适应
}

- (void)setScrollView:(UIScrollView *)scrollView
{
    [super setScrollView:scrollView];
    
    [scrollView addSubview:self];
    self.hidden = YES;
}

- (void)endRefreshing
{
    [super endRefreshing];
    
    [UIView animateWithDuration:0.2 animations:^{
        self.scrollView.contentInset = self.scrollViewOriginalInsets;
    }];
}

- (void)setRefreshState:(SCArticleRefreshViewState)refreshState
{
    [super setRefreshState:refreshState];
    
    if (refreshState == SCArticleRefreshViewStateRefreshing) {
        if (self.refreshBlock) {
            self.refreshBlock();
        }
    }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSString *,id> *)change context:(void *)context
{
    if (keyPath != kSCBaseRefreshViewObserveKeyPath) return;
    if((self.scrollView.contentOffset.y < 0) && (self.refreshState != SCArticleRefreshViewStateRefreshing)){
//        self.hidden = NO;
        if(self.scrollView.contentOffset.y<(-kSCRefreshHeaderHeight)){
            self.hidden = NO;
            self.frame = CGRectMake(0, self.scrollView.frame.origin.y, self.scrollView.width, kSCRefreshHeaderHeight);
            self.refreshState = SCArticleRefreshViewStateRefreshing;
        }else{
            
        }
    }else if(self.refreshState == SCArticleRefreshViewStateNormal){
        self.hidden = YES;
    }
}


@end

