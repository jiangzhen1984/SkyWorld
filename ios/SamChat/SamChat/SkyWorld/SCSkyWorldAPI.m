//
//  SCSkyWorldAPI.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSkyWorldAPI.h"


@interface SCSkyWorldAPI ()

@property (nonatomic, strong) NSDictionary *data;
@property (nonatomic, strong) NSString *type;

@end

@implementation SCSkyWorldAPI


//- (NSString *)generateUrlString
//{
//    NSString *urlStr;
//#warning add errorhandler
//    if([NSJSONSerialization isValidJSONObject:self.data]) {
//        NSError *error;
//        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self.data
//                                                           options:NSJSONWritingPrettyPrinted
//                                                             error:&error];
//        NSString *json = [[NSString alloc] initWithData:jsonData
//                                               encoding:NSUTF8StringEncoding];
//        urlStr = [NSString stringWithFormat:@"%@%@?data=%@", SKYWORLD_API_PREFIX, self.type,json];
//    }
//    return [urlStr stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
//}

+ (NSString *)generateUrlStringWithType:(NSString *)type andData:(NSDictionary *)data
{
    NSString *urlStr;
#warning add errorhandler
    if([NSJSONSerialization isValidJSONObject:data]) {
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
        NSString *json = [[NSString alloc] initWithData:jsonData
                                               encoding:NSUTF8StringEncoding];
        urlStr = [NSString stringWithFormat:@"%@%@?data=%@", SKYWORLD_API_PREFIX, type,json];
    }
    return [urlStr stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
}

#pragma mark - Register JSON Protocol
//API: /api/1.0/UserAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAPI
//Register
//
//{
//    "header":
//    {
//        "action" : "register"
//    },
//    "body" :
//    {
//        "cellphone"    :"1381196123",
//        "username"     : "138",
//        "country_code" : 86, //user count code,
//        "pwd"          : "a",
//        "confirm_pwd"  : "a"
//    }
//}
+ (NSString *)urlRegisterWithCellphone:(NSString *)cellphone countryCode:(NSNumber *)countrycode userName:(NSString *)username passWord:(NSString *)password
{
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_REGISTER};
    NSDictionary *body = @{SKYWORLD_CELLPHONE:cellphone,
                           SKYWORLD_USERNAME:username,
                           SKYWORLD_COUNTRY_CODE:countrycode,
                           SKYWORLD_PWD:password,
                           SKYWORLD_CONFIRM_PWD:password};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Login JSON Protocol
//API: /api/1.0/UserAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAPI
//Login :
//
//{
//    "header":
//    {
//        "action" : "login"
//    },
//    "body" :
//    {
//        "username" : "138",
//        "pwd" : "a"
//    }
//}
+ (NSString *)urlLoginWithUsername:(NSString *)username passWord:(NSString *)password
{
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_LOGIN};
    NSDictionary *body = @{SKYWORLD_USERNAME:username,
                           SKYWORLD_PWD:password};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Logout JSON Protocol
//API: /api/1.0/UserAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAPI
//logout协议：
//
//{
//    "header":
//    {
//        "action" : "logout",
//        "token": "TOKEN-ID"
//    },
//    "body" :
//    {
//    }
//}
+ (NSString *)urlLogout
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_LOGOUT,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Upgrade JSON Protocol
//API: /api/1.0/UserAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAPI
//Upgrade:
//
//{
//    "header":
//    {
//        "action" : "upgrade",
//        "token": "token"
//    },
//    "body":
//    {
//        "area"      : area of businuss convered,
//        "location"  : location of company
//        "desc"      : service description
//    }
//}
+ (NSString *)urlUpgradeWithArea:(NSString *)area location:(NSString *)location description:(NSString *)description
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_UPGRADE,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_AREA:area,
                           SKYWORLD_LOCATION:location,
                           SKYWORLD_DESC:description};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Query JSON Protocol
//API: /api/1.0/UserAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAPI
//{
//    "header":
//    {
//        "action" : "query",
//        "token": "token"
//    },
//    "body":
//    {
//        "opt":1,   1: User Query
//        "param":
//        {
//            "username":"139"
//        }
//    }
//}
+ (NSString *)urlQueryUser:(NSString *)username
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUERY,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@1,
                           SKYWORLD_PARAM:@{
                                   SKYWORLD_USERNAME:username
                                   }};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

//{
//    "header":
//    {
//        "action" : "query"
//        "token": "token",
//    },
//    "body":
//    {
//        "opt":2,   2: User list Query
//        "param":
//        {
//            "usernames":["139", "138", "unam2"]
//        }
//    }
//}
+ (NSString *)urlQueryUserList:(NSArray *)usernameArray
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUERY,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@2,
                           SKYWORLD_PARAM:@{
                                   SKYWORLD_USERNAMES:usernameArray
                                   }};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

//{
//    "header":
//    {
//        "action" : "query"
//    },
//    "body":
//    {
//        "opt":3,   3: User  Query without token
//        "param":
//        {
//            "username":"139"
//        }
//    }
//}
+ (NSString *)urlQueryUserWithoutToken:(NSString *)username
{
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUERY};
    NSDictionary *body = @{SKYWORLD_OPT:@3,
                           SKYWORLD_PARAM:@{
                                   SKYWORLD_USERNAME:username
                                   }};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Relation Query JSON Protocol

