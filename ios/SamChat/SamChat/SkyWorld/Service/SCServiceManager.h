//
//  SCServiceManager.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCServiceManager : NSObject

+ (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType currentCount:(NSInteger)currentCount updateTimePre:(NSTimeInterval)updateTimePre completion:(void (^)(BOOL success, NSDictionary *response, NSError *error))completion;

@end
