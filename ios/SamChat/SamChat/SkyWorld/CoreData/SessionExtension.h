//
//  SessionExtension.h
//  SamChat
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

NS_ASSUME_NONNULL_BEGIN

@interface SessionExtension : NSManagedObject

+ (void)updateSession:(NSString *)sessionId searchTag:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)updateSession:(NSString *)sessionId chatTag:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context;
+ (void)updateSession:(NSString *)sessionId serviceTag:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context;

+ (BOOL)searchTagOfSession:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context;
+ (BOOL)chatTagOfSession:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context;
+ (BOOL)serviceTagOfSession:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context;

+ (BOOL)shouldDeleteSession:(NSString *)sessionId;

@end

NS_ASSUME_NONNULL_END

#import "SessionExtension+CoreDataProperties.h"
