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

    NSDictionary *info = @{SKYWORLD_AREA:self.textViewArea.text,
                           SKYWORLD_LOCATION:self.textViewLocation.text,
                           SKYWORLD_DESC:self.textViewDescription.text};
    
    __weak typeof(self) weakSelf = self;
    [[SamChatClient sharedInstance] upgradeToProducerWithInformationDictionary:info completion:^(BOOL success, SCSkyWorldError *error) {
        if(success){
            [weakSelf showHint:@"成功升级为服务者"];
            [weakSelf.navigationController popViewControllerAnimated:YES];
        }else{
            [weakSelf showHint:@"升级失败"];
        }
    }];
}

@end
