//
//  QuestionMessage+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 5/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "QuestionMessage.h"

NS_ASSUME_NONNULL_BEGIN

@interface QuestionMessage (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *question;
@property (nullable, nonatomic, retain) NSString *question_id;
@property (nullable, nonatomic, retain) NSNumber *sendtime;
@property (nullable, nonatomic, retain) NSString *session_id;
@property (nullable, nonatomic, retain) NSString *senderusername;

@end

NS_ASSUME_NONNULL_END
