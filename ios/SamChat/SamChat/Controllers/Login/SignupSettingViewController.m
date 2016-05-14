//
//  SignupSettingViewController.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SignupSettingViewController.h"
#import "SCUtils.h"
#import "SVProgressHUD.h"
#import "SamChatClient.h"
#import "UIView+Toast.h"
#import "SAMCSkyWorldErrorHelper.h"
#import "NTESService.h"
#import "SAMCMainViewController.h"

@interface SignupSettingViewController ()
@property (weak, nonatomic) IBOutlet UITextField *textUsername;
@property (weak, nonatomic) IBOutlet UITextField *textPassword;
@property (weak, nonatomic) IBOutlet UILabel *labelErrorTip;
@end

@implementation SignupSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = @"天际账号设置";
    //self.navigationItem.titleView = [SCViewFactory customNavigationItemWithTitle:@"天际账号设置"];
    [self clearLabelErrorTip];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.textUsername becomeFirstResponder];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
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
    [SVProgressHUD show];
    NSString *username = [self.textUsername.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    NSString *password = self.textPassword.text;
    NSString *cellphone = self.cellphone;
    NSNumber *countrycode = [NSNumber numberWithInteger:[self.countryCode integerValue]];
    
    [self clearLabelErrorTip];
    
    [SVProgressHUD show];
    [[[SamChatClient sharedClient] accountManager] signup:username
                                                 password:password
                                                cellphone:cellphone
                                              countryCode:countrycode
                                               completion:^(NSError *error) {
        [SVProgressHUD dismiss];
        if(error == nil){
           [[[SamChatClient sharedClient] accountManager] login:username
                                                       password:password
                                                     completion:^(NSError *error) {
                 if(error == nil){
                     [[NTESServiceManager sharedManager] start];
                     //NTESMainTabController * mainTab = [[NTESMainTabController alloc] initWithNibName:nil bundle:nil];
                     SAMCMainViewController *mainTab = [[SAMCMainViewController alloc] initWithNibName:nil bundle:nil];
                     [UIApplication sharedApplication].keyWindow.rootViewController = mainTab;
                 }else{
                      if(error.code == SCSkyWorldErrorUsernameOrPasswordWrong){
                          self.labelErrorTip.text = error.userInfo[NSLocalizedDescriptionKey];
                      }else{
                          [self.view makeToast:error.userInfo[NSLocalizedDescriptionKey]
                                      duration:2.0
                                      position:CSToastPositionCenter];
                      }
                 }
            }];
        }else{
            if(error.code == SCSkyWorldErrorUsernameOrPasswordAlreadyExist){
                self.labelErrorTip.text = error.userInfo[NSLocalizedDescriptionKey];
            }else{
                [self.view makeToast:error.userInfo[NSLocalizedDescriptionKey]
                            duration:2.0
                            position:CSToastPositionCenter];
            }
        }
    }];
    
    
//    [[SamChatClient sharedInstance] signupWithCellphone:self.cellphone
//                                            countryCode:[NSNumber numberWithInteger:[self.countryCode integerValue]]
//                                               username:username
//                                               password:password
//        completion:^(BOOL success, NSError *error) {
//            [self hideHud];
//            if(success){
//              [self showHudInView:self.view hint:NSLocalizedString(@"siguplogin.ongoing", @"Signup Success, Is Login...")];
//              [[SamChatClient sharedInstance] loginWithUsername:username
//                   password:password
//                 completion:^(BOOL success, NSError *error) {
//                     [self hideHud];
//                     if(success){
//                         [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
//                     }else{
//                         if(error.code == SCSkyWorldErrorUsernameOrPasswordWrong){
//                             self.labelErrorTip.text = error.userInfo[NSLocalizedDescriptionKey];
//                         }else{
//                             [self showHint:error.userInfo[NSLocalizedDescriptionKey]];
//                         }
//                     }
//                 }];
//            }else{
//              if(error.code == SCSkyWorldErrorUsernameOrPasswordAlreadyExist){
//                  self.labelErrorTip.text = error.userInfo[NSLocalizedDescriptionKey];
//              }else{
//                  [self showHint:error.userInfo[NSLocalizedDescriptionKey]];
//              }
//            }
//        }];
}

- (IBAction)usernameDoneEditing:(id)sender
{
    [self.textPassword becomeFirstResponder];
}

- (IBAction)passwordDoneEditing:(id)sender
{
    [sender resignFirstResponder];
    [self register:nil];
}

- (IBAction)backgroundTap:(id)sender
{
    [self.textUsername resignFirstResponder];
    [self.textPassword resignFirstResponder];
}

@end
