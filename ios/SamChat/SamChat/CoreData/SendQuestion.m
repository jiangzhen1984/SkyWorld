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
    
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %@",SEND_QUESTION_QUESTION_ID, question_id];
    
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
    sendQuestion.question = questionInfo[SEND_QUESTION_QUESTION];
    sendQuestion.status = @1; // valid
    sendQuestion.sendtime = [SCUtils currentTimeStamp];

    sendQuestion.whoSend = [LoginUserInformation loginUserInformationWithUserName:[SCUserProfileManager sharedInstance].username
                                                           inManagedObjectContext:context];
    
    [[SCCoreDataManager sharedInstance] saveContext];
    return sendQuestion;
}

@end
