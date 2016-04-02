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

@interface SignupSettingViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UILabel *labelErrorTip;
@property (nonatomic, copy) NSDictionary *registerInfo;

@property (strong, nonatomic) MBProgressHUD *hud;
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
    
    [_hud hide:YES];
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

- (NSString *)generateSignUpUrlString
{
    self.registerInfo = @{SKYWORLD_CELLPHONE: self.cellphone,
                          SKYWORLD_USERNAME: self.username.text,
                          SKYWORLD_PWD: self.password.text,
                          SKYWORLD_COUNTRY_CODE: [NSNumber numberWithInteger:[self.countryCode integerValue]],
                          SKYWORLD_CONFIRM_PWD: self.password.text};
    
    NSString *urlString = [SCSkyWorldAPI urlRegisterWithCellphone:self.registerInfo[SKYWORLD_CELLPHONE]
                                                      countryCode:self.registerInfo[SKYWORLD_COUNTRY_CODE]
                                                         userName:self.registerInfo[SKYWORLD_USERNAME]
                                                         passWord:self.registerInfo[SKYWORLD_PWD]];
    return urlString;
}

- (IBAction)register:(UIButton *)sender
{
    _hud = [SCUtils createHUD];
    _hud.labelText = @"正在注册";
    _hud.userInteractionEnabled = NO;
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager GET:[self generateSignUpUrlString]
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject){
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DebugLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     [self registerErrorWithErrorCode:errorCode];
                     return;
                 }
                 [self registerSuccessWithResponse:response];
                 [self loginEaseMobWithUsername:self.registerInfo[SKYWORLD_USERNAME] password:self.registerInfo[SKYWORLD_PWD]];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DebugLog(@"Error: %@", error);
             _hud.labelText = task.response.description;
             [_hud hide:YES afterDelay:1];
         }];
}

- (void)loginEaseMobWithUsername:(NSString *)username password:(NSString *)password
{
    LoginUserInformation *currentUserInfo = [[SCUserProfileManager sharedInstance] currentLoginUserInformation];
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
                currentUserInfo.easemob_status = @SC_LOGINUSER_LOGIN;
                [LoginUserInformation saveContext];
                [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@YES];

            } else {
                DebugLog(@"EaseMob Login Failed! %@", error);
                currentUserInfo.easemob_status = @SC_LOGINUSER_NO_LOGIN;
                [LoginUserInformation saveContext];
            }
        });
    });
}

- (void)registerSuccessWithResponse:(NSDictionary *)response
{
    SCUserProfileManager *userProfileManager = [SCUserProfileManager sharedInstance];
    [userProfileManager saveCurrentLoginUserInfoWithServerResponse:response andOtherInfo:@{SKYWORLD_PWD:self.registerInfo[SKYWORLD_PWD]}];
}

- (void)registerErrorWithErrorCode:(NSInteger)errorCode
{
    if(errorCode == SkyWorldUsernameOrPasswordAlreadyExisted) {
        self.labelErrorTip.text = @"用户名或密码已经存在，请重新输入";
        [_hud hide:YES];
    } else {
#warning Add details
        _hud.labelText = [NSString stringWithFormat:@"错误代码：%ld", errorCode];
        [_hud hide:YES afterDelay:1];
    }
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
