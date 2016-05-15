//
//  SAMCSessionManager.m
//  SamChat
//
//  Created by HJ on 5/15/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSessionManager.h"
#import "SCCoreDataManager.h"
#import "SessionExtension.h"

@implementation SAMCSessionManager

- (void)setExtOfSessionWithMessage:(NIMMessage *)message
{
    NSNumber *sessionType = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
    if (sessionType == nil) {
        return;
    }
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
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
            [[SCCoreDataManager sharedInstance] saveContext];
        }
    }];
}

- (void)updateSession:(NSString *)sessionId tagType:(NSNumber *)tagType value:(BOOL)flag
{
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateChildObjectContextOfmainContext];
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
            [[SCCoreDataManager sharedInstance] saveContext];
        }
    }];
}

- (BOOL)searchTagOfSession:(NSString *)sessionId
{
    NSManagedObjectContext *context = [[SCCoreDataManager sharedInstance] confinementObjectContextOfmainContext];
    return [SessionExtension searchTagOfSession:sessionId inManagedObjectContext:context];
}

- (BOOL)chatTagOfSession:(NSString *)sessionId
{
    NSManagedObjectContext *context = [[SCCoreDataManager sharedInstance] confinementObjectContextOfmainContext];
    return [SessionExtension chatTagOfSession:sessionId inManagedObjectContext:context];
}

- (BOOL)serviceTagOfSession:(NSString *)sessionId
{
    NSManagedObjectContext *context = [[SCCoreDataManager sharedInstance] confinementObjectContextOfmainContext];
    return [SessionExtension serviceTagOfSession:sessionId inManagedObjectContext:context];
}

- (BOOL)deleteSession:(NSString *)sessionId ifNeededAfterClearTagType:(NSNumber *)tagType
{
    NSManagedObjectContext *context = [[SCCoreDataManager sharedInstance] confinementObjectContextOfmainContext];
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
    BOOL result = [SessionExtension deleteSessionIfNeeded:sessionId inManagedObjectContext:context];
    if ([context hasChanges]) {
        [context save:NULL];
        [[SCCoreDataManager sharedInstance] saveContext];
    }
    return result;
}

@end
