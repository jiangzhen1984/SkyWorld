//
//  SCUserSettingModel.m
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUserSettingModel.h"

@implementation SCUserSettingModel

+ (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    UIImage *headImage = [SCUtils scalingAndCroppingImage:image ForSize:CGSizeMake(120.f, 120.f)];
    NSData* imageData;
    if (UIImagePNGRepresentation(headImage)) {
        imageData = UIImagePNGRepresentation(headImage);
    }else {
        imageData = UIImageJPEGRepresentation(headImage, 1.0);
    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager POST:[SCSkyWorldAPI urlUpdateUserAvatar]
       parameters:nil
constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
    [formData appendPartWithFormData:imageData name:@"avatarimage"];
} progress:^(NSProgress *uploadProgress) {
} success:^(NSURLSessionDataTask *task, id responseObject) {
    if([responseObject isKindOfClass:[NSDictionary class]]) {
        DebugLog(@"avatar success:%@", responseObject);
        NSDictionary *response = responseObject;
        NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
        NSString *avatarUrlString = [response valueForKeyPath:SKYWORLD_USER_AVATAR_ORIGIN];
        if((errorCode) || (avatarUrlString==nil)){
            if(completion){
                completion(false, [SCSkyWorldError errorWithCode:errorCode]);
            }
            return;
        }
        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
        [mainContext performBlockAndWait:^{
            [LoginUserInformation updateImageFileWithString:avatarUrlString inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
        }];
        if (completion){
            completion(true, nil);
        }
    }
} failure:^(NSURLSessionDataTask *task, NSError *error) {
    DebugLog(@"avatar failed:%@", error);
    if (completion) {
        completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
    }
}];
}

@end
