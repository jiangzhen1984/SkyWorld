//
//  SCPushManager.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol SCPushDelegate
- (void)didReceivePushData:(NSDictionary *)pushData;
@end

@interface SCPushManager : NSObject

@property (nonatomic, weak) id<SCPushDelegate> delegate;

- (void)asyncWaitingPush;

@end
