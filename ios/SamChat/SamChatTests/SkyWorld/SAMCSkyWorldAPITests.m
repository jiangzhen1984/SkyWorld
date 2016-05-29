//
//  SAMCSkyWorldAPITests.m
//  SamChat
//
//  Created by HJ on 5/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>
#import "SAMCSkyWorldAPI.h"
#import "SAMCUserProfileManager+UnitTest.h"

@interface SAMCSkyWorldAPITests : XCTestCase

@property (nonatomic, strong) id userProfileManagerMock;

@end

@implementation SAMCSkyWorldAPITests

- (void)setUp {
    [super setUp];
    self.userProfileManagerMock = [SAMCUserProfileManager createClassMock];
    LoginData *loginData = [[LoginData alloc] init];
    loginData.token = @"testtoken";
    OCMStub([self.userProfileManagerMock currentLoginData]).andReturn(loginData);
//    LoginData *loginData = [[LoginData alloc] init];
//    loginData.account = @"testaccount";
//    loginData.token = @"testtoken";
//    OCMStub([userProfileManager currentLoginData]).andReturn(loginData);
}

- (void)tearDown {
    [super tearDown];
    [SAMCUserProfileManager clearMock];
}

- (void)testRegisterUrl
{
    NSString *registerUrl = [SAMCSkyWorldAPI urlRegisterWithCellphone:@"12345678901"
                                                          countryCode:@86
                                                             username:@"testuser"
                                                             password:@"testpass"];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:registerUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"register");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.cellphone"], @"12345678901");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.username"], @"testuser");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.country_code"], @86);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.pwd"], @"testpass");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.confirm_pwd"], @"testpass");
    
    registerUrl = [SAMCSkyWorldAPI urlRegisterWithCellphone:nil
                                                countryCode:nil
                                                   username:nil
                                                   password:nil];
    dict = [self dataWithUserApiSeparatedComponentVerification:registerUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"register");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.cellphone"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.username"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.country_code"], @0);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.pwd"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.confirm_pwd"], @"");
}

- (void)testLoginUrl
{
    NSString *loginUrl = [SAMCSkyWorldAPI urlLoginWithUsername:@"testuserlogin"
                                                      passWord:@"testpasslogin"];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:loginUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"login");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.username"], @"testuserlogin");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.pwd"], @"testpasslogin");
    
    loginUrl = [SAMCSkyWorldAPI urlLoginWithUsername:nil passWord:nil];
    dict = [self dataWithUserApiSeparatedComponentVerification:loginUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"login");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.username"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.pwd"], @"");
}

- (void)testLogoutUrl
{
    [self.userProfileManagerMock currentLoginData].token = @"testtoken";
    NSString *logoutUrl = [SAMCSkyWorldAPI urlLogout];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:logoutUrl];
    
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"logout");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"testtoken");
    XCTAssertEqualObjects([dict valueForKey:@"body"], @{});
    
    [self.userProfileManagerMock currentLoginData].token = nil;
    logoutUrl = [SAMCSkyWorldAPI urlLogout];
    dict = [self dataWithUserApiSeparatedComponentVerification:logoutUrl];
    
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"logout");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"");
    XCTAssertEqualObjects([dict valueForKey:@"body"], @{});
}

- (void)testUpgrageUrl
{
    [self.userProfileManagerMock currentLoginData].token = @"testtoken";
    NSString *upgradeUrl = [SAMCSkyWorldAPI urlUpgradeWithArea:@"testarea"
                                                      location:@"testlocation"
                                                   description:@"testdescription"];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:upgradeUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"upgrade");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"testtoken");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.area"], @"testarea");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.location"], @"testlocation");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.desc"], @"testdescription");
    
    [self.userProfileManagerMock currentLoginData].token = nil;
    upgradeUrl = [SAMCSkyWorldAPI urlUpgradeWithArea:nil
                                            location:nil
                                         description:nil];
    dict = [self dataWithUserApiSeparatedComponentVerification:upgradeUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"upgrade");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.area"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.location"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.desc"], @"");
}

