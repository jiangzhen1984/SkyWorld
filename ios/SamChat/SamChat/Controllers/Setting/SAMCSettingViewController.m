//
//  SAMCSettingViewController.m
//  SamChat
//
//  Created by HJ on 5/25/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSettingViewController.h"
#import "NTESCommonTableData.h"
#import "NTESCommonTableDelegate.h"
#import "NTESColorButtonCell.h"
#import "SamChatClient.h"
#import "SAMCContactViewController.h"
#import "NTESTeamListViewController.h"

@interface SAMCSettingViewController ()<NIMUserManagerDelegate>

@property (nonatomic,strong) UITableView *tableView;
@property (nonatomic,strong) NSArray *data;
@property (nonatomic,strong) NTESCommonTableDelegate *delegator;

@end

@implementation SAMCSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = [[SamChatClient sharedClient].accountManager currentAccount];
    [self buildData];
    __weak typeof(self) wself = self;
    self.delegator = [[NTESCommonTableDelegate alloc] initWithTableData:^NSArray *{
        return wself.data;
    }];
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.tableHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 0, 8.0)];
    self.tableView.tableFooterView  = [[UIView alloc] init];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:self.tableView];
    self.tableView.delegate   = self.delegator;
    self.tableView.dataSource = self.delegator;
    
    extern NSString *NTESCustomNotificationCountChanged;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onCustomNotifyChanged:) name:NTESCustomNotificationCountChanged object:nil];
    
    if ([NIMSDKConfig sharedConfig].hostUserInfos) {
        //说明托管了用户信息，那就直接加 userManager 的监听
        [[NIMSDK sharedSDK].userManager addDelegate:self];
    }else{
        //没有托管用户信息，就直接加 NIMKit 的监听
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onUserInfoHasUpdatedNotification:) name:NIMKitUserInfoHasUpdatedNotification object:nil];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)buildData{
    BOOL disableRemoteNotification = NO;
    if (IOS8) {
        disableRemoteNotification = [UIApplication sharedApplication].currentUserNotificationSettings.types == UIUserNotificationTypeNone;
    }else{
        disableRemoteNotification = [UIApplication sharedApplication].enabledRemoteNotificationTypes == UIRemoteNotificationTypeNone;
    }
    NSArray *data = @[
                      @{
                          HeaderTitle:@"",
                          RowContent:@[
                                  @{
                                      Title         : @"Service Provider Setting",
                                      CellAction    : @"onTouchServiceProviderSetting:",
                                      ShowAccessory : @(YES)
                                      },
                                  ],
                          FooterTitle:@""
                          },
                      @{
                          HeaderTitle:@"",
                          RowContent:@[
                                  @{
                                      Title         : @"Samchat Contacts",
                                      CellAction    : @"onTouchContacts:",
                                      ShowAccessory : @(YES)
                                      },
                                  ],
                          FooterTitle:@""
                          },
                      @{
                          HeaderTitle:@"",
                          RowContent:@[
                                  @{
                                      Title         : @"Samchat Groups",
                                      CellAction    : @"onTouchGroups:",
                                      ShowAccessory : @(YES)
                                      },
                                  ],
                          FooterTitle:@""
                          },
                      @{
                          HeaderTitle:@"",
                          RowContent:@[
                                  @{
                                      Title         : @"User Profile",
                                      CellAction    : @"onTouchUserProfile:",
                                      ShowAccessory : @(YES)
                                      },
                                  ],
                          FooterTitle:@""
                          },
                      @{
                          HeaderTitle:@"",
                          RowContent:@[
                                  @{
                                      Title         : @"About Samchat",
                                      CellAction :@"onTouchAbout:",
                                      ShowAccessory : @(YES)
                                      },
                                  ],
                          FooterTitle:@""
                          },
                      @{
                          HeaderTitle:@"",
                          RowContent :@[
                                  @{
                                      Title        : @"注销",
                                      CellClass    : @"NTESColorButtonCell",
                                      CellAction   : @"logoutCurrentAccount:",
                                      ExtraInfo    : @(ColorButtonCellStyleRed),
                                      ForbidSelect : @(YES)
                                      },
                                  ],
                          FooterTitle:@"",
                          },
                      ];
    self.data = [NTESCommonTableSection sectionsWithData:data];
}

- (void)refreshData{
    [self buildData];
    [self.tableView reloadData];
}


#pragma mark - Action
- (void)onTouchServiceProviderSetting:(id)sender
{
    DDLogDebug(@"onTouchServiceProviderSetting:");
}

- (void)onTouchContacts:(id)sender
{
    DDLogDebug(@"onTouchContacts");
    SAMCContactViewController *vc = [[SAMCContactViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)onTouchGroups:(id)sender
{
    DDLogDebug(@"onTouchGroups");
    NTESNormalTeamListViewController *vc = [[NTESNormalTeamListViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)onTouchUserProfile:(id)sender
{
    DDLogDebug(@"onTouchUserProfile");
}

- (void)onTouchAbout:(id)sender
{
    DDLogDebug(@"onTouchAbout");
}

- (void)logoutCurrentAccount:(id)sender
{
}

#pragma mark - Notification
- (void)onCustomNotifyChanged:(NSNotification *)notification
{
    [self buildData];
    [self.tableView reloadData];
}


- (void)onUserInfoHasUpdatedNotification:(NSNotification *)notification
{
    NSDictionary *userInfo = notification.userInfo;
    NSArray *userInfos = userInfo[NIMKitInfoKey];
    if ([userInfos containsObject:[NIMSDK sharedSDK].loginManager.currentAccount]) {
        [self buildData];
        [self.tableView reloadData];
    }
}

#pragma mark - NIMUserManagerDelegate
- (void)onUserInfoChanged:(NIMUser *)user{
    if ([user.userId isEqualToString:[[NIMSDK sharedSDK].loginManager currentAccount]]) {
        [self.tableView reloadData];
    }
}

#pragma mark - Private


#pragma mark - 旋转处理 (iOS7)
- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    [self.tableView reloadData];
}

@end
