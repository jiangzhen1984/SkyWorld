//
//  SCArticleCellCommentView.m
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleCellCommentView.h"

#import "UIView+SDAutoLayout.h"
#import "SCArticleCellModel.h"
#import "MLLinkLabel.h"


@interface SCArticleCellCommentView () <MLLinkLabelDelegate>

@property (nonatomic, strong) NSArray *likeItemsArray;
@property (nonatomic, strong) NSArray *commentItemsArray;

@property (nonatomic, strong) UIImageView *bgImageView;

@property (nonatomic, strong) MLLinkLabel *likeLabel;
@property (nonatomic, strong) UIView *likeLableBottomLine;

@property (nonatomic, strong) NSMutableArray *commentLabelsArray;


@end

@implementation SCArticleCellCommentView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews
{
    _bgImageView = [UIImageView new];
    UIImage *bgImage = [[UIImage imageNamed:@"LikeCmtBg"] stretchableImageWithLeftCapWidth:40 topCapHeight:30];
    _bgImageView.image = bgImage;
    [self addSubview:_bgImageView];
    
    _likeLabel = [MLLinkLabel new];
    _likeLabel.numberOfLines = 0;
    _likeLabel.lineBreakMode = NSLineBreakByWordWrapping;
    _likeLabel.font = [UIFont systemFontOfSize:14];
    [self addSubview:_likeLabel];
    
    _likeLableBottomLine = [UIView new];
    _likeLableBottomLine.backgroundColor = SC_RGB(225, 225, 225);
    [self addSubview:_likeLableBottomLine];
    
    _bgImageView.sd_layout.spaceToSuperView(UIEdgeInsetsMake(0, 0, 0, 0));
}

- (void)setCommentItemsArray:(NSArray *)commentItemsArray
{
    _commentItemsArray = commentItemsArray;
    
    long originalLabelsCount = self.commentLabelsArray.count;
    long needsToAddCount = commentItemsArray.count > originalLabelsCount ? (commentItemsArray.count - originalLabelsCount) : 0;
    for (int i = 0; i < needsToAddCount; i++) {
        MLLinkLabel *label = [MLLinkLabel new];
        UIColor *highLightColor = SC_ARTICLE_CELL_HIGHLIGHT_COLOR;
        label.linkTextAttributes = @{NSForegroundColorAttributeName : highLightColor};
        label.font = [UIFont systemFontOfSize:14];
        label.delegate = self;
        [self addSubview:label];
        [self.commentLabelsArray addObject:label];
    }
    
    for (int i = 0; i < commentItemsArray.count; i++) {
        SCArticleCellCommentItemModel *model = commentItemsArray[i];
        MLLinkLabel *label = self.commentLabelsArray[i];
        label.attributedText = [self generateAttributedStringWithCommentItemModel:model];
    }
}

- (NSMutableArray *)commentLabelsArray
{
    if (!_commentLabelsArray) {
        _commentLabelsArray = [NSMutableArray new];
    }
    return _commentLabelsArray;
}

