//
//  ReceivedQuestion+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "ReceivedQuestion.h"

NS_ASSUME_NONNULL_BEGIN

@interface ReceivedQuestion (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *question_id;
@property (nullable, nonatomic, retain) NSString *question;
@property (nullable, nonatomic, retain) NSNumber *status;
@property (nullable, nonatomic, retain) NSNumber *response;
@property (nullable, nonatomic, retain) NSNumber *receivedtime;
@property (nullable, nonatomic, retain) NSNumber *canceledtime;
@property (nullable, nonatomic, retain) NSString *receivercellphone;
@property (nullable, nonatomic, retain) NSString *receiverusername;
@property (nullable, nonatomic, retain) ContactUser *fromWho;

@end

NS_ASSUME_NONNULL_END
