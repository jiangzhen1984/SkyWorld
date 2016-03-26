//
//  ResetPassWordViewController.m
//  SamChat
//
//  Created by HJ on 3/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "ResetPassWordViewController.h"
#import "SCViewFactory.h"

@interface ResetPassWordViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textfieldUsername;
@property (weak, nonatomic) IBOutlet UITextField *textfieldPassword;



@end

@implementation ResetPassWordViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.titleView = [SCViewFactory customNavigationItemWithTitle:@"重设密码"];
}

- (IBAction)resetPassword:(UIButton *)sender
{
}

@end
