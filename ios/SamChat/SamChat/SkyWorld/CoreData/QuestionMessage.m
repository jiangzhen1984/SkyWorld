//
//  QuestionMessage.m
//  SamChat
//
//  Created by HJ on 5/13/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "QuestionMessage.h"
#import "SendQuestion.h"
#import "SCUtils.h"
#import "SAMCQuestionMessage.h"
#import "SAMCUserProfileManager.h"

@implementation QuestionMessage

+ (void)insertQuestionWithIds:(NSArray *)questionIds
                    sessionId:(NSString *)sessionId
       inManagedObjectContext:(NSManagedObjectContext *)context
{
    if ((questionIds == nil) || ([questionIds count] == 0)) {
        return;
    }
    
    [questionIds enumerateObjectsUsingBlock:^(NSString *questionId, NSUInteger idx, BOOL * _Nonnull stop) {
        QuestionMessage *questionMessage = nil;
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_QUESTION_MESSAGE];
        request.predicate = [NSPredicate predicateWithFormat:@"(%K == %@) AND (%K == %@)", QUESTION_MESSAGE_QUESTION_ID, questionId, QUESTION_MESSAGE_SESSSION_ID, sessionId];
        NSError *error;
        NSArray *matches = [context executeFetchRequest:request error:&error];
        if ((error==nil) && ([matches count] == 0)) {
            SendQuestion *sendQuestion = [SendQuestion sendQuestionWithId:questionId inManagedObjectContext:context];
            if (sendQuestion != nil) {
                questionMessage = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_QUESTION_MESSAGE
                                                                inManagedObjectContext:context];
                questionMessage.question_id = questionId;
                questionMessage.senderusername = sendQuestion.senderusername;
                questionMessage.sendtime = sendQuestion.sendtime;
                questionMessage.question = sendQuestion.question;
                questionMessage.session_id = sessionId;
            } 
        }
    }];
}

+ (NSArray *)messagesFromQuestionMessageWithTimeFrom:(NSNumber *)timefrom
                                               limit:(NSInteger)limit
                                             session:(NIMSession *)session
                              inManagedObjectContext:(NSManagedObjectContext *)context
{
    if((timefrom == nil) || ([timefrom isEqual:[NSNumber numberWithLongLong:0]])){
        timefrom = [SCUtils currentTimeStamp];
    }
    NSString *currentUsername = [SAMCUserProfileManager sharedManager].currentLoginData.account;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_QUESTION_MESSAGE];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K < %@) AND (%K == %@) AND (%K == %@)",QUESTION_MESSAGE_SENDTIME,timefrom,QUESTION_MESSAGE_SENDERUSERNAME,currentUsername,QUESTION_MESSAGE_SESSSION_ID,session.sessionId];
//    request.predicate = [NSPredicate predicateWithFormat:@"%K < %@",QUESTION_MESSAGE_SENDTIME,timefrom];
    request.fetchLimit = limit;
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:QUESTION_MESSAGE_SENDTIME ascending:YES]];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    __block NSMutableArray *messages = nil;
    if ((error==nil) && matches) {
        messages = [[NSMutableArray alloc] init];
        [matches enumerateObjectsUsingBlock:^(QuestionMessage *questionMessage, NSUInteger idx, BOOL * _Nonnull stop) {
            SAMCQuestionMessage *message = [[SAMCQuestionMessage alloc] initWithQuestionMessage:questionMessage];
            [messages addObject:message];
        }];
    }
    return messages;
}

+ (void)deleteQuestionMessageWithId:(NSString *)questionId
                          sessionId:(NSString *)sessionId
             inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_QUESTION_MESSAGE];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K == %@) AND (%K == %@)", QUESTION_MESSAGE_QUESTION_ID, questionId, QUESTION_MESSAGE_SESSSION_ID, sessionId];
    [request setIncludesPropertyValues:NO]; //only fetch the managedObjectID
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((error == nil) && ([matches count] > 0)){
        for (NSManagedObject *questionMessage in matches) {
            [context deleteObject:questionMessage];
        }
    }
}

+ (void)deleteAllQuestionMessagesWithSessionId:(NSString *)sessionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_QUESTION_MESSAGE];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@", QUESTION_MESSAGE_SESSSION_ID, sessionId];
    [request setIncludesPropertyValues:NO]; //only fetch the managedObjectID
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((error == nil) && ([matches count] > 0)){
        for (NSManagedObject *questionMessage in matches) {
            [context deleteObject:questionMessage];
        }
    }
}

@end
