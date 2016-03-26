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
#import "SCUserProfile.h"
#import "SCUtils.h"
#import "SCViewFactory.h"

@interface SignupSettingViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;
@property (weak, nonatomic) IBOutlet UILabel *labelErrorTip;

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
    NSDictionary *header = @{SKYWORLD_ACTION: SKYWORLD_REGISTER};
    NSDictionary *body = @{SKYWORLD_CELLPHONE: self.cellphone,
                           SKYWORLD_USERNAME: self.username.text,
                           SKYWORLD_PWD: self.password.text,
                           SKYWORLD_CONFIRM_PWD: self.password.text};
    SCSkyWorldAPI *user = [[SCSkyWorldAPI alloc] initAPI:SKYWORLD_APITYPE_USERAPI
                                              WithHeader:header
                                                 andBody:body];
    return [user generateUrlString];
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
                 NSLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     [self registerErrorWithErrorCode:errorCode];
                     return;
                 }
                 [self registerSuccessWithResponse:response];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             NSLog(@"Error: %@", error);
             _hud.labelText = task.response.description;
             [_hud hide:YES afterDelay:1];
         }];
}

- (void)registerSuccessWithResponse:(NSDictionary *)response
{
    SCUserProfile *userProfile = [[SCUserProfile alloc] initWithLoginSuccessServerResponse:response];
    userProfile.password = self.password.text;
    [userProfile saveProfileForLoginSuccess];
    [SCUtils presentHomeViewFromViewController:self];
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
