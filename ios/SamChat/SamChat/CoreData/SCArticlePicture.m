//
//  SCArticlePicture.m
//  SamChat
//
//  Created by HJ on 4/14/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCArticlePicture.h"
#import "SCArticle.h"

@implementation SCArticlePicture

+ (void)insertArticlePicturesWithPicsArray:(NSArray *)picsArray articleId:(NSInteger)articleId inManagedObjectContext:(NSManagedObjectContext *)context
{
    if(picsArray == nil){
        return;
    }
    
    int i = 0;
    for (id pic in picsArray) {
        if(![pic isKindOfClass:[NSDictionary class]]){
            continue;
        }
        NSDictionary *picDictionary = pic;
        SCArticlePicture *scarticlePicture = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_SCARTICLEPICTURE
                                                                           inManagedObjectContext:context];
#warning add thumbnail processing
        scarticlePicture.url_thumbnail = picDictionary[SKYWORLD_URL];
        scarticlePicture.url_original = picDictionary[SKYWORLD_URL];
        scarticlePicture.sequence = [NSNumber numberWithInt:i];
        i++;
        scarticlePicture.fg_id = [NSNumber numberWithInteger:articleId];
    }
    //[context save:NULL];
}





@end
