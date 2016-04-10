//
//  SCUserProfileManager.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUserProfileManager.h"

static SCUserProfileManager *sharedInstance = nil;

@interface SCUserProfileManager ()

@end

@implementation SCUserProfileManager

@synthesize username = _username;
@synthesize token = _token;

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
    if(self){
        
    }
    return self;
}

- (NSString *)username
{
    if(!_username) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        _username = [userDefaults objectForKey:SC_CURRENT_LOGIN_USERNAME];
    }
    return _username;
}

- (void)setUsername:(NSString *)username
{
    if(![_username isEqualToString:username]) {
        _username = username;
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:_username forKey:SC_CURRENT_LOGIN_USERNAME];
    }
}

- (NSString *)token
{
    if(!_token) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        _token = [userDefaults objectForKey:SC_CURRENT_LOGIN_TOKEN];
    }
    return _token;
}

- (void)setToken:(NSString *)token
{
    if(![_token isEqualToString:token]) {
        _token = token;
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:_token forKey:SC_CURRENT_LOGIN_TOKEN];
    }
}

- (LoginUserInformation *)currentLoginUserInformation
{
    LoginUserInformation *currentLoginUserInformation = nil;
    if(self.username) {
        NSManagedObjectContext *context = [SCCoreDataManager sharedInstance].mainObjectContext;
        currentLoginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                      inManagedObjectContext:context];
    }
    return currentLoginUserInformation;
}

- (BOOL)isCurrentUserLoginStatusOK
{
    BOOL status = FALSE;
    LoginUserInformation *currentUserInformation = [self currentLoginUserInformation];
    status = ([currentUserInformation.status integerValue] == SC_LOGINUSER_LOGIN)
             && ([currentUserInformation.easemob_status integerValue] == SC_LOGINUSER_LOGIN)
             && [EMClient sharedClient].isAutoLogin;
    return status;
}

- (void)logOutCurrentUser
{
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        LoginUserInformation *currentLoginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                            inManagedObjectContext:mainContext];
        currentLoginUserInformation.status = SC_LOGINUSER_NO_LOGIN;
        currentLoginUserInformation.easemob_status = SC_LOGINUSER_NO_LOGIN;
        currentLoginUserInformation.password = @"";
        [[SCCoreDataManager sharedInstance] saveContext];
    }];
}

- (void)saveCurrentLoginUserInformationWithSkyWorldResponse:(NSDictionary *)response andOtherInfo:(NSDictionary *)otherInfo
{
    self.username = [response valueForKeyPath:SKYWORLD_USER_USERNAME];
    self.token = response[SKYWORLD_TOKEN];
    //NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:mainContext];
        NSDictionary *userInfo = response[SKYWORLD_USER];
        if(userInfo[SKYWORLD_AREA]){
            loginUserInformation.area = userInfo[SKYWORLD_AREA];
        }
        if(userInfo[SKYWORLD_COUNTRY_CODE]){
            loginUserInformation.countrycode = [NSString stringWithFormat:@"%ld", [userInfo[SKYWORLD_COUNTRY_CODE] integerValue]];
        }
        if(userInfo[SKYWORLD_DESC]){
            loginUserInformation.discription = userInfo[SKYWORLD_DESC];
        }
        loginUserInformation.easemob_status = @SC_LOGINUSER_LOGIN;
        if([userInfo valueForKeyPath:SKYWORLD_EASEMOB_USERNAME]){
            loginUserInformation.easemob_username = [userInfo valueForKeyPath:SKYWORLD_EASEMOB_USERNAME];
        }else{
            loginUserInformation.easemob_username = self.username;
        }
        if([userInfo valueForKeyPath:SKYWORLD_AVATAR_ORIGIN]){
            loginUserInformation.imagefile = [userInfo valueForKeyPath:SKYWORLD_AVATAR_ORIGIN];
        }
        if(userInfo[SKYWORLD_LASTUPDATE]){
            loginUserInformation.lastupdate = userInfo[SKYWORLD_LASTUPDATE];
        }
        if(userInfo[SKYWORLD_LOCATION]){
            loginUserInformation.location = userInfo[SKYWORLD_LOCATION];
        }
        loginUserInformation.logintime = [SCUtils currentTimeStamp];
        if(otherInfo[SKYWORLD_PWD]){
            loginUserInformation.password = otherInfo[SKYWORLD_PWD];
        }
        if(userInfo[SKYWORLD_CELLPHONE]){
            loginUserInformation.phonenumber = userInfo[SKYWORLD_CELLPHONE];
        }
        loginUserInformation.status = @SC_LOGINUSER_LOGIN;
        if(userInfo[SKYWORLD_ID]){
            loginUserInformation.unique_id = userInfo[SKYWORLD_ID];
        }
        if(userInfo[SKYWORLD_TYPE]){
            loginUserInformation.usertype = userInfo[SKYWORLD_TYPE];
        }
        [ContactUser contactUserWithLoginUserInformation:loginUserInformation inManagedObjectContext:mainContext];
        [[SCCoreDataManager sharedInstance] saveContext];
    }];
}

