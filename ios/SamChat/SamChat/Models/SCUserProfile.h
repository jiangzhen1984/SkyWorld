//
//  SCUserProfile.h
//  SamChat
//
//  Created by HJ on 3/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCUserProfile : NSObject

@property (nonatomic, assign) NSInteger status;
@property (nonatomic, copy) NSString *username;
@property (nonatomic, copy) NSString *phonenumber;
@property (nonatomic, copy) NSString *password;
@property (nonatomic, assign) NSInteger usertype;
@property (nonatomic, copy) NSString *imagefile;
@property (nonatomic, copy) NSString *userdescription;
@property (nonatomic, copy) NSString *area;
@property (nonatomic, copy) NSString *location;
@property (nonatomic, assign) NSInteger logintime;
@property (nonatomic, assign) NSInteger logouttime;
@property (nonatomic, assign) NSInteger unique_id;
@property (nonatomic, copy) NSString *easemob_username;
@property (nonatomic, assign) NSInteger easemob_status;
@property (nonatomic, assign) NSInteger lastupdate;
@property (nonatomic, copy) NSString *token;

- (instancetype)initWithLoginSuccessServerResponse: (NSDictionary *)response;
- (void)saveProfileForLoginSuccess;

@end
