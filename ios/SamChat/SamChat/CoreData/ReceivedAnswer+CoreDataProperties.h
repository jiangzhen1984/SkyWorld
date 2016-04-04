//
//  ReceivedAnswer+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "ReceivedAnswer.h"

NS_ASSUME_NONNULL_BEGIN

@interface ReceivedAnswer (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *question_id;
@property (nullable, nonatomic, retain) NSString *answer;
@property (nullable, nonatomic, retain) NSNumber *receivedtime;
@property (nullable, nonatomic, retain) ContactUser *fromWho;

@end

NS_ASSUME_NONNULL_END
