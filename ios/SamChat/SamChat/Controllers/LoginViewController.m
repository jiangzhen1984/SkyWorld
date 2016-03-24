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

@interface LoginViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;

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
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    
    [manager GET:[self generateUrlString]
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject){
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 NSLog(@"%@", responseObject);
                 NSDictionary *result = responseObject;
                 NSInteger errorCode = [(NSNumber *)result[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     
                     return;
                 }
                 
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             NSLog(@"Error: %@", error);
         }];
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
