//
//  SAMCSettingManager.h
//  SamChat
//
//  Created by HJ on 5/18/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SAMCSettingManager : NSObject

@property (nonatomic, assign) BOOL findNewVersion;

- (void)feedbackWithComment:(NSString *)comment completion:(void (^)(NSError *error))completion;
- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(NSError *error))completion;
- (void)checkVersionCompletion:(void (^)(BOOL findNew, NSString *versionInfo))completion;

@end
