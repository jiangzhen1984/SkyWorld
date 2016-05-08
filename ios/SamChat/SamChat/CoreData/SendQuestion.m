//
//  SendQuestion.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "SendQuestion.h"
#import "LoginUserInformation.h"

@implementation SendQuestion

+ (SendQuestion *)sendQuestionWithInfo:(NSDictionary *)questionInfo inManagedObjectContext:(NSManagedObjectContext *)context
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
    DebugLog(@"question: %@",questionInfo);
    sendQuestion.question = questionInfo[SEND_QUESTION_QUESTION];
    sendQuestion.status = @1; // valid
#warning change to server time
    sendQuestion.sendtime = [SCUtils currentTimeStamp];

    NSString *sendUsername = [SCUserProfileManager sharedInstance].username;
    sendQuestion.whoSend = [LoginUserInformation loginUserInformationWithUserName:sendUsername
                                                           inManagedObjectContext:context];
    sendQuestion.senderusername = sendUsername;
    
    [[SCCoreDataManager sharedInstance] saveContext];
    return sendQuestion;
}

+ (SendQuestion *)sendQuestionWithId:(NSString *)questionId inManagedObjectContext:(NSManagedObjectContext *)context
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

@end
