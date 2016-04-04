//
//  ISCTableCellModel.h
//  SamChat
//
//  Created by HJ on 4/3/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

@protocol ISCTableCellModel <NSObject>

@property (readonly, strong, nonatomic) NSString *i_title;
@property (readonly, strong, nonatomic) NSString *i_details;
@property (readonly, strong, nonatomic) NSString *i_time;
@property (readonly, strong, nonatomic) NSString *i_avatarURLPath;
//@property (readonly, strong, nonatomic) UIImage *avatarImage;

@end

