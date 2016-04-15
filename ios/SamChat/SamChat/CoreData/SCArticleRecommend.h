//
//  SCArticleRecommend.h
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class SCArticle;

NS_ASSUME_NONNULL_BEGIN

@interface SCArticleRecommend : NSManagedObject

+ (void)updateArticleRecommendsWithRecommendsArray:(NSArray *)recommendsArray articleId:(NSNumber *)articleId inManagedObjectContext:(NSManagedObjectContext *)context;
+ (NSArray *)loadArticleRecommendsWithArticleId:(NSNumber *)articleId inManagedObjectContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "SCArticleRecommend+CoreDataProperties.h"
