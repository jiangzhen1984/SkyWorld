//
//  SCArticleCell.h
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SCArticleCellDelegate <NSObject>

- (void)didClickLickButtonInCell:(UITableViewCell *)cell;
- (void)didClickcCommentButtonInCell:(UITableViewCell *)cell;

@end

@class SCArticleCellModel;

@interface SCArticleCell : UITableViewCell

@property (nonatomic, weak) id<SCArticleCellDelegate> delegate;

@property (nonatomic, strong) SCArticleCellModel *model;

@property (nonatomic, strong) NSIndexPath *indexPath;

@property (nonatomic, copy) void (^moreButtonClickedBlock)(NSIndexPath *indexPath);

@end