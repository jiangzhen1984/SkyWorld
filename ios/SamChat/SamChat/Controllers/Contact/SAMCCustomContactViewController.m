//
//  SAMCCustomContactViewController.m
//  SamChat
//
//  Created by HJ on 5/17/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCCustomContactViewController.h"

#import "NTESSessionUtil.h"
#import "NTESSessionViewController.h"
#import "NTESContactUtilItem.h"
#import "NTESContactDefines.h"
#import "NTESGroupedContacts.h"
#import "UIView+Toast.h"
#import "NTESCustomNotificationDB.h"
#import "NTESNotificationCenter.h"
#import "UIActionSheet+NTESBlock.h"
#import "NTESSearchTeamViewController.h"
#import "NTESContactAddFriendViewController.h"
#import "NTESPersonalCardViewController.h"
#import "UIAlertView+NTESBlock.h"
#import "SVProgressHUD.h"
#import "NTESContactUtilCell.h"
#import "NIMContactDataCell.h"
#import "NIMContactSelectViewController.h"
#import "SAMCQRCodeScanViewController.h"
#import "NTESUserUtil.h"

@interface SAMCCustomContactViewController () <NIMSystemNotificationManagerDelegate,NTESContactUtilCellDelegate,NIMContactDataCellDelegate,NIMLoginManagerDelegate,NIMUserManagerDelegate >

@property (nonatomic, strong) NSArray * datas;
@property (nonatomic, strong) UIRefreshControl *refreshControl;
@property (nonatomic, strong) NTESGroupedContacts *contacts;

@end

@implementation SAMCCustomContactViewController

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[[NIMSDK sharedSDK] systemNotificationManager] removeDelegate:self];
    [[[NIMSDK sharedSDK] loginManager] removeDelegate:self];
    [[[NIMSDK sharedSDK] userManager] removeDelegate:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    if ([NIMSDKConfig sharedConfig].hostUserInfos) {
        //托管了用户信息，那就直接加 userManager 的监听
        [[NIMSDK sharedSDK].userManager addDelegate:self];
    }else{
        //没有托管用户信息，就直接加 NIMKit 的监听
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onUserInfoHasUpdatedNotification:) name:NIMKitUserInfoHasUpdatedNotification object:nil];
    }
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds];
    self.tableView.delegate       = self;
    self.tableView.dataSource     = self;
    UIEdgeInsets separatorInset   = self.tableView.separatorInset;
    separatorInset.right          = 0;
    self.tableView.separatorInset = separatorInset;
    self.tableView.sectionIndexBackgroundColor = [UIColor clearColor];
    self.tableView.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.tableView];
    [self prepareData];
    [[[NIMSDK sharedSDK] systemNotificationManager] addDelegate:self];
    [[[NIMSDK sharedSDK] loginManager] addDelegate:self];
    [[[NIMSDK sharedSDK] userManager] addDelegate:self];
}

- (void)setUpNavItem{
    UIButton *teamBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [teamBtn addTarget:self action:@selector(onOpera:) forControlEvents:UIControlEventTouchUpInside];
    [teamBtn setImage:[UIImage imageNamed:@"icon_tinfo_normal"] forState:UIControlStateNormal];
    [teamBtn setImage:[UIImage imageNamed:@"icon_tinfo_pressed"] forState:UIControlStateHighlighted];
    [teamBtn sizeToFit];
    UIBarButtonItem *teamItem = [[UIBarButtonItem alloc] initWithCustomView:teamBtn];
    self.navigationItem.rightBarButtonItem = teamItem;
}

- (void)prepareData{
    _contacts = [[NTESGroupedContacts alloc] init];
    // TODO: check user externsion, only add customers
    self.navigationItem.title = @"客户通讯录";
    [self setUpNavItem];
}


