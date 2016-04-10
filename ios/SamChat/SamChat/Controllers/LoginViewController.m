//
//  LoginViewController.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "LoginViewController.h"
#import "SignupViewController.h"
#import "SCSkyWorldAPI.h"
#import "AFNetworking.h"
#import "MBProgressHUD.h"
#import "SCUtils.h"
#import <sqlite3.h>

@interface LoginViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UIButton *buttonShowPassword;
@property (weak, nonatomic) IBOutlet UIButton *buttonLogin;
@property (weak, nonatomic) IBOutlet UILabel *labelErrorTip;

@end

@implementation LoginViewController

#pragma mark - View Cycle Control

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self clearLabelErrorTip];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self changeButtonShowPasswordStatus];
    [self changeButtonLoginStatus];
    [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:NO];
    [self hideHud];
}

#pragma mark - User Interface Process

- (void)clearLabelErrorTip
{
    self.labelErrorTip.text = @"";
}

- (void)changeButtonShowPasswordStatus
{
    self.buttonShowPassword.hidden = (self.password.text.length <= 0);
}

- (void)changeButtonLoginStatus
{
    BOOL inputOK = (self.username.text.length >= SC_MINIMUM_USERNAME_LENGTH) && (self.password.text.length >= SC_MINIMUM_PASSWORD_LENGTH);
    self.buttonLogin.enabled = inputOK;
}

- (IBAction)usernameEditingChanged:(id)sender
{
    [self clearLabelErrorTip];
}

- (IBAction)passwordEditingChanged:(id)sender
{
    [self changeButtonShowPasswordStatus];
    [self changeButtonLoginStatus];
    [self clearLabelErrorTip];
}

- (IBAction)showPassword:(UIButton *)sender
{
    self.password.secureTextEntry = !self.password.secureTextEntry;
    [self.password becomeFirstResponder]; // reset the cursor position
    NSString *buttonTitle = self.password.secureTextEntry ? @"显示" : @"隐藏";
    [self.buttonShowPassword setTitle:buttonTitle forState:UIControlStateNormal];
    [self changeButtonShowPasswordStatus];
}

- (IBAction)login:(UIButton *)sender
{
    [self clearLabelErrorTip];
    
    [self showHudInView:self.view hint:NSLocalizedString(@"login.ongoing", @"Is Login...")];

    __weak typeof(self) weakSelf = self;
    [[SamChatClient sharedInstance] loginWithUsername:self.username.text
                                             password:self.password.text
       completion:^(BOOL success, SCSkyWorldError *error) {
           [weakSelf hideHud];
           if(success){
               [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
           }else{
               if(error.code == SCSkyWorldErrorUsernameOrPasswordWrong){
                   weakSelf.labelErrorTip.text = error.errorDescription;
               }else{
                   [weakSelf showHint:error.errorDescription];
               }
           }
       }];
}


- (IBAction)usernameDoneEditing:(id)sender
{
    [self.password becomeFirstResponder];
}

- (IBAction)passwordDoneEditing:(id)sender
{
    [sender resignFirstResponder];
    [self login:nil];
}

- (IBAction)backgroundTap:(id)sender
{
    [self.username resignFirstResponder];
    [self.password resignFirstResponder];
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    if([segue.identifier isEqualToString:@"signup"]) {
        if([segue.destinationViewController isKindOfClass:[SignupViewController class]]) {
            //SignupViewController *signupVC = (SignupViewController *)segue.destinationViewController;

        }
    }
}


@end
