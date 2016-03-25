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

@interface SignupSettingViewController ()
@property (weak, nonatomic) IBOutlet UITextField *username;
@property (weak, nonatomic) IBOutlet UITextField *password;


@end

@implementation SignupSettingViewController


- (NSString *)generateUrlString
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

- (void)presentHomeView
{
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
    id homeViewController = [storyboard instantiateViewControllerWithIdentifier:@"HomeView"];
    [self presentViewController:homeViewController animated:YES completion:^(void){}];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
