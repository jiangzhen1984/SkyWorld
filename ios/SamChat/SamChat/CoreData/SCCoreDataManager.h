//
//  SCCoreDataManager.h
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCCoreDataManager : NSObject

@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

@property (readonly, strong, nonatomic) NSManagedObjectContext *mainObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectContext *backgroundObjectContext;

+ (instancetype)sharedInstance;
- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;

@end
