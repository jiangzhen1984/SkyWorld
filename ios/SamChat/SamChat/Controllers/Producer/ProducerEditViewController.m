//
//  ProducerEditViewController.m
//  SamChat
//
//  Created by HJ on 4/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "ProducerEditViewController.h"
#import "AFNetworking.h"

@interface ProducerEditViewController ()
@property (weak, nonatomic) IBOutlet UITextView *textViewArea;
@property (weak, nonatomic) IBOutlet UITextView *textViewLocation;
@property (weak, nonatomic) IBOutlet UITextView *textViewDescription;
@property (weak, nonatomic) IBOutlet UIButton *buttonFinished;


@end

@implementation ProducerEditViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self producerInformationInit];
}

- (void)producerInformationInit
{
    self.textViewArea.text = @"";
    self.textViewLocation.text = @"";
    self.textViewDescription.text = @"";
    LoginUserInformation *loginUserInformation = [[SCUserProfileManager sharedInstance] currentLoginUserInformation];
    if([loginUserInformation.usertype isEqualToNumber:LOGIN_USER_TYPE_SAMVENDOR]){
        self.textViewArea.text = loginUserInformation.area;
        self.textViewLocation.text = loginUserInformation.location;
        self.textViewDescription.text = loginUserInformation.discription;
        [self.buttonFinished setTitle:@"修改" forState:UIControlStateNormal];
    }
}

- (IBAction)editFinished:(UIButton *)sender
{
    DebugLog(@"token: %@", [SCUserProfileManager sharedInstance].token);

    if((self.textViewArea.text.length <= 0)
       || (self.textViewLocation.text.length <=0 )
       || (self.textViewDescription.text.length <= 0)){
        return;
    }

    NSString *urlString = [SCSkyWorldAPI urlUpgradeWithArea:self.textViewArea.text
                                                   location:self.textViewLocation.text
                                                description:self.textViewDescription.text];

    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DebugLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     [self upgradeFailedWithErrorCode:errorCode];
                     return;
                 }
                 [self upgradeSuccessWithResponse:response];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DebugLog(@"Error: %@", error);
         }];
}

- (void)upgradeSuccessWithResponse:(NSDictionary *)response
{
    DebugLog(@"upgrade response %@", response);
    [[SCUserProfileManager sharedInstance] saveCurrentLoginUserInformationWithSkyWorldResponse:response
                                                                                  andOtherInfo:nil];
}

- (void)upgradeFailedWithErrorCode:(NSInteger)errorCode
{
    DebugLog(@"upgrade failed %ld", errorCode);
}

@end
