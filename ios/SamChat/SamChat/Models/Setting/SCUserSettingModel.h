//
//  SCUserSettingModel.h
//  SamChat
//
//  Created by HJ on 4/10/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCUserSettingModel : NSObject

+ (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;

+ (void)feedbackWithComment:(NSString *)comment completion:(void (^)(BOOL success, SCSkyWorldError *error))completion;
+ (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion;

@end
