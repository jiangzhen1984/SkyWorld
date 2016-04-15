//
//  SCArticlePicture.h
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class SCArticle;

NS_ASSUME_NONNULL_BEGIN

@interface SCArticlePicture : NSManagedObject

+ (void)insertArticlePicturesWithPicsArray:(NSArray *)picsArray articleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context;
+ (NSArray *)loadArticlePicturesWithArticleId:(NSInteger)fg_id inManagedObjecContext:(NSManagedObjectContext *)context;

@end

NS_ASSUME_NONNULL_END

#import "SCArticlePicture+CoreDataProperties.h"
