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
        NSManagedObjectContext *context = [[SCCoreDataManager sharedInstance] managedObjectContext];
        currentLoginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                      inManagedObjectContext:context];
    }
    return currentLoginUserInformation;
}

//- (void)saveLoginUserInformation
//{
//    NSManagedObjectContext *context = [[SCCoreDataManager sharedInstance] managedObjectContext];
//    [context save:NULL];
//}

- (BOOL)isCurrentUserLoginStatusOK
{
    BOOL status = FALSE;
    LoginUserInformation *currentUserInformation = [self currentLoginUserInformation];
    status = ([currentUserInformation.status integerValue] == SC_LOGINUSER_LOGIN)
             && ([currentUserInformation.easemob_status integerValue] == SC_LOGINUSER_LOGIN)
             && [EMClient sharedClient].isAutoLogin;
    return status;
}

- (void)saveCurrentLoginUserInformationWithSkyWorldResponse:(NSDictionary *)response andOtherInfo:(NSDictionary *)otherInfo
{
    self.username = [response valueForKeyPath:SKYWORLD_USER_USERNAME];
    self.token = response[SKYWORLD_TOKEN];
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
    [privateContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:privateContext];
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
            loginUserInformation.imagefile = [self downloadAvatarFrom:[userInfo valueForKeyPath:SKYWORLD_AVATAR_ORIGIN]];
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
        [privateContext save:NULL];
    }];
}

- (void)updateCurrentLoginUserInformationWithEaseMobStatus:(NSInteger)status
{
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
    [privateContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:privateContext];
        loginUserInformation.easemob_status = [NSNumber numberWithInteger:status];
        [privateContext save:NULL];
    }];
}

- (void)updateCurrentLoginUserInformationWithEaseMobPushInfo:(NSDictionary *)info
{
    if((!info[SKYWORLD_USERNAME]) || (![info[SKYWORLD_USERNAME] isEqualToString:self.username])){
        return;
    }
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
    [privateContext performBlockAndWait:^{
        LoginUserInformation *loginUserInformation = [LoginUserInformation loginUserInformationWithUserName:self.username
                                                                                     inManagedObjectContext:privateContext];
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
            loginUserInformation.imagefile = [self downloadAvatarFrom:[info valueForKeyPath:SKYWORLD_AVATAR_ORIGIN]];
        }
        if([info valueForKeyPath:SKYWORLD_EASEMOB_USERNAME]){
            loginUserInformation.easemob_username = [info valueForKeyPath:SKYWORLD_EASEMOB_USERNAME];
        }
        [privateContext save:NULL];
    }];
}

- (NSString *)downloadAvatarFrom:(NSString *)urlStr
{
#warning add image file download
    return @"test";
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
        //[[EMSDWebImagePrefetcher sharedImagePrefetcher] prefetchURLs:@[avatarUrlString]];
        [LoginUserInformation updateImageFileWithString:avatarUrlString inManagedObjectContext:[SCCoreDataManager sharedInstance].managedObjectContext];
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
