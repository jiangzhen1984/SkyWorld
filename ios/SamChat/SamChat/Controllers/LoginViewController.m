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
@property (nonatomic, copy) NSDictionary *loginInfo;

@property (strong, nonatomic) MBProgressHUD *hud;

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
    [_hud hide:YES];
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

- (NSString *)generateLoginUrlString
{
    self.loginInfo = @{SKYWORLD_USERNAME: self.username.text,
                           SKYWORLD_PWD: self.password.text};
    return [SCSkyWorldAPI urlLoginWithUsername:self.loginInfo[SKYWORLD_USERNAME] passWord:self.loginInfo[SKYWORLD_PWD]];
}


- (IBAction)login:(UIButton *)sender
{
    [self clearLabelErrorTip];
    
    _hud = [SCUtils createHUD];
    _hud.labelText = @"正在登录";
    _hud.userInteractionEnabled = NO;
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager GET:[self generateLoginUrlString]
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject){
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DebugLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     [self loginErrorWithErrorCode:errorCode];
                     return;
                 }
                 [self loginSkyWorldSuccessWithResponse:response];
                 [self loginEaseMobWithUsername:self.loginInfo[SKYWORLD_USERNAME] password:self.loginInfo[SKYWORLD_PWD]];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             _hud.labelText = task.response.description;
             [_hud hide:YES afterDelay:1];
             DebugLog(@"Error: %@", error);
         }];
}

- (void)loginEaseMobWithUsername:(NSString *)username password:(NSString *)password
{
    __weak typeof(self) weakself = self;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        EMError *error = [[EMClient sharedClient] loginWithUsername:username
                                                           password:password];
        dispatch_async(dispatch_get_main_queue(), ^{
//            [weakself hideHud];
            if (!error) {
                DebugLog(@"EaseMob Login Success!");
                //设置是否自动登录
                [[EMClient sharedClient].options setIsAutoLogin:YES];
                [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobStatus:SC_LOGINUSER_LOGIN];
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
/*
                //获取数据库中数据
                [MBProgressHUD showHUDAddedTo:weakself.view animated:YES];
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    [[EMClient sharedClient] dataMigrationTo3];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [[ChatDemoHelper shareHelper] asyncGroupFromServer];
                        [[ChatDemoHelper shareHelper] asyncConversationFromDB];
                        [[ChatDemoHelper shareHelper] asyncPushOptions];
                        [MBProgressHUD hideAllHUDsForView:weakself.view animated:YES];
                        //发送自动登陆状态通知
                        [[NSNotificationCenter defaultCenter] postNotificationName:KNOTIFICATION_LOGINCHANGE object:@YES];
                        
                        //保存最近一次登录用户名
                        [weakself saveLastLoginUsername];
                    });
                });*/
            } else {
                DebugLog(@"EaseMob Login Failed! %@", error);
                [[SCUserProfileManager sharedInstance] updateCurrentLoginUserInformationWithEaseMobStatus:SC_LOGINUSER_NO_LOGIN];
                /*
                switch (error.code)
                {
                      
                        //                    case EMErrorNotFound:
                        //                        TTAlertNoTitle(error.errorDescription);
                        //                        break;
                    case EMErrorNetworkUnavailable:
                        TTAlertNoTitle(NSLocalizedString(@"error.connectNetworkFail", @"No network connection!"));
                        break;
                    case EMErrorServerNotReachable:
                        TTAlertNoTitle(NSLocalizedString(@"error.connectServerFail", @"Connect to the server failed!"));
                        break;
                    case EMErrorUserAuthenticationFailed:
                        TTAlertNoTitle(error.errorDescription);
                        break;
                    case EMErrorServerTimeout:
                        TTAlertNoTitle(NSLocalizedString(@"error.connectServerTimeout", @"Connect to the server timed out!"));
                        break;
                    default:
                        TTAlertNoTitle(NSLocalizedString(@"login.fail", @"Login failure"));
                        break;
                }*/
            }
        });
    });
}


- (void)loginSkyWorldSuccessWithResponse: (NSDictionary *)response
{
    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
//    [userProfileManager saveCurrentLoginUserInfoWithServerResponse:response
//                                                      andOtherInfo:@{SKYWORLD_PWD:self.loginInfo[SKYWORLD_PWD]}];
    [userProfileManager saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                               andOtherInfo:@{SKYWORLD_PWD:self.loginInfo[SKYWORLD_PWD]}];
}

- (void)loginErrorWithErrorCode: (NSInteger)errorCode
{
    if(errorCode == SkyWorldUsernameOrPasswordError) {
        self.labelErrorTip.text = @"用户名或密码错误，请重新输入";
        [_hud hide:YES];
    } else {
#warning Add details
        _hud.labelText = [NSString stringWithFormat:@"错误代码：%ld", errorCode];
        [_hud hide:YES afterDelay:1];
    }
    //[[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@NO];
}

- (NSString *)dataFilePath
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    return [documentsDirectory stringByAppendingPathComponent:@"data.sqlite"];
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
