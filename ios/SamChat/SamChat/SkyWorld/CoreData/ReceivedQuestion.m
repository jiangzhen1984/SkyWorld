//
//  ReceivedQuestion.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "ReceivedQuestion.h"
#import "ContactUser.h"
#import "SCUserProfileManager.h"
#import "SCCoreDataManager.h"

@interface ReceivedQuestion ()

@end

@implementation ReceivedQuestion

+ (ReceivedQuestion *)receivedQuestionWithSkyWorldInfo:(NSDictionary *)questionDictionary inManagedObjectContext:(NSManagedObjectContext *)context
{
    LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:[SCUserProfileManager sharedInstance].username
                                                                                        inManagedObjectContext:context];
    ReceivedQuestion *receivedQuestion = nil;
    NSString *quest_id = [NSString stringWithFormat:@"%@", questionDictionary[SKYWORLD_QUEST_ID]];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_RECEIVED_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@", RECEIVED_QUESTION_QUESTION_ID, quest_id];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)){
        return nil;
    }else if([matches count]){
        receivedQuestion = [matches firstObject];
    }else{
        receivedQuestion = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_RECEIVED_QUESTION
                                                        inManagedObjectContext:context];
        receivedQuestion.question_id = quest_id;
        receivedQuestion.response = RECEIVED_QUESTION_NOTRESPONSED;
    }
    receivedQuestion.question = questionDictionary[SKYWORLD_QUEST];

    if([questionDictionary[SKYWORLD_OPT] isEqualToNumber:@0]){ // new question
        receivedQuestion.status = RECEIVED_QUESTION_VALID;
        receivedQuestion.receivedtime = questionDictionary[SKYWORLD_DATETIME];
    }else{ // cancel question
        receivedQuestion.status = RECEIVED_QUESTION_INVALID;
        receivedQuestion.canceledtime = questionDictionary[SKYWORLD_DATETIME];
    }
    receivedQuestion.receivercellphone = loginUserInformation.phonenumber;
    receivedQuestion.receiverusername = loginUserInformation.username;
    receivedQuestion.fromWho = [ContactUser contactUserWithSkyWorldInfo:questionDictionary[SKYWORLD_ASKER]
                                                 inManagedObjectContext:context];
    if ([context hasChanges]) {
        [context save:NULL];
    }
    [[SCCoreDataManager sharedInstance] saveContext];
    return receivedQuestion;
}

+ (ReceivedQuestion *)receivedQuestionWithQuestionID:(NSString *)questionId inManagedObjectContext:(NSManagedObjectContext *)context
{
    ReceivedQuestion *receivedQuestion = nil;
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_RECEIVED_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %@", RECEIVED_QUESTION_QUESTION_ID, questionId];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if(matches && [matches count]){
        receivedQuestion = [matches firstObject];
    }
    return receivedQuestion;
}

+ (NSArray *)receivedQuestionIDsFrom:(NSString *)username inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_RECEIVED_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(ANY %K == %@) AND (%K==%@)",RECEIVED_QUESTION_FROMWHO_USERNAME,username,RECEIVED_QUESTION_RESPONSE,RECEIVED_QUESTION_NOTRESPONSED];
    request.propertiesToFetch = @[RECEIVED_QUESTION_QUESTION_ID];
    request.returnsDistinctResults = YES;
    request.resultType = NSDictionaryResultType;
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    //DebugLog(@"fetch question ids: %@", matches);
    NSMutableArray *questionIds = nil;
    if(matches && [matches count]){
        questionIds = [[NSMutableArray alloc] init];
        for (NSDictionary *object in matches) {
            [questionIds addObject:object[RECEIVED_QUESTION_QUESTION_ID]];
        }
    }
    return questionIds;
}

+ (NSArray *)unresponsedQuestionIdsFrom:(NSString *)username markResponsed:(BOOL)flag inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_RECEIVED_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"(ANY %K == %@) AND (%K==%@)",RECEIVED_QUESTION_FROMWHO_USERNAME,username,RECEIVED_QUESTION_RESPONSE,RECEIVED_QUESTION_NOTRESPONSED];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    //DebugLog(@"fetch question ids: %@", matches);
    NSMutableArray *questionIds = nil;
    if(matches && [matches count]){
        questionIds = [[NSMutableArray alloc] init];
        for (ReceivedQuestion *object in matches) {
            if(flag){
                object.response = RECEIVED_QUESTION_RESPONSED;
            }
            [questionIds addObject:object.question_id];
        }
    }
    if (flag) {
        [context save:NULL];
        [[SCCoreDataManager sharedInstance] saveContext];
    }
    return questionIds;
}


@end
