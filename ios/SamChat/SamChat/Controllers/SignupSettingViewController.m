//
//  SignupSettingViewController.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SignupSettingViewController.h"
#import "SCSkyWorldAPI.h"
#import "AFNetworking.h"
#import "MBProgressHUD.h"
#import "SCUtils.h"
#import "SCViewFactory.h"
#import "SCSignupModel.h"

@interface SignupSettingViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UILabel *labelErrorTip;
@end

@implementation SignupSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.titleView = [SCViewFactory customNavigationItemWithTitle:@"天际账号设置"];
    [self clearLabelErrorTip];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.username becomeFirstResponder];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideHud];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - process

- (void)clearLabelErrorTip
{
    self.labelErrorTip.text = @"";
}

#pragma mark - Action
- (IBAction)register:(UIButton *)sender
{
    [self showHudInView:self.view hint:NSLocalizedString(@"signup.ongoing", @"Is signup...")];
    
    NSDictionary *registerInfo = @{SKYWORLD_CELLPHONE: self.cellphone,
                                   SKYWORLD_USERNAME: self.username.text,
                                   SKYWORLD_PWD: self.password.text,
                                   SKYWORLD_COUNTRY_CODE: [NSNumber numberWithInteger:[self.countryCode integerValue]],
                                   SKYWORLD_CONFIRM_PWD: self.password.text};
   // [[SamChatClient sharedInstance] signupWithUserinfoDictionary:registerInfo delegate:self];
    NSString *username = self.username.text;
    NSString *password = self.password.text;
    [[SamChatClient sharedInstance] signupWithUserinfoDictionary:registerInfo
        completion:^(BOOL success, SCSkyWorldError *error) {
            [self hideHud];
            if(success){
              [self showHudInView:self.view hint:NSLocalizedString(@"siguplogin.ongoing", @"Signup Success, Is Login...")];
              [[SamChatClient sharedInstance] loginWithUsername:username
                   password:password
                 completion:^(BOOL success, SCSkyWorldError *error) {
                     [self hideHud];
                     if(success){
                         [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
                     }else{
                         if(error.code == SCSkyWorldErrorUsernameOrPasswordWrong){
                             self.labelErrorTip.text = error.errorDescription;
                         }else{
                             [self showHint:error.errorDescription];
                         }
                     }
                 }];
            }else{
              if(error.code == SCSkyWorldErrorUsernameOrPasswordAlreadyExist){
                  self.labelErrorTip.text = error.errorDescription;
              }else{
                  [self showHint:error.errorDescription];
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
    [self register:nil];
}

- (IBAction)backgroundTap:(id)sender
{
    [self.username resignFirstResponder];
    [self.password resignFirstResponder];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
