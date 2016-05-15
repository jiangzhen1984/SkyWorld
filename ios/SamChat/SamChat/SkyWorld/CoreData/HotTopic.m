//
//  HotTopic.m
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "HotTopic.h"
#import "SCUserProfileManager.h"
#import "SAMCHotTopicCellModel.h"

@implementation HotTopic

+ (NSArray *)hotTopicsWithType:(NSInteger)type inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_HOT_TOPIC];
    if(type != 0){ // not all type
        request.predicate = [NSPredicate predicateWithFormat:@"%K = %ld",HOT_TOPIC_TYPE, type];
    }
    
    NSMutableArray *topics = [[NSMutableArray alloc] init];
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((error==nil) && matches){
        for (HotTopic *object in matches) {
            SAMCHotTopicCellModel *topic = [[SAMCHotTopicCellModel alloc] init];
            topic.type = [object.type integerValue];
            topic.name = object.name;
            [topics addObject:topic];
        }
    }
    return topics;
}

+ (void)updateHotTopicsWithArray:(NSArray<SAMCHotTopicCellModel*> *)topics inManagedObjectContext:(NSManagedObjectContext *)context
{
    if((topics==nil) || ([topics count] <= 0)){
        return;
    }
    [HotTopic clearEntityInManagedObjectContext:context];
    [HotTopic insertHotTopicsWithArray:topics inManagedObjectContext:context];
}

#pragma mark - Private
+ (void)insertHotTopicsWithArray:(NSArray *)topics inManagedObjectContext:(NSManagedObjectContext *)context
{
    for (id topic in topics) {
        if(![topic isKindOfClass:[SAMCHotTopicCellModel class]]){
            return;
        }
        HotTopic *hotTopic = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_HOT_TOPIC
                                                           inManagedObjectContext:context];
        hotTopic.type = [NSNumber numberWithInteger:((SAMCHotTopicCellModel*)topic).type];
        hotTopic.name = ((SAMCHotTopicCellModel*)topic).name;
    }
}

+ (void)clearEntityInManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_HOT_TOPIC];
    [request setIncludesPropertyValues:NO]; //only fetch the managedObjectID
    NSError *error;
    NSArray *topics = [context executeFetchRequest:request error:&error];
    if(topics && (error == nil)){
        for (NSManagedObject *topic in topics) {
            [context deleteObject:topic];
        }
    }
}

@end
