//
//  Utils.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@class MBProgressHUD;

@interface SCUtils : NSObject

+ (MBProgressHUD *)createHUD;
+ (UIImage *)createImageWithColor:(UIColor *) color;
+ (void)presentHomeViewFromViewController:(UIViewController *)viewContoller;
+ (NSNumber *)currentTimeStamp;
+ (NSString *)convertToDateStringWithTimeStamp:(NSInteger)timestamp;
+ (UIStoryboard *)mainStoryBoard;
+ (UIImage*)scalingAndCroppingImage:(UIImage*)sourceImage ForSize:(CGSize)targetSize;
+ (UIImage *)scaleImage:(UIImage *)sourceImage toMaxSize:(NSInteger)maxsize;

@end
