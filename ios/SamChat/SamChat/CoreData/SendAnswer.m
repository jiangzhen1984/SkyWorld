//
//  SendAnswer.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "SendAnswer.h"
#import "LoginUserInformation.h"

@interface SendAnswer ()

@end

@implementation SendAnswer
@synthesize i_cellHeight = _i_cellHeight;

+ (SendAnswer *)sendAnswerWithInfo:(NSDictionary *)answerInfo inManagedObjectContext:(NSManagedObjectContext *)context
{
    SendAnswer *sendAnswer = nil;
    DebugLog(@"answerInfo:%@", answerInfo);
    
    NSString *question_id = answerInfo[SEND_ANSWER_QUESTION_ID];
    if(!answerInfo[SEND_ANSWER_QUESTION_ID]){
        return nil;
    }
    
    sendAnswer = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SEND_ANSWER
                                                           inManagedObjectContext:context];
    sendAnswer.question_id = question_id;
    sendAnswer.answer = answerInfo[SEND_ANSWER_ANSWER];
    sendAnswer.status = answerInfo[SEND_ANSWER_STATUS]?:SEND_ANSWER_SENDING;
    sendAnswer.sendtime = answerInfo[SEND_ANSWER_SENDTIME]?:[SCUtils currentTimeStamp];
    sendAnswer.whoSend = [LoginUserInformation loginUserInformationWithUserName:[SCUserProfileManager sharedInstance].username
                                                         inManagedObjectContext:context];
    [context save:NULL];
    return sendAnswer;
}

+ (NSArray *)loadCurrentUsersAnswersOfQuestionID:(NSString *)question_id inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SEND_ANSWER];
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %@ AND %K=%@", SEND_ANSWER_QUESTION_ID, question_id, SEND_ANSWER_WHOSEND_USERNAM, [SCUserProfileManager sharedInstance].username];
    
    NSError *error;
    return [context executeFetchRequest:request error:&error];
}

- (void)updateStatus:(NSNumber *)status inManagedObjectContext:(NSManagedObjectContext *)context
{
    self.status = status;
    [context save:NULL];
}

- (SCChatMessageStatus)i_messageStatus
{
    return (SCChatMessageStatus)[self.status integerValue];
}

- (BOOL)i_isSender
{
    return YES;
}

- (NSString *)i_avatarURLPath
{
    return self.whoSend.imagefile;
}
//@property (strong, nonatomic) UIImage *i_avatarImage;
- (NSString *)i_text
{
    return self.answer;
}


@end
