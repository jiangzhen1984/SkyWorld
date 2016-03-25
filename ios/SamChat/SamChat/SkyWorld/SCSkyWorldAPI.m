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

- (instancetype)initAPI:(NSString *)type WithHeader:(NSDictionary *)header andBody:(NSDictionary *)body
{
    self = [super init];
    if(self) {
        _type = type;
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
        urlStr = [NSString stringWithFormat:@"%@%@?data=%@", SKYWORLD_API_PREFIX, self.type,json];
    }
    return [urlStr stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
}

@end
