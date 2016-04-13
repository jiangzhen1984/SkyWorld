//
//  SCRefreshHeader.h
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCBaseRefreshView.h"

@interface SCRefreshHeader : SCBaseRefreshView

+ (instancetype)refreshHeaderWithRefreshingText:(NSString *)text;

- (void)addToScrollView:(UIScrollView *)scrollView;

@property (nonatomic, strong) UILabel *indicatorLabel;
@property (nonatomic, strong) UIActivityIndicatorView *indicator;

@property (nonatomic, copy) void (^refreshBlock)();

@end