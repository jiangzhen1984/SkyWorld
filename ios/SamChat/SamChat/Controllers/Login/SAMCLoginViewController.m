//
//  LoginViewController.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCLoginViewController.h"
#import "SignupViewController.h"
#import "SCUtils.h"
#import "SamChatClient.h"
#import "NSString+NTES.h"
#import "UIView+Toast.h"
#import "SVProgressHUD.h"
//#import "NTESMainTabController.h"
#import "SAMCMainViewController.h"
#import "NTESService.h"
#import "SAMCSkyWorldErrorHelper.h"

@interface SAMCLoginViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textUsername;
@property (weak, nonatomic) IBOutlet UITextField *textPassword;
@property (weak, nonatomic) IBOutlet UIButton *buttonShowPassword;
@property (weak, nonatomic) IBOutlet UIButton *buttonLogin;
@property (weak, nonatomic) IBOutlet UILabel *labelErrorTip;

@end

@implementation SAMCLoginViewController

#pragma mark - View Cycle Control
- (void)viewDidLoad
{
    [super viewDidLoad];
    [self clearLabelErrorTip];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
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

#pragma mark - Action
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
    [SVProgressHUD show];
    [self clearLabelErrorTip];
    NSString *username = [self.textUsername.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    NSString *password = self.textPassword.text;
    NSString *loginAccount = username;
    NSString *loginToken = [password tokenByPassword];
    //NSString *loginToken = password;
    
    [[[SamChatClient sharedClient] accountManager] login:loginAccount
                                                password:loginToken completion:^(NSError *error) {
        if (error == nil) {
            [[NTESServiceManager sharedManager] start];
            //NTESMainTabController * mainTab = [[NTESMainTabController alloc] initWithNibName:nil bundle:nil];
            SAMCMainViewController *mainTab = [[SAMCMainViewController alloc] initWithNibName:nil bundle:nil];
            [UIApplication sharedApplication].keyWindow.rootViewController = mainTab;
        }else{
            [SVProgressHUD dismiss];
            if (error.code == SCSkyWorldErrorUsernameOrPasswordWrong) {
                self.labelErrorTip.text = error.userInfo[NSLocalizedDescriptionKey];
            }else{
                NSString *toast = [NSString stringWithFormat:@"登录失败 code: %zd",error.code]; // TODO: fix toast text
                [self.view makeToast:toast duration:2.0 position:CSToastPositionCenter];
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
    if([segue.identifier isEqualToString:@"signup"]) {
        if([segue.destinationViewController isKindOfClass:[SignupViewController class]]) {
            //SignupViewController *signupVC = (SignupViewController *)segue.destinationViewController;
        }
    }
}


@end
