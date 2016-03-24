//
//  SCSkyWorldUserAPI.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 skyworld. All rights reserved.
//

#import "SCSkyWorldUserAPI.h"



@interface SCSkyWorldUserAPI ()
@property (strong, nonatomic) NSDictionary *data;
@end


@implementation SCSkyWorldUserAPI

- (instancetype)initWithHeader:(NSDictionary *)header andBody:(NSDictionary *)body
{
    self = [super init];
    if(self) {
        _data = @{SKYWORLD_HEADER: header, SKYWORLD_BODY: body};
    }
    return self;
}

- (NSURL *)generateUrl
{
    NSString *urlStr = [self generateUrlString];
    NSURL *url = [[NSURL alloc] initWithString:urlStr];
    return url;
}

- (NSString *)generateUrlString
{
    NSString *urlStr;
#warning add errorhandler
    if([NSJSONSerialization isValidJSONObject:self.data]) {
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:self.data
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
        NSString *json = [[NSString alloc] initWithData:jsonData
                                               encoding:NSUTF8StringEncoding];
        urlStr = [NSString stringWithFormat:@"%@%@", SKYWORLD_USERAPI_PREFIX, json];
    }
    return [urlStr stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
}


@end
