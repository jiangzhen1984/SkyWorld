//
//  SAMCSettingManager.m
//  SamChat
//
//  Created by HJ on 5/18/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSettingManager.h"
#import "SAMCSkyWorldAPI.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "AFNetworking.h"
#import "SCUtils.h"

@implementation SAMCSettingManager

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.findNewVersion = false;
    }
    return self;
}

- (void)feedbackWithComment:(NSString *)comment completion:(void (^)(NSError *error))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    if((comment==nil) || (comment.length<=0)){
        // should check in controller
        completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
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
                    completion([SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
                }else{
                    completion(nil);
                }
            }else{
                completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
            }
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
        }];
}

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(NSError *error))completion
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
            completion([SAMCSkyWorldErrorHelper errorWithCode:errorCode]);
            return;
        }
#warning 11111111111111111111111111111
        //        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
        //        [mainContext performBlockAndWait:^{
        //            [LoginUserInformation updateImageFileWithString:avatarUrlString inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
        //        }];
        completion(nil);
    }else{
        completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorUnknowError]);
    }
} failure:^(NSURLSessionDataTask *task, NSError *error) {
    DDLogDebug(@"avatar failed:%@", error);
    completion([SAMCSkyWorldErrorHelper errorWithCode:SCSkyWorldErrorServerNotReachable]);
}];
}

#pragma mark - Check Version
- (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion
{
    NSAssert(completion != nil, @"completion block should not be nil");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSNumber *latestCheckTime = [userDefaults objectForKey:SC_LATEST_VERSION_CHECK_TIME];
    NSNumber * currentTimeStamp = [SCUtils currentTimeStamp];
    if ((latestCheckTime != nil) &&
        (([currentTimeStamp longLongValue] - [latestCheckTime longLongValue]) < 24*60*60*1000)) {
        // check only once in 24hours
        completion(false, nil);
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
                //NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
                NSNumber *latestAlertVersion = [userDefaults objectForKey:SC_LATEST_ALERT_VERSION];
                NSString *currentVersionString = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
                NSNumber *currentVersion = [NSNumber numberWithInteger:[currentVersionString integerValue]];
                if (latestAlertVersion == nil) {
                    latestAlertVersion = currentVersion;
                }
                if([latestAlertVersion compare:latestVersion] != NSOrderedSame){
                    self.findNewVersion = true;
                    completion(true, [latestVersion stringValue]);
                }else{
                    completion(false, nil);
                }
                latestAlertVersion = latestVersion;
                [userDefaults setObject:latestAlertVersion forKey:SC_LATEST_ALERT_VERSION];
                [userDefaults setObject:currentTimeStamp forKey:SC_LATEST_VERSION_CHECK_TIME];
            }else{
                completion(false, nil);
            }
        } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
            DDLogDebug(@"Check Version Error:%@", error);
            completion(false, nil);
        }];
}


@end
