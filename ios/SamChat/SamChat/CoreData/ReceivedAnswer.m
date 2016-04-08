//
//  ReceivedAnswer.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "ReceivedAnswer.h"
#import "ContactUser.h"

@interface ReceivedAnswer () <ISCTableCellModel>

@end

@implementation ReceivedAnswer

+ (ReceivedAnswer *)receivedAnswerWithSkyWorldInfo:(NSDictionary *)answerDictionary inManagedObjectContext:(NSManagedObjectContext *)context
{
    DebugLog(@"MyThead: %@", [NSThread currentThread]);
    ReceivedAnswer *receivedAnswer = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_RECEIVED_ANSWER
                                                                   inManagedObjectContext:context];
    receivedAnswer.question_id = [NSString stringWithFormat:@"%ld",[[answerDictionary valueForKeyPath:SKYWORLD_QUEST_QUEST_ID] integerValue]];
    receivedAnswer.answer = [answerDictionary valueForKeyPath:SKYWORLD_ANS_ANSWER];
    receivedAnswer.receivedtime = [SCUtils currentTimeStamp];
    
    receivedAnswer.fromWho = [ContactUser contactUserWithSkyWorldInfo:answerDictionary[SKYWORLD_SYSERVICER]
                                               inManagedObjectContext:context];
    [[SCCoreDataManager sharedInstance] saveContext];
    return receivedAnswer;
}

- (NSString *)i_title
{
    return self.fromWho.username;
}

- (NSString *)i_details
{
    return self.answer;
}

- (NSString *)i_time
{
    //return [NSString stringWithFormat:@"%ld",[self.receivedtime integerValue]];
    return [SCUtils convertToDateStringWithTimeStamp:[self.receivedtime integerValue]];
}

- (NSString *)i_avatarURLPath
{
    return self.fromWho.imagefile;
}

@end
