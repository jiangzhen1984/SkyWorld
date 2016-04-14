//
//  SCArticle+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "SCArticle.h"

NS_ASSUME_NONNULL_BEGIN

@interface SCArticle (CoreDataProperties)

@property (nullable, nonatomic, retain) NSNumber *timestamp;
@property (nullable, nonatomic, retain) NSNumber *fg_id;
@property (nullable, nonatomic, retain) NSNumber *status;
@property (nullable, nonatomic, retain) NSString *comment;
@property (nullable, nonatomic, retain) NSString *owner_username;
@property (nullable, nonatomic, retain) NSString *owner_phonenumber;
@property (nullable, nonatomic, retain) NSString *publisher_phonenumber;
@property (nullable, nonatomic, retain) NSString *publish_username;

@end

NS_ASSUME_NONNULL_END