#pragma mark - Question JSON Protocol
//API: /api/1.0/QuestionAPI
//http://121.42.207.185/SkyWorld/api/1.0/QuestionAPI
//New Question:
//{
//    "header":
//    {
//        "action": "question",
//        "token":"95189486473904140"
//    },
//    "body":
//    {
//        "opt":1,
//        "question" :"aaa"
//    }
//}
+ (NSString *)urlNewQuestionWithQuestion:(NSString *)question
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUESTION,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@1,
                           SKYWORLD_QUESTION:question};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_QUESTIONAPI andData:data];
}

//Cancle Question
//{
//    "header":
//    {
//        "action": "question",
//        "token":"95189486473904140"
//    },
//    "body":
//    {
//        "opt":2,
//        "question_id" : question_id
//    }
//}
+ (NSString *)urlCancleQuestionWithQuestionID:(NSInteger)questionID
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUESTION,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@2,
                           SKYWORLD_QUESTION_ID:[NSNumber numberWithInteger:questionID]};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_QUESTIONAPI andData:data];
}

// End Question
//{
//    "header":
//    {
//        "action": "question",
//        "token":"95189486473904140"
//    },
//    "body":
//    {
//        "opt":3,
//        "question_id" : question_id
//    }
//}
+ (NSString *)urlEndQuestionWithQuestionID:(NSInteger)questionID
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUESTION,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@3,
                           SKYWORLD_QUESTION_ID:[NSNumber numberWithInteger:questionID]};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_QUESTIONAPI andData:data];
}

// Query Question
//{
//    "header":
//    {
//        "action": "question",
//        "token":"95189486473904140"
//    },
//    "body":
//    {
//        "opt" :4,
//        "act" : [1/2]  1:query question by id  2: query question by asker
//        "question_id"  : question id // this paramter only for act is 1
//        "asker_id"     : asker id // this paramter only for act is 2
//    }
//}
+ (NSString *)urlQueryQuestionWithQuestionID:(NSInteger)questionID
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUESTION,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@4,
                           SKYWORLD_ACT:@1,
                           SKYWORLD_QUESTION_ID:[NSNumber numberWithInteger:questionID]};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_QUESTIONAPI andData:data];
}

+ (NSString *)urlQueryQuestionWithAskerID:(NSInteger)askerID
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_QUESTION,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_OPT:@4,
                           SKYWORLD_ACT:@2,
                           SKYWORLD_QUESTION_ID:[NSNumber numberWithInteger:askerID]};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_QUESTIONAPI andData:data];
}

#pragma mark - Answer JSON Protocol
//API: /api/1.0/QuestionAPI
//http://121.42.207.185/SkyWorld/api/1.0/QuestionAPI
//Answer:
//
//{
//    "header":
//    {
//        "action": "answer",
//        "token":"95056787646578688"
//    },
//    "body":
//    {
//        "answer":"content",
//        "question_id" : quid
//    }
//}
+ (NSString *)urlAnswerQuestion:(NSInteger)questionID withAnswer:(NSString *)answer
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_ANSWER,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_ANSWER:answer,
                           SKYWORLD_QUESTION_ID:[NSNumber numberWithInteger:questionID]};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_QUESTIONAPI andData:data];
}

#pragma mark - Follow JSON Protocol
//API: /api/1.0/UserApi
//http://121.42.207.185/SkyWorld/api/1.0/UserApi
//Follow :
//
//{
//    "header":
//    {
//        "action" : "follow",
//        "token"  : token id
//    },
//    "body" :
//    {
//        "user_id" : userId,
//        "flag"    : [1/2]  1: make follow 2 make unfollow
//        "both"    : [true/false]  true: make relationship for eachother, false only for request user
//    }
//}
+ (NSString *)urlMakeFollow:(BOOL)flag withUser:(NSInteger)userID bothSide:(BOOL)both
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_FOLLOW,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_USER_ID:[NSNumber numberWithInteger:userID],
                           SKYWORLD_FLAG:(flag?@1:@2),
                           SKYWORLD_BOTH:[NSNumber numberWithBool:both]};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Feedback JSON Protocol
//API: /api/1.0/UserAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAPI
//Feedback协议：
//
//{
//    "header":
//    {
//        "action" : "feedback",
//        "token": "TOKEN-ID"
//    },
//    "body" :
//    {
//        "comment" : "feed back"
//    }
//}
+ (NSString *)urlFeedbackWithComment:(NSString *)comment
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_FOLLOW,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_COMMENT:comment};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAPI andData:data];
}

#pragma mark - Update UserAvatar Protocol
//API: /api/1.0/UserAvatarAPI
//http://121.42.207.185/SkyWorld/api/1.0/UserAvatarAPI
//This API only support POST and body must use multipart/form-data
//HTTP - parameter data:
//----------------------------------------------------------------------
//{
//    "header":
//    {
//        "action": "update-avatar",
//        "token":"95056787646578688"
//    },
//    "body":
//    {
//        "type":"1",   for origin avatar now only support one type avatar
//    }
//}
//http boundary  and avatar image data
+ (NSString *)urlUpdateUserAvatar
{
    NSString *token = [SCUserProfileManager sharedInstance].token;
    NSDictionary *header = @{SKYWORLD_ACTION:SKYWORLD_UPDATE_AVATAR,
                             SKYWORLD_TOKEN:token};
    NSDictionary *body = @{SKYWORLD_TYPE:@"1"};
    NSDictionary *data = @{SKYWORLD_HEADER:header,
                           SKYWORLD_BODY:body};
    return [SCSkyWorldAPI generateUrlStringWithType:SKYWORLD_APITYPE_USERAVATARAPI andData:data];
}

@end
