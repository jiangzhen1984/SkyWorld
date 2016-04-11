//
//  UserSettingViewController.m
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "UserSettingViewController.h"
#import "ContactListViewController.h"
#import "KYDrawerController.h"
#import "QRCodeScanViewController.h"

@interface UserSettingViewController () <UINavigationControllerDelegate, UIImagePickerControllerDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *imageViewAvatar;
@property (weak, nonatomic) IBOutlet UILabel *labelUserName;

@property (nonatomic, strong) UIImagePickerController *imagePicker;

@end

@implementation UserSettingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.imageViewAvatar.layer.cornerRadius = self.imageViewAvatar.frame.size.width/2;
    self.imageViewAvatar.clipsToBounds = YES;
    self.imageViewAvatar.userInteractionEnabled=YES;
    UITapGestureRecognizer *imageTap =[[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(setUserAvatar)];
    [self.imageViewAvatar addGestureRecognizer:imageTap];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self refreshUserProfile];
}

- (void)refreshUserProfile
{
    LoginUserInformation *currentLoginUserInformation = [SCUserProfileManager sharedInstance].currentLoginUserInformation;
    self.labelUserName.text = currentLoginUserInformation.username;
    NSURL *avatarUrl = [NSURL URLWithString:currentLoginUserInformation.imagefile];
    DebugLog(@"avatar url: %@", avatarUrl);
    //[self.imageViewAvatar sd_setImageWithURL:avatarUrl];
#warning change the placeholder image to the last image
    [self.imageViewAvatar sd_setImageWithURL:avatarUrl placeholderImage:[SCUtils createImageWithColor:[UIColor redColor]]];
    //[self.avatarView sd_setImageWithURL:[NSURL URLWithString:model.avatarURLPath] placeholderImage:model.avatarImage];
}

#pragma mark - getter
- (UIImagePickerController *)imagePicker
{
    if (_imagePicker == nil) {
        _imagePicker = [[UIImagePickerController alloc] init];
        _imagePicker.modalPresentationStyle= UIModalPresentationOverFullScreen;
        _imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        _imagePicker.mediaTypes = @[(NSString *)kUTTypeImage];
        _imagePicker.allowsEditing = YES;
        _imagePicker.delegate = self;
    }
    
    return _imagePicker;
}

#pragma mark - Action
- (void)setUserAvatar
{
    [self presentViewController:self.imagePicker animated:YES completion:NULL];
}

- (IBAction)logout:(UIButton *)sender
{
    __weak typeof(self) weakSelf = self;
    UIView *mainView = [[UIApplication sharedApplication].delegate window];
    [self showHudInView:mainView hint:NSLocalizedString(@"setting.logoutOngoing", @"loging out...")];
    [[SamChatClient sharedInstance] logoutWithCompletion:^(BOOL success, SCSkyWorldError *error) {
        [weakSelf hideHud];
        if(success){
            [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_LOGIN_STATE_CHANGE object:@NO];
        }else{
            [weakSelf showHint:error.errorDescription];
        }
    }];
}

- (IBAction)contact:(UIButton *)sender
{
    ContactListViewController *contactListVC = [[ContactListViewController alloc] initWithNibName:nil bundle:nil];
    DebugLog(@"navigation: %@", self.navigationController);
    [self.navigationController pushViewController:contactListVC animated:YES];
    //[(KYDrawerController *)[self parentViewController] setDrawerState:KYDrawerControllerDrawerStateClosed animated:YES];
}

- (IBAction)QRCodeScan:(UIButton *)sender
{
    QRCodeScanViewController *vc = [[SCUtils mainStoryBoard] instantiateViewControllerWithIdentifier:@"QRCodeScanView"];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - UIImagePickerControllerDelegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info
{
    [self hideHud];
    UIView *mainView = [[UIApplication sharedApplication].delegate window];
    [self showHudInView:mainView hint:NSLocalizedString(@"setting.uploading", @"uploading...")];
    
    __weak typeof(self) weakSelf = self;
    UIImage *orgImage = info[UIImagePickerControllerOriginalImage];
    [picker dismissViewControllerAnimated:YES completion:nil];
    if (orgImage) {
        [[SamChatClient sharedInstance] uploadUserAvatarInBackground:orgImage completion:^(BOOL success, SCSkyWorldError *error) {
            [weakSelf hideHud];
            if(success){
                NSURL *avatarUrl = [NSURL URLWithString:[SCUserProfileManager sharedInstance].currentLoginUserInformation.imagefile];
                [weakSelf.imageViewAvatar sd_setImageWithURL:avatarUrl placeholderImage:weakSelf.imageViewAvatar.image];
                
                [weakSelf showHint:NSLocalizedString(@"setting.uploadSuccess", @"uploaded successfully")];
            }else{
                [weakSelf showHint:NSLocalizedString(@"setting.uploadFail", @"uploaded failed")];
            }
        }];
    } else {
        [self hideHud];
        [self showHint:NSLocalizedString(@"setting.uploadFail", @"uploaded failed")];
    }
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self.imagePicker dismissViewControllerAnimated:YES completion:nil];
}

@end
