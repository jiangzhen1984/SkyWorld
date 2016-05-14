//
//  SAMCUserProfileManager.m
//  SamChat
//
//  Created by HJ on 5/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCUserProfileManager.h"
#import "NTESFileLocationHelper.h"

#define SAMCAccount      @"account"
#define SAMCToken        @"token"

@interface LoginData ()<NSCoding>

@end

@implementation LoginData

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init]) {
        _account = [aDecoder decodeObjectForKey:SAMCAccount];
        _token = [aDecoder decodeObjectForKey:SAMCToken];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)encoder
{
    if ([_account length]) {
        [encoder encodeObject:_account forKey:SAMCAccount];
    }
    if ([_token length]) {
        [encoder encodeObject:_token forKey:SAMCToken];
    }
}
@end

@interface SAMCUserProfileManager ()
@property (nonatomic,copy)  NSString    *filepath;
@end

@implementation SAMCUserProfileManager
+ (instancetype)sharedManager
{
    static SAMCUserProfileManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        NSString *filepath = [[NTESFileLocationHelper getAppDocumentPath] stringByAppendingPathComponent:@"samchat_login_data"];
        instance = [[SAMCUserProfileManager alloc] initWithPath:filepath];
    });
    return instance;
}

- (instancetype)initWithPath:filepath
{
    self = [super init];
    if(self){
        _filepath = filepath;
        [self readData];
    }
    return self;
}

- (void)setCurrentLoginData:(LoginData *)currentLoginData
{
    _currentLoginData = currentLoginData;
    [self saveData];
}

- (void)readData
{
    NSString *filepath = [self filepath];
    if ([[NSFileManager defaultManager] fileExistsAtPath:filepath])
    {
        id object = [NSKeyedUnarchiver unarchiveObjectWithFile:filepath];
        _currentLoginData = [object isKindOfClass:[LoginData class]] ? object : nil;
    }
}

- (void)saveData
{
    NSData *data = [NSData data];
    if (_currentLoginData)
    {
        data = [NSKeyedArchiver archivedDataWithRootObject:_currentLoginData];
    }
    [data writeToFile:[self filepath] atomically:YES];
}

@end
