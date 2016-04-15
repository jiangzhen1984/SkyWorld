//
//  SCArticleCellModel.m
//  SamChat
//
//  Created by HJ on 4/11/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleCellModel.h"
#import "SCArticlePicture.h"
#import "SCArticleRecommend.h"
#import "SCArticleComment.h"
#import <UIKit/UIKit.h>

extern const CGFloat contentLabelFontSize;
extern CGFloat maxContentLabelHeight;

@interface SCArticleCellModel ()
@property (nonatomic, strong) SCArticle *article;
@end

@implementation SCArticleCellModel
{
    CGFloat _lastContentWidth;
}

@synthesize msgContent = _msgContent;

- (instancetype)initWithSCArticle:(SCArticle *)article
{
    self = [super init];
    if(self){
        self.article = article;
        _msgContent = article.comment;
    }
    if(self.article == nil){
        return nil;
    }
    return self;
}

//- (void)setMsgContent:(NSString *)msgContent
//{
//    _msgContent = msgContent;
//}

- (NSString *)msgContent
{
    CGFloat contentW = [UIScreen mainScreen].bounds.size.width - 70;
    if (contentW != _lastContentWidth) {
        _lastContentWidth = contentW;
        CGRect textRect = [_msgContent boundingRectWithSize:CGSizeMake(contentW, MAXFLOAT) options:NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:contentLabelFontSize]} context:nil];
        if (textRect.size.height > maxContentLabelHeight) {
            _shouldShowMoreButton = YES;
        } else {
            _shouldShowMoreButton = NO;
        }
    }
    
    return _msgContent;
}

- (void)setIsOpening:(BOOL)isOpening
{
    if (!_shouldShowMoreButton) {
        _isOpening = NO;
    } else {
        _isOpening = isOpening;
    }
}

- (NSString *)avatarUrl
{
    return [[SCUserProfileManager sharedInstance] getUserProfileByUsername:self.name updateFlag:false].imagefile;
}

- (UIImage *)avatarImageDefault
{
    return [UIImage imageNamed:@"UserAvatarDefault"];
}

- (NSString *)time
{
    return [SCUtils convertToDateStringWithTimeStamp:[self.article.timestamp integerValue]];
}

- (NSString *)name
{
    return self.article.publish_username;
}

- (NSNumber *)articleId
{
    return self.article.fg_id;
}

// urls
- (NSArray *)picUrlsArray
{
    NSManagedObjectContext *mainContext = [[SCCoreDataManager sharedInstance] mainObjectContext];
    NSArray *articlePictureArray = [SCArticlePicture loadArticlePicturesWithArticleId:[self.article.fg_id integerValue]
                                                                inManagedObjecContext:mainContext];
    NSMutableArray *picUrls = [[NSMutableArray alloc] init];
    for (SCArticlePicture *articlePicture in articlePictureArray) {
        [picUrls addObject:articlePicture.url_original];
    }
    return picUrls;
}

- (NSArray<SCArticleCellLikeItemModel *> *)likeItemsArray
{
    if(_likeItemsArray == nil){
        NSMutableArray *likes = [[NSMutableArray alloc] init];
        // should be main thread
        NSArray *scarticleRecommends = [SCArticleRecommend loadArticleRecommendsWithArticleId:[self.article.fg_id integerValue]
                                        inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
        for (SCArticleRecommend *recommend in scarticleRecommends) {
            SCArticleCellLikeItemModel *likeModel = [[SCArticleCellLikeItemModel alloc] init];
            likeModel.userName = recommend.recommender_username;
#warning id or cellphone
            [likes addObject:likeModel];
        }
        _likeItemsArray = likes;
    }
    return _likeItemsArray;
}

- (NSArray<SCArticleCellCommentItemModel *> *)commentItemsArray
{
    if(_commentItemsArray == nil){
        NSMutableArray *comments = [[NSMutableArray alloc] init];
        NSArray *scarticleComments = [SCArticleComment loadArticleCommentsWithArticleId:[self.article.fg_id integerValue]
                                                                 inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
        for (SCArticleComment *comment in scarticleComments) {
            SCArticleCellCommentItemModel *commentModel = [[SCArticleCellCommentItemModel alloc] init];
            commentModel.commentString = comment.content;
            commentModel.firstUserName = comment.commenter_username;
            commentModel.firstUserId = comment.commenter_phonenumber;
            [comments addObject:commentModel];
        }
        _commentItemsArray = comments;
    }
    return _commentItemsArray;
}


@end


@implementation SCArticleCellLikeItemModel


@end

@implementation SCArticleCellCommentItemModel


@end