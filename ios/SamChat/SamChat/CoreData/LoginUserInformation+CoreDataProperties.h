//
//  LoginUserInformation+CoreDataProperties.h
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

#import "LoginUserInformation.h"

NS_ASSUME_NONNULL_BEGIN

@interface LoginUserInformation (CoreDataProperties)

@property (nullable, nonatomic, retain) NSString *area;
@property (nullable, nonatomic, retain) NSString *discription;
@property (nullable, nonatomic, retain) NSNumber *easemob_status;
@property (nullable, nonatomic, retain) NSString *easemob_username;
@property (nullable, nonatomic, retain) NSString *imagefile;
@property (nullable, nonatomic, retain) NSNumber *lastupdate;
@property (nullable, nonatomic, retain) NSString *location;
@property (nullable, nonatomic, retain) NSNumber *logintime;
@property (nullable, nonatomic, retain) NSNumber *logouttime;
@property (nullable, nonatomic, retain) NSString *password;
@property (nullable, nonatomic, retain) NSString *phonenumber;
@property (nullable, nonatomic, retain) NSNumber *status;
@property (nullable, nonatomic, retain) NSNumber *unique_id;
@property (nullable, nonatomic, retain) NSString *username;
@property (nullable, nonatomic, retain) NSNumber *usertype;
@property (nullable, nonatomic, retain) NSString *countrycode;

@end

NS_ASSUME_NONNULL_END
