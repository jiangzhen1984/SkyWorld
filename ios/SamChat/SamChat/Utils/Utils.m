//
//  Utils.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "Utils.h"
#import "MBProgressHUD.h"

@implementation Utils

+ (MBProgressHUD *)createHUD
{
    UIWindow *window = [[UIApplication sharedApplication].windows lastObject];
    MBProgressHUD *hud = [[MBProgressHUD alloc] initWithWindow:window];
    hud.detailsLabelFont = [UIFont boldSystemFontOfSize:16];
    [window addSubview:hud];
    [hud show:YES];
    hud.removeFromSuperViewOnHide = YES;
    return hud;
}

@end
