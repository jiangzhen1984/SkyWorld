//
//  SCBaseRefreshView.h
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>


UIKIT_EXTERN NSString *const kSCBaseRefreshViewObserveKeyPath;

typedef enum {
    SCArticleRefreshViewStateNormal,
    SCArticleRefreshViewStateWillRefresh,
    SCArticleRefreshViewStateRefreshing,
} SCArticleRefreshViewState;

@interface SCBaseRefreshView : UIView

@property (nonatomic, strong) UIScrollView *scrollView;

- (void)endRefreshing;

@property (nonatomic, assign) UIEdgeInsets scrollViewOriginalInsets;
@property (nonatomic, assign) SCArticleRefreshViewState refreshState;

@end
