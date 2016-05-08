//
//  SCSettingManager.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCSettingManager : NSObject

+ (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, NSError *error))completion;
+ (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion;
+ (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion;

@end
