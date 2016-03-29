//
//  SCSkyWorldAPIMacro.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#ifndef SCSkyWorldAPIMacro_h
#define SCSkyWorldAPIMacro_h


#define SKYWORLD_API_PREFIX         @"http://121.42.207.185/SkyWorld/api/1.0/"
#define SKYWORLD_APITYPE_USERAPI    @"UserAPI"

#define SKYWORLD_REGISTER       @"register"
#define SKYWORLD_LOGIN          @"login"
#define SKYWORLD_ANSWER         @"answer"

#define SKYWORLD_HEADER         @"header"
#define SKYWORLD_BODY           @"body"

#define SKYWORLD_ACTION         @"action"
#define SKYWORLD_TOKEN          @"token"

#define SKYWORLD_USER           @"user"

#define SKYWORLD_CELLPHONE      @"cellphone"
#define SKYWORLD_USERNAME       @"username"
#define SKYWORLD_USER_USERNAME  @"user.username"
#define SKYWORLD_COUNTRY_CODE   @"country_code"
#define SKYWORLD_PWD            @"pwd"
#define SKYWORLD_CONFIRM_PWD    @"confirm_pwd"

#define SKYWORLD_RET            @"ret"
#define SKYWORLD_ID             @"id"
#define SKYWORLD_MAIL           @"mail"
#define SKYWORLD_TYPE           @"type"
#define SKYWORLD_AVATAR         @"avatar"
#define SKYWORLD_ORIGIN         @"origin"
#define SKYWORLD_AVATAR_ORIGIN  @"avatar.origin"
#define SKYWORLD_LASTUPDATE     @"lastupdate"

#define SKYWORLD_DESC           @"desc"
#define SKYWORLD_AREA           @"area"
#define SKYWORLD_LOCATION       @"location"

#define SKYWORLD_EASEMOB_USERNAME   @"easemob.username"

#define SKYWORLD_CATEGORY       @"category"
#define SKYWORLD_QUESTION       @"question"
#define SKYWORLD_DATETIME       @"datetime"

typedef enum {
    SkyWorldUsernameOrPasswordError = -201,
    SkyWorldInternalError = -103,
    SkyWorldPasswordMismatch = -102,
    SkyWorldUsernameOrPasswordAlreadyExisted = -101,
    SkyWorldActionParameterUnsupported = -2,
    SkyWorldParseFailed = -1,
} SKYWORLD_ERROR_CODE;

#endif /* SCSkyWorldAPIMacro_h */
