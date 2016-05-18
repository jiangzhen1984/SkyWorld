//
//  SAMCSessionManager.m
//  SamChat
//
//  Created by HJ on 5/15/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSessionManager.h"
#import "SAMCCoreDataManager.h"
#import "SessionExtension.h"

@implementation SAMCSessionManager

- (void)setExtOfSessionWithMessage:(NIMMessage *)message
{
    NSNumber *sessionType = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
    if (sessionType == nil) {
        return;
    }
    NSManagedObjectContext *privateContext = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [privateContext performBlockAndWait:^{
        if ([sessionType isEqualToNumber:MESSAGE_FROM_VIEW_SEARCH]) {
            [SessionExtension updateSession:message.session.sessionId
                                 serviceTag:YES
                     inManagedObjectContext:privateContext];
        }else if([sessionType isEqualToNumber:MESSAGE_FROM_VIEW_CHAT]) {
            [SessionExtension updateSession:message.session.sessionId
                                    chatTag:YES
                     inManagedObjectContext:privateContext];
        }else if([sessionType isEqualToNumber:MESSAGE_FROM_VIEW_VENDOR]) {
            [SessionExtension updateSession:message.session.sessionId
                                  searchTag:YES
                     inManagedObjectContext:privateContext];
        }
        if ([privateContext hasChanges]) {
            [privateContext save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
}

- (void)updateSession:(NSString *)sessionId tagType:(NSNumber *)tagType value:(BOOL)flag
{
    NSManagedObjectContext *privateContext = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        if ([tagType isEqualToNumber:MESSAGE_FROM_VIEW_SEARCH]) {
            [SessionExtension updateSession:sessionId
                                 serviceTag:flag
                     inManagedObjectContext:privateContext];
        }else if([tagType isEqualToNumber:MESSAGE_FROM_VIEW_CHAT]) {
            [SessionExtension updateSession:sessionId
                                    chatTag:flag
                     inManagedObjectContext:privateContext];
        }else if([tagType isEqualToNumber:MESSAGE_FROM_VIEW_VENDOR]) {
            [SessionExtension updateSession:sessionId
                                  searchTag:flag
                     inManagedObjectContext:privateContext];
        }
        if ([privateContext hasChanges]) {
            [privateContext save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
}

- (BOOL)searchTagOfSession:(NSString *)sessionId
{
    __block BOOL result = NO;
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [context performBlockAndWait:^{
        result = [SessionExtension searchTagOfSession:sessionId inManagedObjectContext:context];
    }];
    return result;
}

- (BOOL)chatTagOfSession:(NSString *)sessionId
{
    __block BOOL result = NO;
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [context performBlockAndWait:^{
        result = [SessionExtension chatTagOfSession:sessionId inManagedObjectContext:context];
    }];
    return result;
}

- (BOOL)serviceTagOfSession:(NSString *)sessionId
{
    __block BOOL result = NO;
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [context performBlockAndWait:^{
        result = [SessionExtension serviceTagOfSession:sessionId inManagedObjectContext:context];
    }];
    return result;
}

- (BOOL)deleteSession:(NSString *)sessionId ifNeededAfterClearTagType:(NSNumber *)tagType
{
    __block BOOL result = NO;
    NSManagedObjectContext *context = [[SAMCCoreDataManager sharedManager] privateChildObjectContextOfmainContext];
    [context performBlockAndWait:^{
        if ([tagType isEqualToNumber:MESSAGE_FROM_VIEW_SEARCH]) {
            [SessionExtension updateSession:sessionId
                                 serviceTag:NO
                     inManagedObjectContext:context];
        }else if([tagType isEqualToNumber:MESSAGE_FROM_VIEW_CHAT]) {
            [SessionExtension updateSession:sessionId
                                    chatTag:NO
                     inManagedObjectContext:context];
        }else if([tagType isEqualToNumber:MESSAGE_FROM_VIEW_VENDOR]) {
            [SessionExtension updateSession:sessionId
                                  searchTag:NO
                     inManagedObjectContext:context];
        }
        result = [SessionExtension deleteSessionIfNeeded:sessionId inManagedObjectContext:context];
        if ([context hasChanges]) {
            [context save:NULL];
            [[SAMCCoreDataManager sharedManager] saveContext];
        }
    }];
    return result;
}

@end
