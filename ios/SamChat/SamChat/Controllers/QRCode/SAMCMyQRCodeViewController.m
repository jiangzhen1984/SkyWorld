//
//  SAMCMyQRCodeViewController.m
//  SamChat
//
//  Created by HJ on 5/17/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCMyQRCodeViewController.h"
#import "NIMAvatarImageView.h"
#import "SamChatClient.h"
#import <ZXingObjC/ZXingObjC.h>

@interface SAMCMyQRCodeViewController ()

@property (nonatomic, strong) UIView *backgroundView;
@property (nonatomic, strong) NIMAvatarImageView *avatarView;
@property (nonatomic, strong) UILabel *nameLabel;
@property (nonatomic, strong) UIImageView *qrcodeView;

@end

@implementation SAMCMyQRCodeViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self setupSubviews];
}

- (void)setupSubviews
{
    self.view.backgroundColor = [UIColor lightGrayColor];
    
    _backgroundView = [[UIView alloc] init];
    _backgroundView.translatesAutoresizingMaskIntoConstraints = NO;
    _backgroundView.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:_backgroundView];
    
    
    NSString *uid = [[SamChatClient sharedClient].accountManager currentAccount];
    NIMKitInfo *info = [[NIMKit sharedKit] infoByUser:uid];
    
    _avatarView = [[NIMAvatarImageView alloc] init];
    _avatarView.translatesAutoresizingMaskIntoConstraints = NO;
    [_avatarView nim_setImageWithURL:[NSURL URLWithString:info.avatarUrlString] placeholderImage:info.avatarImage options:NIMWebImageRetryFailed];
    [_backgroundView addSubview:_avatarView];
    
    _nameLabel = [[UILabel alloc] init];
    _nameLabel.translatesAutoresizingMaskIntoConstraints = NO;
    _nameLabel.text = info.showName;
    [_backgroundView addSubview:_nameLabel];
    
    _qrcodeView = [[UIImageView alloc] init];
    _qrcodeView.translatesAutoresizingMaskIntoConstraints = NO;
    _qrcodeView.image = [self qrImageWithString:uid];
    [_backgroundView addSubview:_qrcodeView];
    
    [_backgroundView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-50-[_avatarView(70)]-5-[_nameLabel]-50-|"
                                                                            options:0
                                                                            metrics:nil
                                                                              views:NSDictionaryOfVariableBindings(_avatarView,_nameLabel)]];
    [_backgroundView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-20-[_avatarView(70)]-20-[_qrcodeView]-20-|"
                                                                            options:0
                                                                            metrics:nil
                                                                              views:NSDictionaryOfVariableBindings(_avatarView,_qrcodeView)]];
    [_backgroundView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-20-[_nameLabel]"
                                                                            options:0
                                                                            metrics:nil
                                                                              views:NSDictionaryOfVariableBindings(_nameLabel)]];
    [_backgroundView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-50-[_qrcodeView]-50-|"
                                                                            options:0
                                                                            metrics:nil
                                                                              views:NSDictionaryOfVariableBindings(_qrcodeView)]];
    
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-20-[_backgroundView]-20-|"
                                                                      options:0
                                                                      metrics:nil
                                                                        views:NSDictionaryOfVariableBindings(_backgroundView)]];
    [self.view addConstraint:[NSLayoutConstraint constraintWithItem:_backgroundView
                                                          attribute:NSLayoutAttributeTop
                                                          relatedBy:NSLayoutRelationEqual
                                                             toItem:self.view
                                                          attribute:NSLayoutAttributeTop
                                                         multiplier:1.0
                                                           constant:152]];
    [_qrcodeView addConstraint:[NSLayoutConstraint constraintWithItem:_qrcodeView
                                                            attribute:NSLayoutAttributeHeight
                                                            relatedBy:NSLayoutRelationEqual
                                                               toItem:_qrcodeView
                                                            attribute:NSLayoutAttributeWidth
                                                           multiplier:1.0
                                                             constant:0.0]];
}

#pragma mark - Private
- (UIImage *)qrImageWithString:(NSString *)text
{
    NSError *error = nil;
    ZXMultiFormatWriter *writer = [ZXMultiFormatWriter writer];
    ZXEncodeHints *hints = [[ZXEncodeHints alloc] init];
    hints.margin = @(0);
    ZXBitMatrix *result = [writer encode:text
                                  format:kBarcodeFormatQRCode
                                   width:500
                                  height:500
                                   hints:hints
                                   error:&error];
    UIImage *image = nil;
    if (result) {
        CGImageRef cgimage = [[ZXImage imageWithMatrix:result] cgimage];
        image = [UIImage imageWithCGImage:cgimage];
        CGImageRelease(cgimage);
    }
    return image;
}

@end
