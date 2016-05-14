//
//  SCSettingManager.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSettingManager.h"
#import "SAMCSkyWorldAPI.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "AFNetworking.h"

@implementation SCSettingManager

+ (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    if((comment==nil) || (comment.length<=0)){
        // should check in controller
        completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
        return;
    }
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SAMCSkyWorldAPI urlFeedbackWithComment:comment]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    completion(false, [SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
                    completion(true, nil);
                }
            }else{
                completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

+ (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
#warning 11111111111111111111111111111
    //UIImage *headImage = [SCUtils scalingAndCroppingImage:image ForSize:CGSizeMake(120.f, 120.f)];
    UIImage *headImage = image;
    NSData* imageData;
    if (UIImagePNGRepresentation(headImage)) {
        imageData = UIImagePNGRepresentation(headImage);
    }else {
        imageData = UIImageJPEGRepresentation(headImage, 1.0);
    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager POST:[SAMCSkyWorldAPI urlUpdateUserAvatar]
       parameters:nil
constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
    //[formData appendPartWithFormData:imageData name:@"avatarimage"];
    [formData appendPartWithFileData:imageData
                                name:@"image0"
                            fileName:@"avatarImage"
                            mimeType:@"image/jpeg"];
} progress:^(NSProgress *uploadProgress) {
} success:^(NSURLSessionDataTask *task, id responseObject) {
    if([responseObject isKindOfClass:[NSDictionary class]]) {
        DDLogDebug(@"avatar success:%@", responseObject);
        NSDictionary *response = responseObject;
        NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
        NSString *avatarUrlString = [response valueForKeyPath:SKYWORLD_USER_AVATAR_ORIGIN];
        if((errorCode) || (avatarUrlString==nil)){
            completion(false, [SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
            return;
        }
#warning 11111111111111111111111111111
//        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
//        [mainContext performBlockAndWait:^{
//            [LoginUserInformation updateImageFileWithString:avatarUrlString inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
//        }];
        completion(true, nil);
    }else{
        completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
    }
} failure:^(NSURLSessionDataTask *task, NSError *error) {
    DDLogDebug(@"avatar failed:%@", error);
    completion(false, [SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
}];
}

#pragma mark - Check Version
+ (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion
{
    if(!completion){
        return;
    }
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SAMCSkyWorldAPI urlGetLatestIOSClientVersion]
      parameters:nil
        progress:^(NSProgress * _Nonnull downloadProgress) {
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            DDLogDebug(@"Check Version Return:%@", responseObject);
            if([responseObject isKindOfClass:[NSDictionary class]]
               && ([(NSNumber *)responseObject[SKYWORLD_RET] integerValue] == 0)){
                NSNumber *latestVersion = responseObject[SKYWORLD_IOS_NUMBER];
                if(latestVersion == nil){
                    completion(false, nil);
                    return;
                }
                NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
                NSNumber *latestAlertVersion = [userDefaults objectForKey:SC_LATEST_ALERT_VERSION];
                NSString *currentVersionString = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
                NSNumber *currentVersion = [NSNumber numberWithInteger:[currentVersionString integerValue]];
                if (latestAlertVersion == nil) {
                    latestAlertVersion = currentVersion;
                }
                if([latestAlertVersion compare:latestVersion] != NSOrderedSame){
                    completion(true, [latestVersion stringValue]);
                }else{
                    completion(false, nil);
                }
                latestAlertVersion = latestVersion;
                [userDefaults setObject:latestAlertVersion forKey:SC_LATEST_ALERT_VERSION];
            }else{
                completion(false, nil);
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            DDLogDebug(@"Check Version Error:%@", error);
            completion(false, nil);
        }];
}

@end
