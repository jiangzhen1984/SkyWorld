//
//  SCArticleCellModel.h
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
@class SCArticleCellLikeItemModel, SCArticleCellCommentItemModel;

@interface SCArticleCellModel : NSObject

@property (nonatomic, copy) NSString *iconName;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *msgContent;
@property (nonatomic, strong) NSArray *picNamesArray;

@property (nonatomic, strong) NSArray<SCArticleCellLikeItemModel *> *likeItemsArray;
@property (nonatomic, strong) NSArray<SCArticleCellCommentItemModel *> *commentItemsArray;

@property (nonatomic, assign) BOOL isOpening;

@property (nonatomic, assign, readonly) BOOL shouldShowMoreButton;


@end


@interface SCArticleCellLikeItemModel : NSObject

@property (nonatomic, copy) NSString *userName;
@property (nonatomic, copy) NSString *userId;

@end


@interface SCArticleCellCommentItemModel : NSObject

@property (nonatomic, copy) NSString *commentString;

@property (nonatomic, copy) NSString *firstUserName;
@property (nonatomic, copy) NSString *firstUserId;

@property (nonatomic, copy) NSString *secondUserName;
@property (nonatomic, copy) NSString *secondUserId;

@end