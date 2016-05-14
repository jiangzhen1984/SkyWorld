//
//  SendQuestion.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "SendQuestion.h"
#import "LoginUserInformation.h"
#import "SCUtils.h"
#import "SAMCUserProfileManager.h"
//#import "SCUserProfileManager.h"
#import "SCCoreDataManager.h"
#import "SAMCQuestionMessage.h"

@implementation SendQuestion

+ (SendQuestion *)sendQuestionWithInfo:(NSDictionary *)questionInfo
                inManagedObjectContext:(NSManagedObjectContext *)context
{
    SendQuestion *sendQuestion = nil;

    NSString *question_id = questionInfo[SEND_QUESTION_QUESTION_ID];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SEND_QUESTION];
    
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@",SEND_QUESTION_QUESTION_ID, question_id];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)){
        return nil;
    }else if([matches count]){
        sendQuestion = [matches firstObject];
    }else{
        sendQuestion = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SEND_QUESTION
                                                    inManagedObjectContext:context];
        sendQuestion.question_id = question_id;
    }
    DDLogDebug(@"question: %@",questionInfo);
    sendQuestion.question = questionInfo[SEND_QUESTION_QUESTION];
    sendQuestion.status = @1; // valid
#warning change to server time
    sendQuestion.sendtime = [SCUtils currentTimeStamp];

    NSString *sendUsername = [SAMCUserProfileManager sharedManager].currentLoginData.account;
    sendQuestion.whoSend = [LoginUserInformation loginUserInformationWithUserName:sendUsername
                                                           inManagedObjectContext:context];
    sendQuestion.senderusername = sendUsername;
    
    [[SCCoreDataManager sharedInstance] saveContext];
    return sendQuestion;
}

+ (SendQuestion *)sendQuestionWithId:(NSString *)questionId
              inManagedObjectContext:(NSManagedObjectContext *)context
{
    SendQuestion *sendQuestion = nil;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SEND_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@", SEND_QUESTION_QUESTION_ID, questionId];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if ((error==nil) && matches) {
        sendQuestion = [matches firstObject];
    }
    return sendQuestion;
}

+ (NSArray *)messagesFromQuestionWithTimeFrom:(NSNumber *)timefrom
                                        limit:(NSInteger)limit
                                      session:(NIMSession *)session
                       inManagedObjectContext:(NSManagedObjectContext *)context
{
    if((timefrom == nil) || ([timefrom isEqual:[NSNumber numberWithLongLong:0]])){
       timefrom = [SCUtils currentTimeStamp];
    }
    NSString *currentUsername = [SAMCUserProfileManager sharedManager].currentLoginData.account;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SEND_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K < %@) AND (%K == %@)",SEND_QUESTION_SENDTIME,timefrom,SEND_QUESTION_SENDERUSERNAME,currentUsername];
    request.fetchLimit = limit;
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:SEND_QUESTION_SENDTIME ascending:YES]];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    NSMutableArray *messages = nil;
    if ((error==nil) && matches) {
        messages = [[NSMutableArray alloc] init];
        for (SendQuestion *question in matches) {
            SAMCQuestionMessage *message = [[SAMCQuestionMessage alloc] initWithSendQuestion:question];
            [messages addObject:message];
        }
    }
    return messages;
}

@end
