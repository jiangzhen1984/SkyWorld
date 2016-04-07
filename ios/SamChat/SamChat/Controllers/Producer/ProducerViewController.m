//
//  ProducerViewController.m
//  SamChat
//
//  Created by HJ on 4/1/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "ProducerViewController.h"
#import "ProducerEditViewController.h"

@interface ProducerViewController ()
@property (weak, nonatomic) IBOutlet UITextView *textViewArea;
@property (weak, nonatomic) IBOutlet UITextView *textViewLocation;
@property (weak, nonatomic) IBOutlet UITextView *textViewDescription;

@end

@implementation ProducerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self producerInformationInit];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
    }
}

- (IBAction)backgroundTap:(id)sender
{
    ProducerEditViewController *editViewController = [self.storyboard instantiateViewControllerWithIdentifier:@"ProducerEditView"];
    [self.navigationController pushViewController:editViewController animated:YES];
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
