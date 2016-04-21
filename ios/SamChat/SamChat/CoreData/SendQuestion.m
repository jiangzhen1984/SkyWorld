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
    sendQuestion.question = questionInfo[SEND_QUESTION_QUESTION];
    sendQuestion.status = @1; // valid
    sendQuestion.sendtime = [SCUtils currentTimeStamp];

    NSString *sendUsername = [SCUserProfileManager sharedInstance].username;
    sendQuestion.whoSend = [LoginUserInformation loginUserInformationWithUserName:sendUsername
                                                           inManagedObjectContext:context];
    sendQuestion.senderusername = sendUsername;
    
    [[SCCoreDataManager sharedInstance] saveContext];
    return sendQuestion;
}

+ (NSArray *)messagesFromQuestionWithTimeFrom:(NSNumber *)timefrom limit:(NSInteger)count conversationId:(NSString *)conversationId inManagedObjectContext:(NSManagedObjectContext *)context
{
    if((timefrom == nil) || ([timefrom isEqual:[NSNumber numberWithLongLong:0]])){
        timefrom = [SCUtils currentTimeStamp];
    }
    NSString *currentUsername = [SCUserProfileManager sharedInstance].username;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SEND_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K < %@) AND (%K == %@)",SEND_QUESTION_SENDTIME,timefrom,SEND_QUESTION_SENDERUSERNAME,currentUsername];
    request.fetchLimit = count;
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:SEND_QUESTION_SENDTIME ascending:YES]];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    NSMutableArray *messages = nil;
    if ((error==nil) && matches) {
        messages = [[NSMutableArray alloc] init];
        for (SendQuestion *question in matches) {
            NSString *text = [NSString stringWithFormat:@"我的问题：%@", question.question];
            EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:text];
            EMMessage *message = [[EMMessage alloc] initWithConversationID:conversationId
                                                                      from:currentUsername
                                                                        to:conversationId
                                                                      body:body
                                                                       ext:nil];
            message.chatType = EMChatTypeChat;
            message.direction = EMMessageDirectionSend;
            message.timestamp = [question.sendtime longLongValue];
            message.status = EMMessageStatusSuccessed;
            message.isReadAcked = YES;
            message.isDeliverAcked = YES;
            message.isRead = YES;
            message.ext = @{MESSAGE_CONVERSATION_TYPE:CONVERSATION_TYPE_QUESTION};
            [messages addObject:message];
        }
    }
    return messages;
}

@end
