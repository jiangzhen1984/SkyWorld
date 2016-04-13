//
//  SCHotTopicView.m
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCHotTopicView.h"

@interface SCHotTopicView ()


@end

@implementation SCHotTopicView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self _setupViews];
    }
    return self;
}

- (void)_setupViews
{
    _titleLabel = [UILabel new];
    _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _titleLabel.text = @"test";
    _titleLabel.textAlignment = NSTextAlignmentCenter;
    _titleLabel.backgroundColor = [UIColor purpleColor];
    [self addSubview:_titleLabel];
    
    _tableView = [UITableView new];
    _tableView.translatesAutoresizingMaskIntoConstraints = NO;
    _tableView.backgroundColor = [UIColor greenColor];
    [self addSubview:_tableView];
    
    _refreshHeaderView = [SCRefreshHeader refreshHeaderWithRefreshingText:@"正在刷新..."];
    _refreshHeaderView.translatesAutoresizingMaskIntoConstraints = NO;
    _refreshHeaderView.scrollView = self.tableView;
    [self addSubview:_refreshHeaderView];
    self.backgroundColor = [UIColor yellowColor];
    
    _refreshFooterView = [SCRefreshFooter refreshFooterWithRefreshingText:@"正在加载数据..."];
    [_refreshFooterView addToScrollView:self.tableView];
    
    // title label contraints
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_titleLabel]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[_titleLabel(44)]"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel)]];
    // tableview constraints
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_tableView)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[_titleLabel][_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel, _tableView)]];
}

- (void)setReloadHotTopicBlock:(void (^)())reloadHotTopicBlock
{
    _refreshHeaderView.refreshBlock = reloadHotTopicBlock;
}

- (void)setLoadMoreHotTopicBlock:(void (^)())loadMoreHotTopicBlock
{
    _refreshFooterView.refreshBlock = loadMoreHotTopicBlock;
}

- (void)endReloadRefreshing
{
    [self.refreshHeaderView endRefreshing];
}

- (void)endLoadMoreRefreshing
{
    [self.refreshFooterView endRefreshing];
}

- (void)dealloc
{
    [_refreshHeaderView removeFromSuperview];
}

@end
