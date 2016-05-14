//
//  SessionExtension+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "SessionExtension.h"

NS_ASSUME_NONNULL_BEGIN

@interface SessionExtension (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *session_id;
@property (nullable, nonatomic, retain) NSNumber *search_tag;
@property (nullable, nonatomic, retain) NSNumber *chat_tag;
@property (nullable, nonatomic, retain) NSNumber *service_tag;

@end

NS_ASSUME_NONNULL_END
