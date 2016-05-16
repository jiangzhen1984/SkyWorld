//
//  SAMCCountryCodeController.h
//  SamChat
//
//  Created by HJ on 5/16/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SAMCCountryCodeController : UIViewController

@property (nonatomic, copy) void(^selecteCountryCodeBlock)(NSString *countryCode);

@end
