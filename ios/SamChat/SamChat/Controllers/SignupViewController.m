//
//  SignupViewController.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SignupViewController.h"
#import "SignupSettingViewController.h"

@interface SignupViewController ()
@property (weak, nonatomic) IBOutlet UITextField *cellphone;

@end

@implementation SignupViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    if([segue.identifier isEqualToString:@"signupsetting"]) {
        if([segue.destinationViewController isKindOfClass:[SignupSettingViewController class]]) {
            SignupSettingViewController *signupSettingVC = (SignupSettingViewController *)segue.destinationViewController;
            signupSettingVC.cellphone = self.cellphone.text;
        }
    }
}


@end
