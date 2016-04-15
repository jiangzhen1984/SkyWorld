//
//  SCArticleTableHeaderView.m
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleTableHeaderView.h"
#import "UIView+SDAutoLayout.h"
#import "UIImageView+EMWebCache.h"

@implementation SCArticleTableHeaderView

{
    UIImageView *_backgroundImageView;
    UIImageView *_iconView;
    UILabel *_nameLabel;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self setup];
    }
    return self;
}

- (void)setup
{
    _backgroundImageView = [UIImageView new];
    _backgroundImageView.image = [UIImage imageNamed:@"pbg.jpg"];
    [self addSubview:_backgroundImageView];
    
    _iconView = [UIImageView new];
    //_iconView.image = [UIImage imageNamed:@"picon.jpg"];
    [_iconView sd_setImageWithURL:[NSURL URLWithString:[[SCUserProfileManager sharedInstance] currentLoginUserInformation].imagefile]];
    _iconView.layer.borderColor = [UIColor whiteColor].CGColor;
    _iconView.layer.borderWidth = 3;
    [self addSubview:_iconView];
    
    _nameLabel = [UILabel new];
    _nameLabel.text = [[SCUserProfileManager sharedInstance] currentLoginUserInformation].username;
    _nameLabel.textColor = [UIColor whiteColor];
    _nameLabel.textAlignment = NSTextAlignmentRight;
    _nameLabel.font = [UIFont boldSystemFontOfSize:15];
    [self addSubview:_nameLabel];
    
    
    _backgroundImageView.sd_layout.spaceToSuperView(UIEdgeInsetsMake(-60, 0, 40, 0));
    
    _iconView.sd_layout
    .widthIs(70)
    .heightIs(70)
    .rightSpaceToView(self, 15)
    .bottomSpaceToView(self, 20);
    
    
    _nameLabel.tag = 1000;
    [_nameLabel setSingleLineAutoResizeWithMaxWidth:200];
    _nameLabel.sd_layout
    .rightSpaceToView(_iconView, 20)
    .bottomSpaceToView(_iconView, -35)
    .heightIs(20);
}


@end
