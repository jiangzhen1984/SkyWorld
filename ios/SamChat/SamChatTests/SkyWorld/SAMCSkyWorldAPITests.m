//
//  SAMCSkyWorldAPITests.m
//  SamChat
//
//  Created by HJ on 5/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "SAMCSkyWorldAPI.h"

@interface SAMCSkyWorldAPITests : XCTestCase

@end

@implementation SAMCSkyWorldAPITests

- (void)setUp {
    [super setUp];
}

- (void)tearDown {
    [super tearDown];
}

- (void)testRegisterUrl
{
    NSString *registerUrl = [SAMCSkyWorldAPI urlRegisterWithCellphone:@"12345678901"
                                                          countryCode:@86
                                                             username:@"testuser"
                                                             password:@"testpass"];
    registerUrl = [registerUrl stringByRemovingPercentEncoding];
    NSArray *urlStrings = [registerUrl componentsSeparatedByString:@"="];
    XCTAssertEqual(2, [urlStrings count]);
    XCTAssertEqualObjects(urlStrings[0], @"http://139.129.57.77/sw/api/1.0/UserAPI?data");
    NSData *data = [urlStrings[1] dataUsingEncoding:NSUTF8StringEncoding];
    NSError *error = nil;
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(dict);
    XCTAssertEqualObjects([dict valueForKeyPath:@"header.action"], @"register");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.cellphone"], @"12345678901");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.username"], @"testuser");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.country_code"], @86);
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.pwd"], @"testpass");
    XCTAssertEqualObjects([dict valueForKeyPath:@"body.confirm_pwd"], @"testpass");
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

@end
