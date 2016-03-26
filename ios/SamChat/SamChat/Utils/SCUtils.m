//
//  Utils.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUtils.h"
#import "MBProgressHUD.h"

@implementation SCUtils

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

+ (UIImage *)createImageWithColor: (UIColor *)color
{
    CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

+ (void)presentHomeViewFromViewController:(UIViewController *)viewContoller
{
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    id homeViewController = [storyboard instantiateViewControllerWithIdentifier:@"HomeView"];
    [viewContoller presentViewController:homeViewController animated:YES completion:^(void){}];
}

@end
