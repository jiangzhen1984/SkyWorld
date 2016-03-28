//
//  SCUserProfileManager.m
//  SamChat
//
//  Created by HJ on 3/28/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUserProfileManager.h"

static SCUserProfileManager *sharedInstance = nil;

@implementation SCUserProfileManager

+ (instancetype)sharedInstance
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
    if(self){
        
    }
    return self;
}

@end
