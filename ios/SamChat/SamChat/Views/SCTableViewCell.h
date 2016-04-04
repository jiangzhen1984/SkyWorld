//
//  SCTableViewCell.h
//  SamChat
//
//  Created by HJ on 4/3/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ISCTableCellModel.h"

@interface SCTableViewCell : UITableViewCell

@property (nonatomic, strong) UIImageView *avatarView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *timeLabel;
@property (nonatomic, strong) UILabel *detailLabel;

@property (nonatomic, strong) id<ISCTableCellModel> model;

@end
//
//#import "IConversationModel.h"
//#import "IModelCell.h"
//#import "EaseImageView.h"
//
//static CGFloat EaseConversationCellMinHeight = 60;
//
//@interface EaseConversationCell : UITableViewCell<IModelCell>
//
//@property (strong, nonatomic) EaseImageView *avatarView;
//
//@property (strong, nonatomic) UILabel *detailLabel;
//
//@property (strong, nonatomic) UILabel *timeLabel;
//
//@property (strong, nonatomic) UILabel *titleLabel;
//
//@property (strong, nonatomic) id<IConversationModel> model;
//
//@property (nonatomic) BOOL showAvatar;//default is "YES"
//
//@property (nonatomic) UIFont *titleLabelFont UI_APPEARANCE_SELECTOR;
//
//@property (nonatomic) UIColor *titleLabelColor UI_APPEARANCE_SELECTOR;
//
//@property (nonatomic) UIFont *detailLabelFont UI_APPEARANCE_SELECTOR;
//
//@property (nonatomic) UIColor *detailLabelColor UI_APPEARANCE_SELECTOR;
//
//@property (nonatomic) UIFont *timeLabelFont UI_APPEARANCE_SELECTOR;
//
//@property (nonatomic) UIColor *timeLabelColor UI_APPEARANCE_SELECTOR;
