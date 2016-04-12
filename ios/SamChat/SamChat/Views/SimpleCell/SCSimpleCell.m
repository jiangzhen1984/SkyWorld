//
//  SCSimpleCell.m
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSimpleCell.h"

@implementation SCSimpleCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self setupView];
    }
    return self;
}

- (void)setupView
{
    _iconImageView = [[UIImageView alloc] init];
    _titleLable = [[UILabel alloc] init];
    
    [self.contentView addSubview:_iconImageView];
    [self.contentView addSubview:_titleLable];
}

@end
