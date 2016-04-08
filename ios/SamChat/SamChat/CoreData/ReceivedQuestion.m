//
//  ReceivedQuestion.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "ReceivedQuestion.h"
#import "ContactUser.h"

@interface ReceivedQuestion ()

@end

@implementation ReceivedQuestion

@synthesize i_cellHeight = _i_cellHeight;

+ (ReceivedQuestion *)receivedQuestionWithSkyWorldInfo:(NSDictionary *)questionDictionary inManagedObjectContext:(NSManagedObjectContext *)context
{
    LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:[SCUserProfileManager sharedInstance].username
                                                                                        inManagedObjectContext:context];
    DebugLog(@"MyThead: %@", [NSThread currentThread]);
    ReceivedQuestion *receivedQuestion = nil;
    
    NSInteger quest_id = [questionDictionary[SKYWORLD_QUEST_ID] integerValue];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_RECEIVED_QUESTION];
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %@", RECEIVED_QUESTION_QUESTION_ID, [NSString stringWithFormat:@"%ld", quest_id]];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)){
        return nil;
    }else if([matches count]){
        receivedQuestion = [matches firstObject];
    }else{
        receivedQuestion = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_RECEIVED_QUESTION
                                                        inManagedObjectContext:context];
        receivedQuestion.question_id = [NSString stringWithFormat:@"%ld", quest_id];
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

    [[SCCoreDataManager sharedInstance] saveContext];
    return receivedQuestion;
}

- (NSString *)i_title
{
    if([self.response isEqualToNumber:RECEIVED_QUESTION_NOTRESPONSED]){
        return @"新的问题";
    }else{
        return @"已回复问题";
    }
}

- (NSString *)i_details
{
    return self.question;
}

- (NSString *)i_time
{
    return [SCUtils convertToDateStringWithTimeStamp:[self.receivedtime integerValue]];
}

- (NSString *)i_avatarURLPath
{
    return self.fromWho.imagefile;
}

- (BOOL)i_isSender
{
    return NO;
}

//@property (strong, nonatomic) UIImage *i_avatarImage;
- (NSString *)i_text
{
    return self.question;
}

@end
