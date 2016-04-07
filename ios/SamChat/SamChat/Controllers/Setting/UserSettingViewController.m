//
//  UserSettingViewController.m
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "UserSettingViewController.h"

@interface UserSettingViewController ()

//@property (nonatomic, strong) UIImagePickerController *imagePickerController;

@end

@implementation UserSettingViewController

- (void)selectUserAvatar
{
    UIImagePickerController *imagePickerController = [[UIImagePickerController alloc] init];
    DebugLog(@"selectUserAvatar%@", [UIImagePickerController availableMediaTypesForSourceType:UIImagePickerControllerSourceTypePhotoLibrary]);
    if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary]){
        DebugLog(@"selectUserAvatar in ...");
        imagePickerController.sourceType =  UIImagePickerControllerSourceTypePhotoLibrary;
        imagePickerController.mediaTypes =[UIImagePickerController availableMediaTypesForSourceType:imagePickerController.sourceType];
    }
    imagePickerController.delegate = self;
    imagePickerController.allowsImageEditing = NO;
    [self presentModalViewController:imagePickerController animated:YES];
   // [self.navigationController pushViewController:imagePickerController animated:YES];
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info
{

}

//- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingImage:(UIImage *)image editingInfo:(nullable NSDictionary<NSString *,id> *)editingInfo NS_DEPRECATED_IOS(2_0, 3_0);
//- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info;
//- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker;

@end
