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
#import "AppMacro.h"
#import "MBProgressHUD.h"
#import "SCUtils.h"
#import "SCConfig.h"
#import "SCLoginUser.h"
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
                 NSLog(@"%@", responseObject);
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
             NSLog(@"Error: %@", error);
         }];
}

- (void)loginSuccessWithResponse: (NSDictionary *)response
{
    NSLog(@"login success");
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:response[SKYWORLD_TOKEN] ?:@"" forKey:SC_LOGINUSER_TOKEN];
    [userDefaults setObject:self.password.text ?:@"" forKey:SC_LOGINUSER_PASSWORD];
    [userDefaults setInteger:SC_LOGINUSER_LOGIN forKey:SC_LOGINUSER_STATUS];
    
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSince1970];
    int64_t timestamp = [[NSNumber numberWithDouble:timeInterval] longLongValue];
    NSLog(@"time: %lld", timestamp);
    [userDefaults setInteger:timestamp forKey:SC_LOGINUSER_LOGINTIME];
    
//    [userDefaults setInteger:user.easemob_status forKey:SC_LOGINUSER_EASEMOB_STATUS];
    NSDictionary *userDict = response[SKYWORLD_USER];
    SCLoginUser *user = [[SCLoginUser alloc] initWithDictionary:userDict];
    [SCConfig saveProfile:user];
    [self presentHomeView];
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
    [self loginFailed];
}

- (void)loginFailed
{
    NSLog(@"login failed");
    
}

- (void)presentHomeView
{
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    id homeViewController = [storyboard instantiateViewControllerWithIdentifier:@"HomeView"];
    [self presentViewController:homeViewController animated:YES completion:^(void){}];
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
