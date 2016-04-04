//
//  ContactUser.m
//  
//
//  Created by HJ on 4/4/16.
//
//

#import "ContactUser.h"
#import "ReceivedAnswer.h"
#import "ReceivedQuestion.h"

@implementation ContactUser

+ (ContactUser *)contactUserWithSkyWorldInfo:(NSDictionary *)userDictionary inManagedObjectContext:(NSManagedObjectContext *)context
{
    ContactUser *contactUser = nil;
    DebugLog(@"user: %@", userDictionary);
    NSInteger unique_id = [userDictionary[SKYWORLD_ID] integerValue];
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:ENTITY_CONTACT_USER];
    
    request.predicate = [NSPredicate predicateWithFormat:@"%K = %ld",CONTACT_USER_UNIQUE_ID, unique_id];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    if((!matches) || error || ([matches count] > 1)){
        return nil;
    }else if([matches count]){
        contactUser = [matches firstObject];
    }else{
        contactUser = [NSEntityDescription insertNewObjectForEntityForName:ENTITY_CONTACT_USER
                                                    inManagedObjectContext:context];
        contactUser.unique_id = userDictionary[SKYWORLD_ID];
    }
    contactUser.username = userDictionary[SKYWORLD_USERNAME];
    contactUser.phonenumber = userDictionary[SKYWORLD_CELLPHONE];
    contactUser.usertype = userDictionary[SKYWORLD_TYPE];
    contactUser.imagefile = [userDictionary valueForKeyPath:SKYWORLD_AVATAR_ORIGIN];
    contactUser.desc = userDictionary[SKYWORLD_DESC];
    contactUser.area = userDictionary[SKYWORLD_AREA];
    contactUser.location = userDictionary[SKYWORLD_LOCATION];
    contactUser.easemob_username = [userDictionary valueForKeyPath:SKYWORLD_EASEMOB_USERNAME];
    contactUser.lastupdate = userDictionary[SKYWORLD_LASTUPDATE];

    return contactUser;
}

@end
