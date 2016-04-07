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

+ (NSNumber *)currentTimeStamp
{
    // only logintime
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970];
    int64_t timestamp = [[NSNumber numberWithDouble:timeInterval] longLongValue] * 1000;
    //DebugLog(@"time: %lld", timestamp);
    return [NSNumber numberWithLongLong:timestamp];
}

//+ (NSDate *)convertToDateWithTimeStamp:(NSInteger)timeStamp
//{
//    return [NSDate dateWithTimeIntervalSince1970:timeStamp];
//}

+ (NSString *)convertToDateStringWithTimeStamp:(NSInteger)timestamp
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setLocale:[NSLocale currentLocale]];

//    [formatter setDateStyle:NSDateFormatterMediumStyle];
//    [formatter setTimeStyle:NSDateFormatterShortStyle];
    NSTimeInterval timeInterval = ((double)timestamp)/1000.0;
    NSDate *confromTimesp = [NSDate dateWithTimeIntervalSince1970:timeInterval];
    [formatter setDateFormat:@"YYYY.MM.dd HH:mm"];
    NSString *str = [formatter stringFromDate:confromTimesp];
    return str;
}

+ (UIStoryboard *)mainStoryBoard
{
    return [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
}

@end
