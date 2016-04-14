//
//  SCArticleRecommend.m
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleRecommend.h"
#import "SCArticle.h"

@implementation SCArticleRecommend

+ (void)insertArticleRecommendsWithRecommendsArray:(NSArray *)recommendsArray articleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    if(recommendsArray == nil){
        return;
    }

    for (id recommend in recommendsArray) {
        if(![recommend isKindOfClass:[NSDictionary class]]){
            continue;
        }
        NSDictionary *recommendDictionary = recommend;
        SCArticleRecommend *scarticleRecommend = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SCARTICLERECOMMEND
                                                                           inManagedObjectContext:context];
        
#warning add username timestamp
        scarticleRecommend.timestamp = @0;
        scarticleRecommend.recommender_username = @"";
        scarticleRecommend.recommender_phonenumber = recommendDictionary[SKYWORLD_CELLPHONE];
        scarticleRecommend.fg_id = [NSNumber numberWithInteger:articleId];
    }
    //[context save:NULL];
}

+ (void)clearArticleRecommendsWithArticleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SCARTICLERECOMMEND];
    [request setIncludesPropertyValues:NO]; //only fetch the managedObjectID
    NSError *error;
    NSArray *articleRecommends = [context executeFetchRequest:request error:&error];
    if(articleRecommends && (error == nil)){
        for (NSManagedObject *articleRecommend in articleRecommends) {
            [context deleteObject:articleRecommend];
        }
        //[context save:NULL];
    }
}

+ (void)updateArticleRecommendsWithRecommendsArray:(NSArray *)recommendsArray articleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    [SCArticleRecommend clearArticleRecommendsWithArticleId:articleId
                                     inManagedObjectContext:context];
    [SCArticleRecommend insertArticleRecommendsWithRecommendsArray:recommendsArray
                                                         articleId:articleId
                                            inManagedObjectContext:context];
}
 
@end
