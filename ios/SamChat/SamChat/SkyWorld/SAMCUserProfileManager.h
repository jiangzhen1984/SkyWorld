//
//  SAMCUserProfileManager.h
//  SamChat
//
//  Created by HJ on 5/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LoginData : NSObject
@property (nonatomic,copy)  NSString *account;
@property (nonatomic,copy)  NSString *token;
@end

@interface SAMCUserProfileManager : NSObject

+ (instancetype)sharedManager;

@property (nonatomic,strong) LoginData *currentLoginData;

@end