- (void)updateCurrentLoginUserInformationWithEaseMobStatus:(NSInteger)status
{
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:mainContext];
        loginUserInformation.easemob_status = [NSNumber numberWithInteger:status];
        [[SCCoreDataManager sharedInstance] saveContext];
    }];
}

- (void)updateCurrentLoginUserInformationWithEaseMobPushInfo:(NSDictionary *)info
{
    if((!info[SKYWORLD_USERNAME]) || (![info[SKYWORLD_USERNAME] isEqualToString:self.username])){
        return;
    }
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:mainContext];
        if(info[SKYWORLD_CELLPHONE]){
            loginUserInformation.phonenumber = info[SKYWORLD_CELLPHONE];
        }
        if(info[SKYWORLD_AREA]){
            loginUserInformation.area = info[SKYWORLD_AREA];
        }
        if(info[SKYWORLD_LOCATION]){
            loginUserInformation.location = info[SKYWORLD_LOCATION];
        }
        if(info[SKYWORLD_DESC]){
            loginUserInformation.discription = info[SKYWORLD_DESC];
        }
        if([info valueForKeyPath:SKYWORLD_AVATAR_ORIGIN]){
            loginUserInformation.imagefile = [info valueForKeyPath:SKYWORLD_AVATAR_ORIGIN];
        }
        if([info valueForKeyPath:SKYWORLD_EASEMOB_USERNAME]){
            loginUserInformation.easemob_username = [info valueForKeyPath:SKYWORLD_EASEMOB_USERNAME];
        }
        [ContactUser contactUserWithLoginUserInformation:loginUserInformation inManagedObjectContext:mainContext];
        [[SCCoreDataManager sharedInstance] saveContext];
    }];
}

- (void)updateCurrentLoginUserInformationWithUnreadQuestionCountAddOne
{
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:mainContext];
        loginUserInformation.unreadquestioncount = [NSNumber numberWithInteger:([loginUserInformation.unreadquestioncount integerValue]+1)];
        //[[SCCoreDataManager sharedInstance] saveContext];
    }];
}

- (void)clearCurrentLoginUserInformationUnreadQuestionCount
{
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:mainContext];
        loginUserInformation.unreadquestioncount = @0;
        [[SCCoreDataManager sharedInstance] saveContext];
    }];
}

- (void)uploadUserAvatarInBackground:(UIImage*)image completion:(void (^)(BOOL success, NSError *error))completion
{
    UIImage *headImage = [SCUtils scalingAndCroppingImage:image ForSize:CGSizeMake(120.f, 120.f)];
    NSData* imageData;
    if (UIImagePNGRepresentation(headImage)) {
        imageData = UIImagePNGRepresentation(headImage);
    }else {
        imageData = UIImageJPEGRepresentation(headImage, 1.0);
    }
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager POST:[SCSkyWorldAPI urlUpdateUserAvatar]
       parameters:nil
constructingBodyWithBlock:^(id<AFMultipartFormData> formData) {
    [formData appendPartWithFormData:imageData name:@"avatarimage"];
} progress:^(NSProgress *uploadProgress) {
} success:^(NSURLSessionDataTask *task, id responseObject) {
    if([responseObject isKindOfClass:[NSDictionary class]]) {
        DebugLog(@"avatar success:%@", responseObject);
        NSDictionary *response = responseObject;
        NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
        NSString *avatarUrlString = [response valueForKeyPath:SKYWORLD_USER_AVATAR_ORIGIN];
        if((errorCode) || (avatarUrlString==nil)){
            if(completion){
                completion(false, nil);
            }
            return;
        }
        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
        [mainContext performBlockAndWait:^{
            [LoginUserInformation updateImageFileWithString:avatarUrlString inManagedObjectContext:[SCCoreDataManager sharedInstance].mainObjectContext];
        }];
        if (completion){
            completion(true, nil);
        }
    }
} failure:^(NSURLSessionDataTask *task, NSError *error) {
    DebugLog(@"avatar failed:%@", error);
    if (completion) {
        completion(false, error);
    }
}];
}

@end
