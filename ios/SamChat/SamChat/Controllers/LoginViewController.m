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
    NSDictionary *header = @{SKYWORLD_ACTION: SKYWORLD_LOGIN};
    NSDictionary *body = @{SKYWORLD_USERNAME: self.username.text,
                           SKYWORLD_PWD: self.password.text};
    SCSkyWorldAPI *user = [[SCSkyWorldAPI alloc] initAPI:SKYWORLD_APITYPE_USERAPI
                                              WithHeader:header
                                                 andBody:body];
    return [user generateUrlString];
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
                 [self loginSuccessWithResponse:response];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             _hud.labelText = task.response.description;
             [_hud hide:YES afterDelay:1];
             DebugLog(@"Error: %@", error);
         }];
}

- (void)loginSuccessWithResponse: (NSDictionary *)response
{
    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
    userProfileManager.username = [response valueForKeyPath:SKYWORLD_USER_USERNAME];
    userProfileManager.token = response[SKYWORLD_TOKEN];
    LoginUserInformation *loginUserInformation = [LoginUserInformation infoWithServerResponse:response];
    loginUserInformation.password = self.password.text;
    
    // only logintime
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970];
    int64_t timestamp = [[NSNumber numberWithDouble:timeInterval] longLongValue];
    //DebugLog(@"time: %lld", timestamp);
    loginUserInformation.logintime = [NSNumber numberWithLongLong:timestamp];
    
    [LoginUserInformation saveContext];
    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];
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
