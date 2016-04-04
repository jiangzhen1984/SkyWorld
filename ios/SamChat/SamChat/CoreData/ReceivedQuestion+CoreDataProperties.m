//
//  ReceivedQuestion+CoreDataProperties.m
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "ReceivedQuestion+CoreDataProperties.h"

@implementation ReceivedQuestion (CoreDataProperties)

@dynamic question_id;
@dynamic question;
@dynamic status;
@dynamic response;
@dynamic receivedtime;
@dynamic canceledtime;
@dynamic receivercellphone;
@dynamic receiverusername;
@dynamic fromWho;

@end
