//
//  SCArticleComment.m
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticleComment.h"

@implementation SCArticleComment

+ (void)insertArticleCommentsWithCommentsArray:(NSArray *)commentsArray articleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    if(commentsArray == nil){
        return;
    }
    
    for (id comment in commentsArray) {
        if(![comment isKindOfClass:[NSDictionary class]]){
            continue;
        }
        NSDictionary *commentDictionary = comment;
        SCArticleComment *scarticleComment = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SCARTICLECOMMENT
                                                                               inManagedObjectContext:context];
        scarticleComment.timestamp = commentDictionary[SKYWORLD_TIMESTAMP];
        scarticleComment.content = commentDictionary[SKYWORLD_CONTENT];
#warning add usename or change to user id
        scarticleComment.commenter_username = @"";
        scarticleComment.commenter_phonenumber = [commentDictionary valueForKeyPath:SKYWORLD_USER_CELLPHONE];
        scarticleComment.fg_id = [NSNumber numberWithInteger:articleId];
    }
    //[context save:NULL];
}

+ (void)clearArticleCommentsWithArticleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_SCARTICLECOMMENT];
    [request setIncludesPropertyValues:NO]; //only fetch the managedObjectID
    NSError *error;
    NSArray *articleComments = [context executeFetchRequest:request error:&error];
    if(articleComments && (error == nil)){
        for (NSManagedObject *articleComment in articleComments) {
            [context deleteObject:articleComment];
        }
        //[context save:NULL];
    }
}

+ (void)updateArticleRecommendsWithCommentsArray:(NSArray *)commentsArray articleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    [SCArticleComment clearArticleCommentsWithArticleId:articleId
                                 inManagedObjectContext:context];
    [SCArticleComment insertArticleCommentsWithCommentsArray:commentsArray
                                                   articleId:articleId
                                      inManagedObjectContext:context];
}


@end
