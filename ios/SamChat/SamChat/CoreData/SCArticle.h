//
//  SCArticle.h
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class SCArticleComment;
@class SCArticleRecommend;
@class SCArticlePicture;

NS_ASSUME_NONNULL_BEGIN

@interface SCArticle : NSManagedObject

+ (void)insertArticlesWithSkyWorldInfo:(NSArray *)articleArray inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)asyncInsertArticlesWithSkyWorldInfo:(NSArray *)articleArray completion:(void (^)(BOOL success))completion;
+ (NSArray *)loadArticlesEarlierThan:(NSInteger)timeline maxCount:(NSInteger)count inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "SCArticle+CoreDataProperties.h"