- (void)setupWithLikeItemsArray:(NSArray *)likeItemsArray commentItemsArray:(NSArray *)commentItemsArray
{
    self.likeItemsArray = likeItemsArray;
    self.commentItemsArray = commentItemsArray;
    
    [_likeLabel sd_clearAutoLayoutSettings];
    _likeLabel.frame = CGRectZero;
    
    if (self.commentLabelsArray.count) {
        [self.commentLabelsArray enumerateObjectsUsingBlock:^(UILabel *label, NSUInteger idx, BOOL *stop) {
            [label sd_clearAutoLayoutSettings];
            label.frame = CGRectZero;
        }];
    }
    
    CGFloat margin = 5;
    
    _likeLabel.attributedText = [self generateAttributedStringWithLikeItemArray:self.likeItemsArray];
  
    if (likeItemsArray.count) {
        _likeLabel.sd_layout
        .leftSpaceToView(self, 0)
        .rightSpaceToView(self, 0)
        .topSpaceToView(self, margin*2)
        .autoHeightRatio(0);
        
        _likeLabel.isAttributedContent = YES;
    }

    if(likeItemsArray.count && self.commentLabelsArray.count){
        _likeLableBottomLine.sd_layout
        .leftSpaceToView(self, 0)
        .rightSpaceToView(self, 0)
        .topSpaceToView(_likeLabel, margin)
        .heightIs(1);
    }
    
    UIView *lastTopView = _likeLabel;
    
    for (int i = 0; i < self.commentItemsArray.count; i++) {
        UILabel *label = (UILabel *)self.commentLabelsArray[i];
        CGFloat topMargin = i == 0 ? 10 : 5;
        label.sd_layout
        .leftSpaceToView(self, 8)
        .rightSpaceToView(self, 5)
        .topSpaceToView(lastTopView, topMargin)
        .autoHeightRatio(0);
        
        label.isAttributedContent = YES;
        lastTopView = label;
    }
    
    [self setupAutoHeightWithBottomView:lastTopView bottomMargin:6];
    
}

#pragma mark - private actions

- (NSMutableAttributedString *)generateAttributedStringWithCommentItemModel:(SCArticleCellCommentItemModel *)model
{
    NSString *text = model.firstUserName;
    if (model.secondUserName.length) {
        text = [text stringByAppendingString:[NSString stringWithFormat:@"回复%@", model.secondUserName]];
    }
    text = [text stringByAppendingString:[NSString stringWithFormat:@"：%@", model.commentString]];
    NSMutableAttributedString *attString = [[NSMutableAttributedString alloc] initWithString:text];
    UIColor *highLightColor = [UIColor blueColor];
    [attString setAttributes:@{NSForegroundColorAttributeName : highLightColor, NSLinkAttributeName : model.firstUserId} range:[text rangeOfString:model.firstUserName]];
    if (model.secondUserName) {
        [attString setAttributes:@{NSForegroundColorAttributeName : highLightColor, NSLinkAttributeName : model.secondUserId} range:[text rangeOfString:model.secondUserName]];
    }
    return attString;
}

- (NSMutableAttributedString *)generateAttributedStringWithLikeItemArray:(NSArray *)likeItemsArray
{
    SCArticleCellLikeItemModel *likeModel;
    UIColor *highLightColor = [UIColor blueColor];
    NSMutableAttributedString *attComma = [[NSMutableAttributedString alloc] initWithString:@","];
    
    NSMutableAttributedString *attString;
    attString = [[NSMutableAttributedString alloc] initWithString:@" ♡ "];
    NSString *username;
    if(likeItemsArray && (likeItemsArray.count > 0)){
        likeModel = likeItemsArray[0];
        username = likeModel.userName;
        NSMutableAttributedString *attStringTemp = [[NSMutableAttributedString alloc] initWithString:username];
        [attStringTemp setAttributes:@{NSForegroundColorAttributeName : highLightColor, NSLinkAttributeName :username}
                           range:NSMakeRange(0,username.length)];
        [attString appendAttributedString:attStringTemp];
    }
    for (int i=1; i<likeItemsArray.count; i++) {
        [attString appendAttributedString:attComma];
        likeModel = likeItemsArray[i];
        username = likeModel.userName;
        NSMutableAttributedString *attStringTemp = [[NSMutableAttributedString alloc] initWithString:username];
        [attStringTemp setAttributes:@{NSForegroundColorAttributeName : highLightColor, NSLinkAttributeName :username}
                           range:NSMakeRange(0,username.length)];
        [attString appendAttributedString:attStringTemp];
    }
    
    return attString;
}


#pragma mark - MLLinkLabelDelegate

- (void)didClickLink:(MLLink *)link linkText:(NSString *)linkText linkLabel:(MLLinkLabel *)linkLabel
{
    NSLog(@"%@", link.linkValue);
}

@end
