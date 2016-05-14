//
//  SAMCSearchManager.h
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SAMCSearchManager : NSObject

- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType currentCount:(NSInteger)currentCount updateTimePre:(NSTimeInterval)updateTimePre completion:(void (^)(NSDictionary *response, NSError *error))completion;
- (void)sendNewQuestion:(NSString *)question completion:(void (^)(NSError *error))completion;

@end
