//
//  Utils.m
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCUtils.h"

@implementation SCUtils

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
    //int64_t timestamp = [[NSNumber numberWithDouble:timeInterval] longLongValue] * 1000;
    int64_t timestamp = timeInterval * 1000;
    DDLogDebug(@"time: %lld", timestamp);
    return [NSNumber numberWithLongLong:timestamp];
}

//+ (NSDate *)convertToDateWithTimeStamp:(NSInteger)timeStamp
//{
//    return [NSDate dateWithTimeIntervalSince1970:timeStamp];
//}

+ (NSString *)convertToDateStringWithTimeStamp:(NSTimeInterval)timestamp
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setLocale:[NSLocale currentLocale]];

//    [formatter setDateStyle:NSDateFormatterMediumStyle];
//    [formatter setTimeStyle:NSDateFormatterShortStyle];
    if(timestamp > 140000000000){
        timestamp = timestamp / 1000;
    }
    NSDate *confromTimesp = [NSDate dateWithTimeIntervalSince1970:timestamp];
    [formatter setDateFormat:@"YYYY.MM.dd HH:mm"];
    NSString *str = [formatter stringFromDate:confromTimesp];
    return str;
}

+ (UIStoryboard *)mainStoryBoard
{
    return [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
}


+ (UIImage*)scalingAndCroppingImage:(UIImage*)sourceImage ForSize:(CGSize)targetSize
{
    UIImage *newImage = nil;
    CGSize imageSize = sourceImage.size;
    CGFloat width = imageSize.width;
    CGFloat height = imageSize.height;
    CGFloat targetWidth = targetSize.width;
    CGFloat targetHeight = targetSize.height;
    CGFloat scaleFactor = 0.0;
    CGFloat scaledWidth = targetWidth;
    CGFloat scaledHeight = targetHeight;
    CGPoint thumbnailPoint = CGPointMake(0.0,0.0);
    if (CGSizeEqualToSize(imageSize, targetSize) == NO)
    {
        CGFloat widthFactor = targetWidth / width;
        CGFloat heightFactor = targetHeight / height;
        if (widthFactor > heightFactor)
            scaleFactor = widthFactor; // scale to fit height
        else
            scaleFactor = heightFactor; // scale to fit width
        scaledWidth= width * scaleFactor;
        scaledHeight = height * scaleFactor;
        // center the image
        if (widthFactor > heightFactor)
        {
            thumbnailPoint.y = (targetHeight - scaledHeight) * 0.5;
        }
        else if (widthFactor < heightFactor)
        {
            thumbnailPoint.x = (targetWidth - scaledWidth) * 0.5;
        }
    }
    UIGraphicsBeginImageContext(targetSize); // this will crop
    CGRect thumbnailRect = CGRectZero;
    thumbnailRect.origin = thumbnailPoint;
    thumbnailRect.size.width= scaledWidth;
    thumbnailRect.size.height = scaledHeight;
    [sourceImage drawInRect:thumbnailRect];
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    if(newImage == nil)
        NSLog(@"could not scale image");
    //pop the context to get back to the default
    UIGraphicsEndImageContext();
    return newImage;
}

+ (UIImage *)scaleImage:(UIImage *)sourceImage toMaxSize:(NSInteger)maxsize
{
    UIImage *newImage = sourceImage;
    CGSize newSize = CGSizeMake(sourceImage.size.width, sourceImage.size.height);
    CGFloat heightScale = newSize.height / maxsize;
    CGFloat widthScale = newSize.width / maxsize;
    
    if((heightScale>1.0) || (widthScale>1.0)){
        if(widthScale > heightScale){
            newSize = CGSizeMake(sourceImage.size.width / widthScale, sourceImage.size.height / widthScale);
        }else{
            newSize = CGSizeMake(sourceImage.size.width / heightScale, sourceImage.size.height / heightScale);
        }
        UIGraphicsBeginImageContext(newSize);
        [sourceImage drawInRect:CGRectMake(0,0,newSize.width,newSize.height)];
        newImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
    }
    return newImage;
}

@end
