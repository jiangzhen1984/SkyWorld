//
//  SCViewFactory.m
//  SamChat
//
//  Created by HJ on 3/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCViewFactory.h"

@implementation SCViewFactory

+ (UILabel *)customNavigationItemWithTitle:(NSString *)title
{
    UILabel *customLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 100, 30)];
    [customLabel setTextColor:[UIColor whiteColor]];
    [customLabel setText:title];
    return customLabel;
}

@end
