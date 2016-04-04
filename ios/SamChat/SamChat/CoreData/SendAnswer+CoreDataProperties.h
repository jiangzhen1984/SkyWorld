//
//  SendAnswer+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "SendAnswer.h"

NS_ASSUME_NONNULL_BEGIN

@interface SendAnswer (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *question_id;
@property (nullable, nonatomic, retain) NSString *answer;
@property (nullable, nonatomic, retain) NSNumber *status;
@property (nullable, nonatomic, retain) NSNumber *sendtime;
@property (nullable, nonatomic, retain) LoginUserInformation *whoSend;

@end

NS_ASSUME_NONNULL_END
