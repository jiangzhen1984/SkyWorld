//
//  SCArticleRecommend+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "SCArticleRecommend.h"

NS_ASSUME_NONNULL_BEGIN

@interface SCArticleRecommend (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *recommender_phonenumber;
@property (nullable, nonatomic, retain) NSString *recommender_username;
@property (nullable, nonatomic, retain) NSNumber *timestamp;
@property (nullable, nonatomic, retain) NSNumber *fg_id;

@end

NS_ASSUME_NONNULL_END
