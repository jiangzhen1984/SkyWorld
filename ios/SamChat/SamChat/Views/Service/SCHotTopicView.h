//
//  SCHotTopicView.h
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SCRefreshFooter.h"
#import "SCRefreshHeader.h"

@interface SCHotTopicView : UIView

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) SCRefreshHeader *refreshHeaderView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) SCRefreshFooter *refreshFooterView;

@property (nonatomic, copy) void(^reloadHotTopicBlock)();
@property (nonatomic, copy) void(^loadMoreHotTopicBlock)();

- (void)endReloadRefreshing;
- (void)endLoadMoreRefreshing;

@end