- (void)testQueryUserUrl
{
    [self.userProfileManagerMock currentLoginData].token = @"testtoken";
    NSString *queryUserUrl = [SAMCSkyWorldAPI urlQueryUser:@"testuser"];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:queryUserUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"testtoken");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @1);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"username":@"testuser"});
    
    [self.userProfileManagerMock currentLoginData].token = nil;
    queryUserUrl = [SAMCSkyWorldAPI urlQueryUser:nil];
    dict = [self dataWithUserApiSeparatedComponentVerification:queryUserUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @1);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"username":@""});
}

- (void)testQueryUserListUrl
{
    [self.userProfileManagerMock currentLoginData].token = @"testtoken";
    NSString *queryUrl = [SAMCSkyWorldAPI urlQueryUserList:@[@"testuser"]];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:queryUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"testtoken");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @2);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"usernames":@[@"testuser"]});

    queryUrl = [SAMCSkyWorldAPI urlQueryUserList:@[@"testuser1", @"testuser2"]];
    dict = [self dataWithUserApiSeparatedComponentVerification:queryUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"testtoken");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @2);
    NSArray *userArray= @[@"testuser1",@"testuser2"];
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"usernames":userArray});

    [self.userProfileManagerMock currentLoginData].token = nil;
    queryUrl = [SAMCSkyWorldAPI urlQueryUserList:nil];
    dict = [self dataWithUserApiSeparatedComponentVerification:queryUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @2);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"usernames":@[]});
}

- (void)testQueryUserWithOutTokenUrl
{
    [self.userProfileManagerMock currentLoginData].token = @"testtoken";
    NSString *queryUrl = [SAMCSkyWorldAPI urlQueryUserWithoutToken:@"testuser"];
    NSDictionary *dict = [self dataWithUserApiSeparatedComponentVerification:queryUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], nil);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @3);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"username":@"testuser"});
    
    queryUrl = [SAMCSkyWorldAPI urlQueryUserWithoutToken:nil];
    dict = [self dataWithUserApiSeparatedComponentVerification:queryUrl];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"query");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], nil);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @3);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.param"], @{@"username":@""});
}

- (void)testNewQuestionUrl
{
    [self.userProfileManagerMock currentLoginData].token = @"testtoken";
    NSString *url = [SAMCSkyWorldAPI urlNewQuestionWithQuestion:@"testquestion"];
    NSDictionary *dict = [self dataWithQuestionApiSeparatedComponentVerification:url];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"question");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"testtoken");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @1);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.question"], @"testquestion");
    
    [self.userProfileManagerMock currentLoginData].token = nil;
    url = [SAMCSkyWorldAPI urlNewQuestionWithQuestion:nil];
    dict = [self dataWithQuestionApiSeparatedComponentVerification:url];
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"question");
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.token"], @"");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.opt"], @1);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.question"], @"");
}



- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

#pragma mark - Private
- (NSDictionary *)dataWithUserApiSeparatedComponentVerification:(NSString *)url
{
    url = [url stringByRemovingPercentEncoding];
    NSArray *urlStrings = [url componentsSeparatedByString:@"="];
    XCTAssertEqual(2, [urlStrings count]);
    XCTAssertEqualObjects(urlStrings[0], @"http://139.129.57.77/sw/api/1.0/UserAPI?data");
    NSData *data = [urlStrings[1] dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error = nil;
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(dict);
    return dict;
}

- (NSDictionary *)dataWithQuestionApiSeparatedComponentVerification:(NSString *)url
{
    url = [url stringByRemovingPercentEncoding];
    NSArray *urlStrings = [url componentsSeparatedByString:@"="];
    XCTAssertEqual(2, [urlStrings count]);
    XCTAssertEqualObjects(urlStrings[0], @"http://139.129.57.77/sw/api/1.0/QuestionAPI?data");
    NSData *data = [urlStrings[1] dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error = nil;
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(dict);
    return dict;
}

@end
