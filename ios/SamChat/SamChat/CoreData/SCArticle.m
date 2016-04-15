//
//  SCArticle.m
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticle.h"
#import "SCArticleComment.h"
#import "SCArticlePicture.h"
#import "SCArticleRecommend.h"

@implementation SCArticle

+ (void)insertArticlesWithSkyWorldInfo:(NSArray *)articleArray inManagedObjectContext:(NSManagedObjectContext *)context
{
    for(id article in articleArray){
        if(![article isKindOfClass:[NSDictionary class]]){
            continue;
        }
        SCArticle *scarticle = nil;
        NSDictionary *articleDictionary = article;
        NSInteger fg_id = [articleDictionary[SKYWORLD_ID] integerValue];
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SCARTICLE];
        request.predicate = [NSPredicate predicateWithFormat:@"%K == %ld",SCARTICLE_FG_ID, fg_id];
        NSError *error;
        NSArray *matches = [context executeFetchRequest:request error:&error];
        if(matches && [matches count]){
            scarticle = [matches firstObject];
        }else{
            scarticle = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SCARTICLE
                                                      inManagedObjectContext:context];
            scarticle.fg_id = [NSNumber numberWithInteger:fg_id];
            scarticle.timestamp = articleDictionary[SKYWORLD_TIMESTAMP];
            scarticle.status = articleDictionary[SKYWORLD_STATUS];
            scarticle.comment = articleDictionary[SKYWORLD_COMMENT] ?:@"";
            
            NSDictionary *publisher = articleDictionary[SKYWORLD_PUBLISHER];
            if(publisher){
                scarticle.publish_username = publisher[SKYWORLD_USERNAME];
                scarticle.publisher_phonenumber = publisher[SKYWORLD_CELLPHONE];
                [[SCUserProfileManager sharedInstance] updateUserProfileByUsername:scarticle.publish_username
                                                                        lastupdate:[publisher[SKYWORLD_LASTUPDATE] integerValue]];
            }
            // insert pics
            NSArray *pics = articleDictionary[SKYWORLD_PICS];
            [SCArticlePicture insertArticlePicturesWithPicsArray:pics
                                                       articleId:fg_id
                                          inManagedObjectContext:context];
        }
        scarticle.owner_username = [SCUserProfileManager sharedInstance].username;
        scarticle.owner_phonenumber = [SCUserProfileManager sharedInstance].currentLoginUserInformation.phonenumber;
        
        scarticle.status = articleDictionary[SKYWORLD_STATUS];
        
        // insert recommends
        NSArray *recommends = articleDictionary[SKYWORLD_RECOMMENDS];
        [SCArticleRecommend updateArticleRecommendsWithRecommendsArray:recommends
                                                             articleId:fg_id
                                                inManagedObjectContext:context];
        
        
        // insert comments
        NSArray *comments = articleDictionary[SKYWORLD_COMMENTS];
        [SCArticleComment updateArticleRecommendsWithCommentsArray:comments
                                                         articleId:fg_id
                                            inManagedObjectContext:context];
        
    }
}

+ (void)asyncInsertArticlesWithSkyWorldInfo:(NSArray *)articleArray completion:(void (^)(BOOL success))completion
{
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateChildObjectContextOfmainContext];
    [privateContext performBlock:^{
        [SCArticle insertArticlesWithSkyWorldInfo:articleArray inManagedObjectContext:privateContext];
        [privateContext save:NULL];
        dispatch_async(dispatch_get_main_queue(), ^{
            [[SCCoreDataManager sharedInstance] saveContext];
            completion(true);
        });
    }];
}

+ (NSArray *)loadArticlesEarlierThan:(NSInteger)timeline maxCount:(NSInteger)count inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SCARTICLE];
    request.predicate = [NSPredicate predicateWithFormat:@"(%K == %@) AND (%K < %ld)",SCARTICLE_OWNER_USERNAME,[SCUserProfileManager sharedInstance].username, SCARTICLE_TIMESTAMP, timeline];
    request.fetchLimit = count;
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:SCARTICLE_TIMESTAMP ascending:NO]];
    return [context executeFetchRequest:request error:NULL];
}

+ (SCArticle *)queryArticleWithArticleId:(NSNumber *)ariticleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SCARTICLE];
    request.predicate = [NSPredicate predicateWithFormat:@"%K == %ld",SCARTICLE_FG_ID, [ariticleId integerValue]];
    return [[context executeFetchRequest:request error:NULL] firstObject];
}

@end
