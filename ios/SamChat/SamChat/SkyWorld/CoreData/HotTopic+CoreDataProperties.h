//
//  HotTopic+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "HotTopic.h"

NS_ASSUME_NONNULL_BEGIN

@interface HotTopic (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *name;
@property (nullable, nonatomic, retain) NSNumber *type;

@end

NS_ASSUME_NONNULL_END
