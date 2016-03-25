//
//  SCLoginUser.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCLoginUser : NSObject
// response member from server
@property (nonatomic, copy) NSString *username;
@property (nonatomic, copy) NSString *phonenumber;
@property (nonatomic, assign) NSInteger usertype;
@property (nonatomic, copy) NSString *imagefile;
@property (nonatomic, copy) NSString *userdescription;
@property (nonatomic, copy) NSString *area;
@property (nonatomic, copy) NSString *location;
@property (nonatomic, assign) NSInteger unique_id;
@property (nonatomic, copy) NSString *easemob_username;
@property (nonatomic, assign) NSInteger lastupdate;

- (instancetype)initWithDictionary: (NSDictionary *)dict;

@end
