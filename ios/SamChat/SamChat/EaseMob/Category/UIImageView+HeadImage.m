/************************************************************
 *  * EaseMob CONFIDENTIAL
 * __________________
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */


#import "UIImageView+HeadImage.h"

@implementation UIImageView (HeadImage)

- (void)imageWithUsername:(NSString *)username placeholderImage:(UIImage*)placeholderImage
{
    if (placeholderImage == nil) {
        placeholderImage = [UIImage imageNamed:@"chatListCellHead"];
    }
    ContactUser *contactUser = [[SCUserProfileManager sharedInstance] getUserProfileByUsername:username];
    if(contactUser){
        [self sd_setImageWithURL:[NSURL URLWithString:contactUser.imagefile] placeholderImage:placeholderImage];
    }else{
        [self sd_setImageWithURL:nil placeholderImage:placeholderImage];
    }
}

@end

@implementation UILabel (Prase)

- (void)setTextWithUsername:(NSString *)username
{
    ContactUser *contactUser = [[SCUserProfileManager sharedInstance] getUserProfileByUsername:username];
    if(contactUser){
        NSString *nickname = [[SCUserProfileManager sharedInstance] getNickNameWithUsername:username];
        if(nickname.length > 0){
            [self setText:nickname];
            [self setNeedsLayout];
        }else{
            [self setText:username];
        }
    }else{
        [self setText:username];
    }    
}

@end
