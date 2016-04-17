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
    //[formData appendPartWithFormData:imageData name:@"avatarimage"];
    [formData appendPartWithFileData:imageData
                                name:@"image0"
                            fileName:@"avatarImage"
                            mimeType:@"image/jpeg"];
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
    }else{
        if(completion){
            completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
        }
    }
} failure:^(NSURLSessionDataTask *task, NSError *error) {
    DebugLog(@"avatar failed:%@", error);
    if (completion) {
        completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
    }
}];
}

+ (void)logoutWithCompletion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = [[EMClient sharedClient] logout:YES];
        dispatch_async(dispatch_get_main_queue(), ^{
            if(error != nil){
                if(completion){
                    completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorLogoutError]);
                }
            }else{
                // [[ApplyViewController shareController] clear];
                AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
                [manager GET:[SCSkyWorldAPI urlLogout]
                  parameters:nil
                    progress:^(NSProgress *downloadProgress){
                    }
                     success:^(NSURLSessionDataTask *task, id responseObject){
                         if([responseObject isKindOfClass:[NSDictionary class]]) {
                             DebugLog(@"%@", responseObject);
                         }
                     }
                     failure:^(NSURLSessionDataTask *task, NSError *error){
                         DebugLog(@"Logout Error: %@", error);
                     }];
                [[SCUserProfileManager sharedInstance] logOutCurrentUser];
                if(completion){
                    completion(true, nil);
                }
            }
        });
    });
}

+ (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion
{
    if((comment==nil) || (comment.length<=0)){
        // should check in controller
        if(completion){
            completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
        }
        return;
    }
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SCSkyWorldAPI urlFeedbackWithComment:comment]
      parameters:nil
        progress:^(NSProgress *downloadProgress) {
        } success:^(NSURLSessionDataTask *task, id responseObject) {
            if([responseObject isKindOfClass:[NSDictionary class]]){
                NSDictionary *response = responseObject;
                NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                if(errorCode){
                    if(completion){
                        completion(false, [SCSkyWorldError errorWithCode:errorCode]);
                    }
                }else{
                    if(completion){
                        completion(true, nil);
                    }
                }
            }else{
                if(completion){
                    completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorUnknowError]);
                }
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            if(completion){
                completion(false, [SCSkyWorldError errorWithCode:SCSkyWorldErrorServerNotReachable]);
            }
        }];
}

#pragma mark - Check Version
+ (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion
{
    if(!completion){
        return;
    }
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[SCSkyWorldAPI urlGetLatestIOSClientVersion]
      parameters:nil
        progress:^(NSProgress * _Nonnull downloadProgress) {
        } success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
            DebugLog(@"Check Version Return:%@", responseObject);
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
            DebugLog(@"Check Version Error:%@", error);
            completion(false, nil);
        }];
}

@end
