//
//  SAMCCoreDataManager.h
//  SamChat
//
//  Created by HJ on 5/15/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SAMCCoreDataMacro.h"

@interface SAMCCoreDataManager : NSObject

@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

@property (readonly, strong, nonatomic) NSManagedObjectContext *mainObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectContext *backgroundObjectContext;

+ (instancetype)sharedManager;
- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;
- (NSManagedObjectContext *)privateChildObjectContextOfmainContext;
- (NSManagedObjectContext *)confinementObjectContextOfmainContext;

@end
