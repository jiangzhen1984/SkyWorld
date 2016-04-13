//
//  SCReceivedAnswerView.m
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCReceivedAnswerView.h"

@implementation SCReceivedAnswerView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self _setupViews];
    }
    return self;
}

- (void)_setupViews
{
    _tableView = [UITableView new];
    _tableView.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:_tableView];
    
    _footLabel = [UILabel new];
    _footLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _footLabel.text = @"更多答案即将到来...";
    _footLabel.textAlignment = NSTextAlignmentCenter;
    [self addSubview:_footLabel];
    
    // tableview constraints
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_tableView]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_tableView)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[_tableView][_footLabel]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_tableView, _footLabel)]];
    // footLabel constraints
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_footLabel]|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_footLabel)]];
}


@end
