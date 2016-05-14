//
//  SAMCSearchMessageDataProvider.h
//  SamChat
//
//  Created by HJ on 5/8/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SAMCSearchMessageDataProvider : NSObject<NIMKitMessageProvider>

- (instancetype)initWithSession:(NIMSession *)session;

@end
