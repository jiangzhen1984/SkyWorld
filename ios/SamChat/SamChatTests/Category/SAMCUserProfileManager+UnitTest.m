//
//  SAMCUserProfileManager+UnitTest.m
//  SamChat
//
//  Created by HJ on 5/27/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCUserProfileManager+UnitTest.h"
#import "SwizzlingDefine.h"
#import <OCMock/OCMock.h>

static SAMCUserProfileManager *mock = nil;

@implementation SAMCUserProfileManager (UnitTest)

+ (instancetype)swizzling_sharedManager
{
    if (mock) {
        return mock;
    }else{
        return [SAMCUserProfileManager swizzling_sharedManager];
    }
}

+ (instancetype)createClassMock
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
//        swizzling_exchangeMethod([SAMCUserProfileManager class] ,@selector(sharedManager), @selector(swizzling_sharedManager));
        Class clazz = [SAMCUserProfileManager class];
        SEL originalSelector = @selector(sharedManager);
        SEL swizzledSelector = @selector(swizzling_sharedManager);
        Method originalMethod = class_getClassMethod(clazz, originalSelector);
        Method swizzledMethod = class_getClassMethod(clazz, swizzledSelector);
        
        clazz = object_getClass((id)clazz);
        BOOL success = class_addMethod(clazz, originalSelector, method_getImplementation(swizzledMethod), method_getTypeEncoding(swizzledMethod));
        if (success) {
            class_replaceMethod(clazz, swizzledSelector, method_getImplementation(originalMethod), method_getTypeEncoding(originalMethod));
        } else {
            method_exchangeImplementations(originalMethod, swizzledMethod);
        }
    });
    mock = [OCMockObject mockForClass:[SAMCUserProfileManager class]];
    return mock;
}

+ (void)clearMock
{
    mock = nil;
}

@end
