//
//  SCArticleRefreshFooter.h
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCBaseRefreshView.h"

@interface SCArticleRefreshFooter : SCBaseRefreshView

+ (instancetype)refreshFooterWithRefreshingText:(NSString *)text;

- (void)addToScrollView:(UIScrollView *)scrollView refreshOpration:(void(^)())refrsh;

@property (nonatomic, strong) UILabel *indicatorLabel;
@property (nonatomic, strong) UIActivityIndicatorView *indicator;

@property (nonatomic, copy) void (^refreshBlock)();

@end
