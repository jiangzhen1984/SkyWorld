//
//  SCArticleRefreshHeader.h
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCBaseRefreshView.h"

@interface SCArticleRefreshHeader : SCBaseRefreshView

+ (instancetype)refreshHeaderWithCenter:(CGPoint)center;

@property (nonatomic, copy) void(^refreshingBlock)();

@end

