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

@interface LoginViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textUsername;
@property (weak, nonatomic) IBOutlet UITextField *textPassword;
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
    self.buttonShowPassword.hidden = (self.textPassword.text.length <= 0);
}

- (void)changeButtonLoginStatus
{
    BOOL inputOK = (self.textUsername.text.length >= SC_MINIMUM_USERNAME_LENGTH) && (self.textPassword.text.length >= SC_MINIMUM_PASSWORD_LENGTH);
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
    self.textPassword.secureTextEntry = !self.textPassword.secureTextEntry;
    [self.textPassword becomeFirstResponder]; // reset the cursor position
    NSString *buttonTitle = self.textPassword.secureTextEntry ? @"显示" : @"隐藏";
    [self.buttonShowPassword setTitle:buttonTitle forState:UIControlStateNormal];
    [self changeButtonShowPasswordStatus];
}

- (IBAction)login:(UIButton *)sender
{
    [self clearLabelErrorTip];
    
    [self showHudInView:self.view hint:NSLocalizedString(@"login.ongoing", @"Is Login...")];

    [[SamChatClient sharedInstance] loginWithUsername:self.textUsername.text
                                             password:self.textPassword.text
       completion:^(BOOL success, NSError *error) {
           [self hideHud];
           if(success){
               [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
           }else{
               if(error.code == SCSkyWorldErrorUsernameOrPasswordWrong){
                   self.labelErrorTip.text = error.userInfo[NSLocalizedDescriptionKey];
               }else{
                   [self showHint:error.userInfo[NSLocalizedDescriptionKey]];
               }
           }
       }];
}


- (IBAction)usernameDoneEditing:(id)sender
{
    [self.textPassword becomeFirstResponder];
}

- (IBAction)passwordDoneEditing:(id)sender
{
    [sender resignFirstResponder];
    [self login:nil];
}

- (IBAction)backgroundTap:(id)sender
{
    [self.textUsername resignFirstResponder];
    [self.textPassword resignFirstResponder];
}

#pragma mark - Navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    if([segue.identifier isEqualToString:@"signup"]) {
        if([segue.destinationViewController isKindOfClass:[SignupViewController class]]) {
            //SignupViewController *signupVC = (SignupViewController *)segue.destinationViewController;

        }
    }
}


@end
