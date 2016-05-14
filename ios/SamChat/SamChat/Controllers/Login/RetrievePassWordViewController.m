//
//  RetrievePassWordViewController.m
//  SamChat
//
//  Created by HJ on 3/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "RetrievePassWordViewController.h"

@interface RetrievePassWordViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textfieldCellphone;

@end

@implementation RetrievePassWordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = @"使用手机短信找回密码";
    //self.navigationItem.titleView = [SCViewFactory customNavigationItemWithTitle:@"使用手机短信找回密码"];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.textfieldCellphone becomeFirstResponder];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)backgroundTap:(id)sender
{
    [self.textfieldCellphone resignFirstResponder];
}

@end
