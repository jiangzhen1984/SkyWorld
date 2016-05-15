//
//  SessionExtension.m
//  SamChat
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SessionExtension.h"
#import "SCCoreDataManager.h"

@implementation SessionExtension

+ (void)updateSession:(NSString *)sessionId searchTag:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context
{
    SessionExtension *sessionExtension = [SessionExtension sessionExtensionWithSessionId:sessionId
                                                                  inManagedObjectContext:context];
    if (sessionExtension == nil) {
        DDLogError(@"session extension update search tag error");
        return;
    }
    if ([sessionExtension.search_tag isEqualToNumber:[NSNumber numberWithBool:flag]]) {
        return;
    }
    sessionExtension.search_tag = [NSNumber numberWithBool:flag];
}

+ (void)updateSession:(NSString *)sessionId chatTag:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context
{
    SessionExtension *sessionExtension = [SessionExtension sessionExtensionWithSessionId:sessionId
                                                                  inManagedObjectContext:context];
    if (sessionExtension == nil) {
        DDLogError(@"session extension update chat tag error");
        return;
    }
    if ([sessionExtension.chat_tag isEqualToNumber:[NSNumber numberWithBool:flag]]) {
        return;
    }
    sessionExtension.chat_tag = [NSNumber numberWithBool:flag];
}

+ (void)updateSession:(NSString *)sessionId serviceTag:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context
{
    SessionExtension *sessionExtension = [SessionExtension sessionExtensionWithSessionId:sessionId
                                                                  inManagedObjectContext:context];
    if (sessionExtension == nil) {
        DDLogError(@"session extension update service tag error");
        return;
    }
    if ([sessionExtension.service_tag isEqualToNumber:[NSNumber numberWithBool:flag]]) {
        return;
    }
    sessionExtension.service_tag = [NSNumber numberWithBool:flag];
}

+ (BOOL)searchTagOfSession:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SESSION_EXTENSTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K == %@) AND (%K == %@)",SESSION_EXTENSION_SESSION_ID, sessionId, SESSION_EXTENSION_SEARCH_TAG, [NSNumber numberWithBool:YES]];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if ((error==nil) && ([matches count] > 0)) {
        return YES;
    }else{
        return NO;
    }
}

+ (BOOL)chatTagOfSession:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SESSION_EXTENSTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K == %@) AND (%K == %@)",SESSION_EXTENSION_SESSION_ID, sessionId, SESSION_EXTENSION_CHAT_TAG, [NSNumber numberWithBool:YES]];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if ((error==nil) && ([matches count] > 0)) {
        return YES;
    }else{
        return NO;
    }
}

+ (BOOL)serviceTagOfSession:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SESSION_EXTENSTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K == %@) AND (%K == %@)",SESSION_EXTENSION_SESSION_ID, sessionId, SESSION_EXTENSION_SERVICE_TAG, [NSNumber numberWithBool:YES]];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if ((error==nil) && ([matches count] > 0)) {
        return YES;
    }else{
        return NO;
    }
}

+ (BOOL)deleteSessionIfNeeded:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    BOOL result = YES;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SESSION_EXTENSTION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@",SESSION_EXTENSION_SESSION_ID, sessionId];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if ((error == nil) && ([matches count] > 0)) {
        SessionExtension *sessionExtension = [matches firstObject];
        if (([sessionExtension.search_tag isEqualToNumber:[NSNumber numberWithBool:YES]])
            || ([sessionExtension.chat_tag isEqualToNumber:[NSNumber numberWithBool:YES]])
            || ([sessionExtension.service_tag isEqualToNumber:[NSNumber numberWithBool:YES]])) {
            result = NO;
        }else{
            [context deleteObject:sessionExtension];
        }
    }
    return result;
}

#pragma mark - Private
+ (SessionExtension *)sessionExtensionWithSessionId:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    SessionExtension *sessionExtension = nil;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SESSION_EXTENSTION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@",SESSION_EXTENSION_SESSION_ID, sessionId];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)){
        return nil;
    }else if([matches count]){
        sessionExtension = [matches firstObject];
    }else{
        sessionExtension = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SESSION_EXTENSTION
                                                         inManagedObjectContext:context];
        sessionExtension.session_id = sessionId;
        sessionExtension.search_tag = [NSNumber numberWithBool:NO];
        sessionExtension.chat_tag = [NSNumber numberWithBool:NO];
        sessionExtension.service_tag = [NSNumber numberWithBool:NO];
    }
    return sessionExtension;
}

@end
