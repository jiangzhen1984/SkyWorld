//
//  SCBaseRefreshView.m
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCBaseRefreshView.h"

NSString *const kSCBaseRefreshViewObserveKeyPath = @"contentOffset";

@implementation SCBaseRefreshView

- (void)setScrollView:(UIScrollView *)scrollView
{
    _scrollView = scrollView;
    
    [scrollView addObserver:self forKeyPath:kSCBaseRefreshViewObserveKeyPath options:NSKeyValueObservingOptionNew context:nil];
}

- (void)willMoveToSuperview:(UIView *)newSuperview
{
    if (!newSuperview) {
        [self.scrollView removeObserver:self forKeyPath:kSCBaseRefreshViewObserveKeyPath];
    }
}

- (void)endRefreshing
{
    self.refreshState = SCArticleRefreshViewStateNormal;
}


- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSString *,id> *)change context:(void *)context
{
    // 子类实现
}

@end