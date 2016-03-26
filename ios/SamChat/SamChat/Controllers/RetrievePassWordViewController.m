//
//  RetrievePassWordViewController.m
//  SamChat
//
//  Created by HJ on 3/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "RetrievePassWordViewController.h"
#import "SCViewFactory.h"

@interface RetrievePassWordViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textfieldCellphone;

@end

@implementation RetrievePassWordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.titleView = [SCViewFactory customNavigationItemWithTitle:@"使用手机短信找回密码"];
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

#pragma mark - Navigation

/*
// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    }
}
*/
@end
