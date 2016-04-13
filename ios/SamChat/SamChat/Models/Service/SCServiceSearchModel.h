//
//  SCServiceSearchModel.h
//  SamChat
//
//  Created by HJ on 4/12/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SCServiceSearchModel : NSObject

- (void)resetModel;
- (void)queryTopicListWithOptType:(NSInteger)optType topicType:(NSInteger)topicType completion:(void (^)(BOOL success, NSArray *topics, SCSkyWorldError *error))completion;

@end
