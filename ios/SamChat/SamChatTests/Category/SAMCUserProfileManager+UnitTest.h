//
//  SAMCUserProfileManager+UnitTest.h
//  SamChat
//
//  Created by HJ on 5/27/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCUserProfileManager.h"

@interface SAMCUserProfileManager (UnitTest)

+ (instancetype)createClassMock;
+ (void)clearMock;

@end
