//
//  SCSearchManager.h
//  SamChat
//
//  Created by HJ on 4/19/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCSearchManager : NSObject

- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType reset:(BOOL)resetflag completion:(void (^)(BOOL success, NSArray *topics, NSError *error))completion;

@end
