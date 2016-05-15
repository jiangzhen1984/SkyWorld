//
//  SAMCSessionManager.h
//  SamChat
//
//  Created by HJ on 5/15/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SAMCSessionManager : NSObject

- (void)setExtOfSessionWithMessage:(NIMMessage *)message;

- (void)updateSession:(NSString *)sessionId tagType:(NSNumber *)tagType value:(BOOL)flag;

- (BOOL)searchTagOfSession:(NSString *)sessionId;
- (BOOL)chatTagOfSession:(NSString *)sessionId;
- (BOOL)serviceTagOfSession:(NSString *)sessionId;

- (BOOL)deleteSession:(NSString *)sessionId ifNeededAfterClearTagType:(NSNumber *)tagType;

@end
