//
//  SamChatClient.h
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SCLoginModel.h"

@interface SamChatClient : NSObject

+ (instancetype)sharedInstance;

- (void)loginWithUsername:(NSString *)username password:(NSString *)password delegate:(id<SCLoginDelegate>) delegate;

@end
