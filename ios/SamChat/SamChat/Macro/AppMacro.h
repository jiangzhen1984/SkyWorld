//
//  AppMacro.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#ifndef AppMacro_h
#define AppMacro_h

#pragma mark - Inline function
#define SC_RGB(r,g,b)       [UIColor colorWithRed:((r) / 255.0) green:((g) / 255.0) blue:((b) / 255.0) alpha:1.0]


#define SC_MAIN_COLOR       SC_RGB(2, 168, 244)

#pragma mark - Login User Information

#define SC_LOGINUSER_USERNAME           @"username"
#define SC_LOGINUSER_STATUS             @"status"
#define SC_LOGINUSER_NO_LOGIN           0
#define SC_LOGINUSER_LOGIN              1

#define SC_LOGINUSER_PHONENUMBER        @"phonenumber"
#define SC_LOGINUSER_PASSWORD           @"password"
#define SC_LOGINUSER_USERTYPE           @"usertype"
#define SC_LOGINUSER_IMAGEFILE          @"imagefile"
#define SC_LOGINUSER_DESCRIPTION        @"description"
#define SC_LOGINUSER_AREA               @"area"
#define SC_LOGINUSER_LOCATION           @"location"
#define SC_LOGINUSER_LOGINTIME          @"logintime"
#define SC_LOGINUSER_LOGOUTTIME         @"logouttime"
#define SC_LOGINUSER_UNIQUE_ID          @"unique_id"
#define SC_LOGINUSER_EASEMOB_USERNAME   @"easemob_username"
#define SC_LOGINUSER_EASEMOB_STATUS     @"easemob_status"
#define SC_LOGINUSER_LASTUPDATE         @"lastupdate"

#endif /* AppMacro_h */
