//
//  AnswerDetailViewController.h
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ReceivedAnswer.h"

@interface AnswerDetailViewController : UIViewController

@property (nonatomic, strong) NSString *question;
@property (nonatomic, strong) ReceivedAnswer *answer;

@end
