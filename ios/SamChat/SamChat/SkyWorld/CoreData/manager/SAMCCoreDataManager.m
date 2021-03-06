//
//  SAMCCoreDataManager.m
//  SamChat
//
//  Created by HJ on 5/15/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCCoreDataManager.h"
#import "NTESFileLocationHelper.h"

@implementation SAMCCoreDataManager

+ (instancetype)sharedManager
{
    static SAMCCoreDataManager *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
    if(self){
    }
    return self;
}

#pragma mark - Core Data stack
@synthesize managedObjectModel = _managedObjectModel;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;
@synthesize mainObjectContext = _mainObjectContext;
@synthesize backgroundObjectContext = _backgroundObjectContext;

- (NSURL *)applicationDocumentsDirectory {
    return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}

- (NSManagedObjectModel *)managedObjectModel {
    if (_managedObjectModel != nil) {
        return _managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"SamChat" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

- (NSPersistentStoreCoordinator *)persistentStoreCoordinator {
    if (_persistentStoreCoordinator != nil) {
        return _persistentStoreCoordinator;
    }
    
    // Create the coordinator and store
    
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    //NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"SamChat.sqlite"];
    NSString *dbPath = [[NTESFileLocationHelper userDirectory] stringByAppendingString:@"samchat.sqlite"];
    NSURL *storeURL = [NSURL fileURLWithPath:dbPath];
    NSError *error = nil;
    NSString *failureReason = @"There was an error creating or loading the application's saved data.";
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error]) {
        // Report any error we got.
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        dict[NSLocalizedDescriptionKey] = @"Failed to initialize the application's saved data";
        dict[NSLocalizedFailureReasonErrorKey] = failureReason;
        dict[NSUnderlyingErrorKey] = error;
        error = [NSError errorWithDomain:@"YOUR_ERROR_DOMAIN" code:9999 userInfo:dict];
        // TODO: Replace this with code to handle the error appropriately.
        // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
    
    return _persistentStoreCoordinator;
}

- (NSManagedObjectContext *)mainObjectContext {
    if (_mainObjectContext != nil) {
        return _mainObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (!coordinator) {
        return nil;
    }
    _mainObjectContext = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSMainQueueConcurrencyType];
    _mainObjectContext.parentContext = self.backgroundObjectContext;
    return _mainObjectContext;
}

- (NSManagedObjectContext *)backgroundObjectContext
{
    if(_backgroundObjectContext != nil) {
        return _backgroundObjectContext;
    }
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if(!coordinator) {
        return nil;
    }
    _backgroundObjectContext = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSPrivateQueueConcurrencyType];
    [_backgroundObjectContext setPersistentStoreCoordinator:coordinator];
    return _backgroundObjectContext;
}

- (NSManagedObjectContext *)privateChildObjectContextOfmainContext
{
    NSManagedObjectContext *context = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSPrivateQueueConcurrencyType];
    context.parentContext = self.mainObjectContext;
    return context;
}

#pragma mark - Core Data Saving support
- (void)saveContext
{
    if([self.mainObjectContext hasChanges]){
        [self.mainObjectContext performBlockAndWait:^{
            [self.mainObjectContext save:NULL];
        }];
    }
    if([self.backgroundObjectContext hasChanges]){
        [self.backgroundObjectContext performBlock:^{
            [self.backgroundObjectContext save:NULL];
        }];
    }
}

@end
