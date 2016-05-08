//
//  ContactUser+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "ContactUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface ContactUser (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *area;
@property (nullable, nonatomic, retain) NSString *desc;
@property (nullable, nonatomic, retain) NSString *easemob_username;
@property (nullable, nonatomic, retain) NSString *imagefile;
@property (nullable, nonatomic, retain) NSNumber *lastupdate;
@property (nullable, nonatomic, retain) NSString *location;
@property (nullable, nonatomic, retain) NSString *phonenumber;
@property (nullable, nonatomic, retain) NSNumber *unique_id;
@property (nullable, nonatomic, retain) NSString *username;
@property (nullable, nonatomic, retain) NSNumber *usertype;
@property (nullable, nonatomic, retain) NSSet<ReceivedQuestion *> *questions;

@end

@interface ContactUser (CoreDataGeneratedAccessors)

- (void)addQuestionsObject:(ReceivedQuestion *)value;
- (void)removeQuestionsObject:(ReceivedQuestion *)value;
- (void)addQuestions:(NSSet<ReceivedQuestion *> *)values;
- (void)removeQuestions:(NSSet<ReceivedQuestion *> *)values;

@end

NS_ASSUME_NONNULL_END
