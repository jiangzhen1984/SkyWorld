//
//  SCTableViewCell.m
//  SamChat
//
//  Created by HJ on 4/3/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCTableViewCell.h"

const CGFloat SCTableCellPadding = 10;

@interface SCTableViewCell ()


@end

@implementation SCTableViewCell

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self _setupSubviews];
    }
    return self;
}

#pragma mark - Layout Subviews
- (void)_setupSubviews
{
    //self.contentView.backgroundColor = [UIColor lightGrayColor];
    _avatarView = [[UIImageView alloc] init];
    _avatarView.translatesAutoresizingMaskIntoConstraints = NO;
    _avatarView.backgroundColor = [UIColor yellowColor];
    [self.contentView addSubview:_avatarView];
    
    _titleLabel = [[UILabel alloc] init];
    _titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _titleLabel.numberOfLines = 1;
    _titleLabel.backgroundColor = [UIColor clearColor];
    _titleLabel.font = [UIFont boldSystemFontOfSize:17];
    _titleLabel.textColor = [UIColor blackColor];
    [self.contentView addSubview:_titleLabel];
    //_titleLabel.backgroundColor = [UIColor greenColor];
    
    _timeLabel = [[UILabel alloc] init];
    _timeLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _timeLabel.font = [UIFont systemFontOfSize:8];
    _timeLabel.textColor = [UIColor grayColor];
    _timeLabel.textAlignment = NSTextAlignmentRight;
    _timeLabel.backgroundColor = [UIColor clearColor];
    [self.contentView addSubview:_timeLabel];
    //_timeLabel.backgroundColor = [UIColor blueColor];
    
    _detailLabel = [[UILabel alloc] init];
    _detailLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _detailLabel.backgroundColor = [UIColor clearColor];
    _detailLabel.font = [UIFont systemFontOfSize:16];
    _detailLabel.textColor = [UIColor grayColor];
    [self.contentView addSubview:_detailLabel];
    //_detailLabel.backgroundColor = [UIColor redColor];
    
    [self _setupSubViewsContraints];
}

#pragma mark - Setup Constraints
- (void)_setupSubViewsContraints
{
    [self addConstraint:[NSLayoutConstraint constraintWithItem:self.avatarView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.avatarView attribute:NSLayoutAttributeHeight multiplier:1.0 constant:0]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-10-[_avatarView]-10-|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_avatarView)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-10-[_titleLabel]-5-[_detailLabel]-10-|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_titleLabel,_detailLabel)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-10-[_timeLabel]-5-[_detailLabel]-10-|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_timeLabel,_detailLabel)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-20-[_avatarView]-10-[_titleLabel]-10-[_timeLabel]-20-|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_avatarView,_titleLabel,_timeLabel)]];
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:[_avatarView]-10-[_detailLabel]-20-|"
                                                                 options:0
                                                                 metrics:nil
                                                                   views:NSDictionaryOfVariableBindings(_avatarView,_detailLabel)]];
}

#pragma mark - setter
- (void)setModel:(id<ISCTableCellModel>)model
{
    _model = model;
    
    self.titleLabel.text = ([_model.i_title length]>0)?_model.i_title:@"";
    self.detailLabel.text = ([_model.i_details length]>0)?_model.i_details:@"";
    self.timeLabel.text = ([_model.i_time length]>0)?_model.i_time:@"";
}

@end


//#import "EaseConversationCell.h"
//
//#import "EMConversation.h"
//#import "UIImageView+EMWebCache.h"
//
//CGFloat const EaseConversationCellPadding = 10;
//
//@interface EaseConversationCell()
//
//@property (nonatomic) NSLayoutConstraint *titleWithAvatarLeftConstraint;
//
//@property (nonatomic) NSLayoutConstraint *titleWithoutAvatarLeftConstraint;
//
//@property (nonatomic) NSLayoutConstraint *detailWithAvatarLeftConstraint;
//
//@property (nonatomic) NSLayoutConstraint *detailWithoutAvatarLeftConstraint;
//
//@end
//
//@implementation EaseConversationCell
//
//+ (void)initialize
//{
//    // UIAppearance Proxy Defaults
//    EaseConversationCell *cell = [self appearance];
//    cell.titleLabelColor = [UIColor blackColor];
//    cell.titleLabelFont = [UIFont systemFontOfSize:17];
//    cell.detailLabelColor = [UIColor lightGrayColor];
//    cell.detailLabelFont = [UIFont systemFontOfSize:15];
//    cell.timeLabelColor = [UIColor blackColor];
//    cell.timeLabelFont = [UIFont systemFontOfSize:13];
//}
//
//#pragma mark - setter
//
//
//- (void)setModel:(id<IConversationModel>)model
//{
//    _model = model;
//    
//    if ([_model.title length] > 0) {
//        self.titleLabel.text = _model.title;
//    }
//    else{
//        self.titleLabel.text = _model.conversation.conversationId;
//    }
//    
//    if (self.showAvatar) {
//        if ([_model.avatarURLPath length] > 0){
//            [self.avatarView.imageView sd_setImageWithURL:[NSURL URLWithString:_model.avatarURLPath] placeholderImage:_model.avatarImage];
//        } else {
//            if (_model.avatarImage) {
//                self.avatarView.image = _model.avatarImage;
//            }
//        }
//    }
//    
//    if (_model.conversation.unreadMessagesCount == 0) {
//        _avatarView.showBadge = NO;
//    }
//    else{
//        _avatarView.showBadge = YES;
//        _avatarView.badge = _model.conversation.unreadMessagesCount;
//    }
//}
//
//- (void)setTitleLabelFont:(UIFont *)titleLabelFont
//{
//    _titleLabelFont = titleLabelFont;
//    _titleLabel.font = _titleLabelFont;
//}
//
//- (void)setTitleLabelColor:(UIColor *)titleLabelColor
//{
//    _titleLabelColor = titleLabelColor;
//    _titleLabel.textColor = _titleLabelColor;
//}
//
//- (void)setDetailLabelFont:(UIFont *)detailLabelFont
//{
//    _detailLabelFont = detailLabelFont;
//    _detailLabel.font = _detailLabelFont;
//}
//
//- (void)setDetailLabelColor:(UIColor *)detailLabelColor
//{
//    _detailLabelColor = detailLabelColor;
//    _detailLabel.textColor = _detailLabelColor;
//}
//
//- (void)setTimeLabelFont:(UIFont *)timeLabelFont
//{
//    _timeLabelFont = timeLabelFont;
//    _timeLabel.font = _timeLabelFont;
//}
//
//- (void)setTimeLabelColor:(UIColor *)timeLabelColor
//{
//    _timeLabelColor = timeLabelColor;
//    _timeLabel.textColor = _timeLabelColor;
//}
//
//#pragma mark - class method
//
//+ (NSString *)cellIdentifierWithModel:(id)model
//{
//    return @"EaseConversationCell";
//}
//
//+ (CGFloat)cellHeightWithModel:(id)model
//{
//    return EaseConversationCellMinHeight;
//}
//
//- (void)setSelected:(BOOL)selected animated:(BOOL)animated
//{
//    [super setSelected:selected animated:animated];
//    if (_avatarView.badge) {
//        _avatarView.badgeBackgroudColor = [UIColor redColor];
//    }
//}
//
//-(void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated{
//    [super setHighlighted:highlighted animated:animated];
//    if (_avatarView.badge) {
//        _avatarView.badgeBackgroudColor = [UIColor redColor];
//    }
//}
//
//@end