#pragma mark - Action
- (void)onEnterMyComputer{
    NSString *uid = [[NIMSDK sharedSDK].loginManager currentAccount];
    NIMSession *session = [NIMSession session:uid type:NIMSessionTypeP2P];
    NTESSessionViewController *vc = [[NTESSessionViewController alloc] initWithSession:session];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)onOpera:(id)sender
{
    SAMCQRCodeScanViewController *vc = [[SAMCQRCodeScanViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}


#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    id<NTESContactItem> contactItem = (id<NTESContactItem>)[_contacts memberOfIndex:indexPath];
    if ([contactItem respondsToSelector:@selector(selName)] && [contactItem selName].length) {
        SEL sel = NSSelectorFromString([contactItem selName]);
        SuppressPerformSelectorLeakWarning([self performSelector:sel withObject:nil]);
    }
    else if (contactItem.vcName.length) {
        Class clazz = NSClassFromString(contactItem.vcName);
        UIViewController * vc = [[clazz alloc] initWithNibName:nil bundle:nil];
        [self.navigationController pushViewController:vc animated:YES];
    }else if([contactItem respondsToSelector:@selector(userId)]){
        NSString * friendId   = contactItem.userId;
        [self enterPersonalCard:friendId];
    }
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    id<NTESContactItem> contactItem = (id<NTESContactItem>)[_contacts memberOfIndex:indexPath];
    return contactItem.uiHeight;
}


#pragma mark - UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_contacts memberCountOfGroup:section];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [_contacts groupCount];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    id contactItem = [_contacts memberOfIndex:indexPath];
    NSString * cellId = [contactItem reuseId];
    UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:cellId];
    if (!cell) {
        Class cellClazz = NSClassFromString([contactItem cellName]);
        cell = [[cellClazz alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellId];
    }
    if ([contactItem showAccessoryView]) {
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }else{
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    if ([cell isKindOfClass:[NTESContactUtilCell class]]) {
        [(NTESContactUtilCell *)cell refreshWithContactItem:contactItem];
        [(NTESContactUtilCell *)cell setDelegate:self];
    }else{
        [(NIMContactDataCell *)cell refreshUser:contactItem];
        [(NIMContactDataCell *)cell setDelegate:self];
    }
    return cell;
}

- (NSString*)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section{
    return [_contacts titleOfGroup:section];
}

- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView {
    return _contacts.sortedGroupTitles;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index {
    return index + 1;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    id<NTESContactItem> contactItem = (id<NTESContactItem>)[_contacts memberOfIndex:indexPath];
    return [contactItem userId].length;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"删除好友" message:@"删除好友后，将同时解除双方的好友关系" delegate:nil cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil];
        [alert showAlertWithCompletionHandler:^(NSInteger index) {
            if (index == 1) {
                [SVProgressHUD show];
                id<NTESContactItem,NTESGroupMemberProtocol> contactItem = (id<NTESContactItem,NTESGroupMemberProtocol>)[_contacts memberOfIndex:indexPath];
                NSString *userId = [contactItem userId];
                __weak typeof(self) wself = self;
                [[NIMSDK sharedSDK].userManager deleteFriend:userId completion:^(NSError *error) {
                    [SVProgressHUD dismiss];
                    if (!error) {
                        [_contacts removeGroupMember:contactItem];
                    }else{
                        [wself.view makeToast:@"删除失败"duration:2.0f position:CSToastPositionCenter];
                    }
                }];
            }
        }];
    }
}

#pragma mark - NIMContactDataCellDelegate
- (void)onPressAvatar:(NSString *)memberId{
    [self enterPersonalCard:memberId];
}

#pragma mark - NTESContactUtilCellDelegate
- (void)onPressUtilImage:(NSString *)content{
    [self.view makeToast:[NSString stringWithFormat:@"点我干嘛 我是<%@>",content] duration:2.0 position:CSToastPositionCenter];
}

#pragma mark - NIMContactSelectDelegate
- (void)didFinishedSelect:(NSArray *)selectedContacts{
    
}

#pragma mark - NIMSDK Delegate
- (void)onSystemNotificationCountChanged:(NSInteger)unreadCount
{
    [self prepareData];
    [self.tableView reloadData];
}

- (void)onLogin:(NIMLoginStep)step
{
    if (step == NIMLoginStepSyncOK) {
        if (self.isViewLoaded) {//没有加载view的话viewDidLoad里会走一遍prepareData
            [self prepareData];
            [self.tableView reloadData];
        }
    }
}

- (void)onFriendChanged:(NIMUser *)user
{
    [self prepareData];
    [self.tableView reloadData];
}

- (void)onBlackListChanged{
    [self prepareData];
    [self.tableView reloadData];
}

- (void)onUserInfoChanged:(NIMUser *)user{
    [self prepareData];
    [self.tableView reloadData];
}

#pragma mark - Notification
- (void)onUserInfoHasUpdatedNotification:(NSNotification *)notfication{
    [self prepareData];
    [self.tableView reloadData];
}

#pragma mark - Private
- (void)enterPersonalCard:(NSString *)userId{
    NTESPersonalCardViewController *vc = [[NTESPersonalCardViewController alloc] initWithUserId:userId];
    [self.navigationController pushViewController:vc animated:YES];
}


- (void)presentMemberSelector:(ContactSelectFinishBlock) block{
    NSMutableArray *users = [[NSMutableArray alloc] init];
    //使用内置的好友选择器
    NIMContactFriendSelectConfig *config = [[NIMContactFriendSelectConfig alloc] init];
    //获取自己id
    NSString *currentUserId = [[NIMSDK sharedSDK].loginManager currentAccount];
    [users addObject:currentUserId];
    //将自己的id过滤
    config.filterIds = users;
    //需要多选
    config.needMutiSelected = YES;
    //初始化联系人选择器
    NIMContactSelectViewController *vc = [[NIMContactSelectViewController alloc] initWithConfig:config];
    //回调处理
    vc.finshBlock = block;
    [vc show];
}

@end
