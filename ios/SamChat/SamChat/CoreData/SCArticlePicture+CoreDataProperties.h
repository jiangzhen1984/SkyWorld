//
//  SCArticlePicture+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "SCArticlePicture.h"

NS_ASSUME_NONNULL_BEGIN

@interface SCArticlePicture (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *url_thumbnail;
@property (nullable, nonatomic, retain) NSString *url_original;
@property (nullable, nonatomic, retain) NSNumber *sequence;
@property (nullable, nonatomic, retain) NSNumber *fg_id;

@end

NS_ASSUME_NONNULL_END
