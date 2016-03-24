//
//  LoginViewController.m
//  SamChat
//
//  Created by HJ on 3/23/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "LoginViewController.h"
#import "SignupViewController.h"
#import "SCSkyWorldUserAPI.h"
#import "AFNetworking.h"
#import "AppMacro.h"
#import "MBProgressHUD.h"
#import "Utils.h"
#import "Config.h"
#import "SCLoginUser.h"
#import <sqlite3.h>

@interface LoginViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;

@property (strong, nonatomic) MBProgressHUD *hud;

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:NO];
    [_hud hide:YES];
}

- (NSString *)generateUrlString
{
    NSDictionary *header = @{SKYWORLD_ACTION: SKYWORLD_LOGIN};
    NSDictionary *body = @{SKYWORLD_USERNAME: self.username.text,
                           SKYWORLD_PWD: self.password.text};
    SCSkyWorldUserAPI *user = [[SCSkyWorldUserAPI alloc] initWithHeader:header
                                                                andBody:body];
    return [user generateUrlString];
}


- (IBAction)login:(UIButton *)sender
{
    _hud = [Utils createHUD];
    _hud.labelText = @"正在登录";
    _hud.userInteractionEnabled = NO;
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager GET:[self generateUrlString]
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject){
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 NSLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     _hud.labelText = [NSString stringWithFormat:@"错误：%@", response];
                     [_hud hide:YES afterDelay:1];
                     [self loginFailed];
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
    [userDefaults setObject:response[SKYWORLD_TOKEN] ?:@"" forKey:SKYWORLD_TOKEN];
    NSDictionary *userDict = response[SKYWORLD_USER];
    if(userDict) {
        
    }
    [Config saveProfile:[[SCLoginUser alloc] initWithDictionary:response[SKYWORLD_USER] andStatus:SC_LOGINUSER_LOGIN]];
    [self presentHomeView];
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
