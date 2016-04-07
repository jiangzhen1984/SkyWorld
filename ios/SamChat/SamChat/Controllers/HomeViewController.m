//
//  HomeViewController.m
//  SamChat
//
//  Created by HJ on 3/30/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "HomeViewController.h"

#import "ChatDemoHelper.h"
#import "SettingsViewController.h"
#import "ApplyViewController.h"
#import "ChatViewController.h"
#import "UserProfileManager.h"
#import "ConversationListController.h"
#import "ContactListViewController.h"

#import "ServiceSearchViewController.h"
#import "OfficalListTableViewController.h"
#import "ProducerViewController.h"

#import "WZLBadgeImport.h"


@interface HomeViewController () <SCUITabPagerDataSource, SCUITabPagerDelegate, UIAlertViewDelegate>

@property (nonatomic, strong) ServiceSearchViewController *serviceSearchVC;
@property (nonatomic, strong) SCChatListViewController *chatListVC;
@property (nonatomic, strong) OfficalListTableViewController *officalListVC;
@property (nonatomic, strong) ProducerViewController *producerVC;


@property (nonatomic, strong) ContactListViewController *contactsVC;

@property (nonatomic, strong) NSDate *lastPlaySoundDate;

@property (nonatomic, strong) NSArray *tabButtons;

- (void)easeMobSetup;

@end

@implementation HomeViewController

#pragma mark - View Controller Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self easeMobSetup];
    
    [self setDataSource:self];
    [self setDelegate:self];
    [self navigationBarStyle];
    [self reloadData];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
 //   [self reloadData];
}

- (void)navigationBarStyle
{
    self.navigationController.navigationBar.translucent = NO;
    [self.navigationController.navigationBar setBackgroundImage:[SCUtils createImageWithColor:SC_MAIN_COLOR] forBarMetrics:UIBarMetricsDefault];
    [self.navigationController.navigationBar setBackIndicatorImage:[SCUtils createImageWithColor:SC_MAIN_COLOR]];
    [self.navigationController.navigationBar setShadowImage:[SCUtils createImageWithColor:[UIColor clearColor]]];
    
    // NavigationBar
    UILabel *titleView = [[UILabel alloc] initWithFrame:CGRectZero];
    titleView.font = [UIFont fontWithName:@"Futura-Medium" size:19];
    titleView.textColor = [UIColor colorWithRed:0.333333 green:0.333333 blue:0.333333 alpha:1.0];
    titleView.text = @"Menu";
    [titleView sizeToFit];
    titleView.backgroundColor = [UIColor colorWithRed:((2) / 255.0) green:((168) / 255.0) blue:((244) / 255.0) alpha:1.0];
    self.navigationItem.titleView = titleView;
}

#pragma mark - Tab Pager Data Source

- (NSInteger)numberOfViewControllers {
    return 4;
}

- (UIViewController *)viewControllerForIndex:(NSInteger)index
{
    UIViewController *viewController = nil;
    switch (index) {
        case 0:
            viewController = self.serviceSearchVC;
            break;
        case 1:
            viewController = [ChatDemoHelper shareHelper].conversationListVC;
            break;
        case 2:
            viewController = self.officalListVC;
            break;
        case 3:
            viewController = self.producerVC;
            break;
        default:
            break;
    }
    return viewController;
}

// Implement either viewForTabAtIndex: or titleForTabAtIndex:
- (UIView *)viewForTabAtIndex:(NSInteger)index
{
    //CGRect frame = CGRectMake(0, 0, self.view.frame.size.width/4, 50);
    //UIButton *buttonView = [[UIButton alloc] initWithFrame:frame];
//    UIButton *buttonView = [[UIButton alloc] init];
//    buttonView.backgroundColor = SC_MAIN_COLOR;
//    [buttonView setTitle:[NSString stringWithFormat:@"%ld", index] forState:UIControlStateNormal];
//    return buttonView;
    return self.tabButtons[index];
}

//- (NSString *)titleForTabAtIndex:(NSInteger)index
//{
//  return [NSString stringWithFormat:@"Tab #%ld", (long) index + 1];
//}

- (CGFloat)tabHeight
{
    return 44.0f;
}

- (UIColor *)tabColor
{
    return [UIColor whiteColor];
}

- (UIColor *)tabBackgroundColor
{
    return SC_MAIN_COLOR;
}

- (UIFont *)titleFont
{
    return [UIFont fontWithName:@"HelveticaNeue-Bold" size:20.0f];
}

- (UIColor *)titleColor
{
    return [UIColor whiteColor];
}

#pragma mark - Tab Pager Delegate

- (void)tabPager:(SCUITabPagerViewController *)tabPager willTransitionToTabAtIndex:(NSInteger)index {
    NSLog(@"Will transition from tab %ld to %ld", [self selectedIndex], (long)index);
}

- (void)tabPager:(SCUITabPagerViewController *)tabPager didTransitionToTabAtIndex:(NSInteger)index {
    NSLog(@"Did transition to tab %ld", (long)index);
}

#pragma mark - Easemob

//两次提示的默认间隔
static const CGFloat kDefaultPlaySoundInterval = 3.0;
static NSString *kMessageType = @"MessageType";
static NSString *kConversationChatter = @"ConversationChatter";
static NSString *kGroupName = @"GroupName";

//#if DEMO_CALL == 1
//@interface MainViewController () <UIAlertViewDelegate, EMCallManagerDelegate>
//#else
//@interface MainViewController () <UIAlertViewDelegate>
//#endif
//{
//
//    ContactListViewController *_contactsVC;
//    SettingsViewController *_settingsVC;
//    //    __weak CallViewController *_callController;
//    
//    UIBarButtonItem *_addFriendItem;
//}
//
//@property (strong, nonatomic) NSDate *lastPlaySoundDate;
//
//@end

- (void)easeMobSetup
{
    //if 使tabBarController中管理的viewControllers都符合 UIRectEdgeNone
    if ([UIDevice currentDevice].systemVersion.floatValue >= 7) {
        self.edgesForExtendedLayout = UIRectEdgeNone;
    }
    self.title = NSLocalizedString(@"title.conversation", @"Conversations");
    
    //获取未读消息数，此时并没有把self注册为SDK的delegate，读取出的未读数是上次退出程序时的
    //    [self didUnreadMessagesCountChanged];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setupUntreatedApplyCount) name:@"setupUntreatedApplyCount" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setupUnreadMessageCount) name:@"setupUnreadMessageCount" object:nil];
    
    [self setupSubviews];
    //self.selectedIndex = 0;
    
//    UIButton *addButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
//    [addButton setImage:[UIImage imageNamed:@"add.png"] forState:UIControlStateNormal];
//    [addButton addTarget:_contactsVC action:@selector(addFriendAction) forControlEvents:UIControlEventTouchUpInside];
//    _addFriendItem = [[UIBarButtonItem alloc] initWithCustomView:addButton];
    
    [self setupUnreadMessageCount];
    [self setupUntreatedApplyCount];
    
    [ChatDemoHelper shareHelper].contactViewVC = _contactsVC;
    [ChatDemoHelper shareHelper].conversationListVC = _chatListVC;
}
//
//#pragma mark - UITabBarDelegate
//
//- (void)tabBar:(UITabBar *)tabBar didSelectItem:(UITabBarItem *)item
//{
//    if (item.tag == 0) {
//        self.title = NSLocalizedString(@"title.conversation", @"Conversations");
//        self.navigationItem.rightBarButtonItem = nil;
//    }else if (item.tag == 1){
//        self.title = NSLocalizedString(@"title.addressbook", @"AddressBook");
//        self.navigationItem.rightBarButtonItem = _addFriendItem;
//    }else if (item.tag == 2){
//        self.title = NSLocalizedString(@"title.setting", @"Setting");
//        self.navigationItem.rightBarButtonItem = nil;
//        [_settingsVC refreshConfig];
//    }
//}

#pragma mark - private

- (void)setupTabButtons
{
    UIButton *button1 = [[UIButton alloc] init];
    button1.backgroundColor = SC_MAIN_COLOR;
    [button1 setTitle:[NSString stringWithFormat:@"1"] forState:UIControlStateNormal];
    
    UIButton *button2 = [[UIButton alloc] init];
    button2.backgroundColor = SC_MAIN_COLOR;
    [button2 setTitle:[NSString stringWithFormat:@"2"] forState:UIControlStateNormal];
    
    UIButton *button3 = [[UIButton alloc] init];
    button3.backgroundColor = SC_MAIN_COLOR;
    [button3 setTitle:[NSString stringWithFormat:@"3"] forState:UIControlStateNormal];
    
    UIButton *button4 = [[UIButton alloc] init];
    button4.backgroundColor = SC_MAIN_COLOR;
    [button4 setTitle:[NSString stringWithFormat:@"4"] forState:UIControlStateNormal];
    
    self.tabButtons = @[button1, button2, button3, button4];
}

- (void)setupSubviews
{
    [self setupTabButtons];
    _serviceSearchVC = [self.storyboard instantiateViewControllerWithIdentifier:@"ServiceSearch"];
    
    _officalListVC = [self.storyboard instantiateViewControllerWithIdentifier:@"OfficalList"];
    
    _producerVC = [self.storyboard instantiateViewControllerWithIdentifier:@"Producer"];
    
//    self.tabBar.backgroundImage = [[UIImage imageNamed:@"tabbarBackground"] stretchableImageWithLeftCapWidth:25 topCapHeight:25];
//    self.tabBar.selectionIndicatorImage = [[UIImage imageNamed:@"tabbarSelectBg"] stretchableImageWithLeftCapWidth:25 topCapHeight:25];
    
    _chatListVC = [[SCChatListViewController alloc] initWithNibName:nil bundle:nil];
    [_chatListVC networkChanged:_connectionState];
    
//    _chatListVC = [[ConversationListController alloc] initWithNibName:nil bundle:nil];
//    [_chatListVC networkChanged:_connectionState];
//    _chatListVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"title.conversation", @"Conversations")
//                                                           image:nil
//                                                             tag:0];
//    [_chatListVC.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"tabbar_chatsHL"]
//                         withFinishedUnselectedImage:[UIImage imageNamed:@"tabbar_chats"]];
//    [self unSelectedTapTabBarItems:_chatListVC.tabBarItem];
//    [self selectedTapTabBarItems:_chatListVC.tabBarItem];
    
    _contactsVC = [[ContactListViewController alloc] initWithNibName:nil bundle:nil];
//    _contactsVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"title.addressbook", @"AddressBook")
//                                                           image:nil
//                                                             tag:1];
//    [_contactsVC.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"tabbar_contactsHL"]
//                         withFinishedUnselectedImage:[UIImage imageNamed:@"tabbar_contacts"]];
//    [self unSelectedTapTabBarItems:_contactsVC.tabBarItem];
//    [self selectedTapTabBarItems:_contactsVC.tabBarItem];
    
//    _settingsVC = [[SettingsViewController alloc] init];
//    _settingsVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"title.setting", @"Setting")
//                                                           image:nil
//                                                             tag:2];
//    [_settingsVC.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"tabbar_settingHL"]
//                         withFinishedUnselectedImage:[UIImage imageNamed:@"tabbar_setting"]];
//    _settingsVC.view.autoresizingMask = UIViewAutoresizingFlexibleHeight;
//    [self unSelectedTapTabBarItems:_settingsVC.tabBarItem];
//    [self selectedTapTabBarItems:_settingsVC.tabBarItem];
    
    //self.viewControllers = @[_chatListVC, _contactsVC, _settingsVC];
    //[self selectedTapTabBarItems:_chatListVC.tabBarItem];
}

//-(void)unSelectedTapTabBarItems:(UITabBarItem *)tabBarItem
//{
//    [tabBarItem setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
//                                        [UIFont systemFontOfSize:14], UITextAttributeFont,[UIColor whiteColor],UITextAttributeTextColor,
//                                        nil] forState:UIControlStateNormal];
//}
//
//-(void)selectedTapTabBarItems:(UITabBarItem *)tabBarItem
//{
//    [tabBarItem setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
//                                        [UIFont systemFontOfSize:14],
//                                        UITextAttributeFont,RGBACOLOR(0x00, 0xac, 0xff, 1),UITextAttributeTextColor,
//                                        nil] forState:UIControlStateSelected];
//}

- (void)setupBadgeToView:(UIView *)view
{
    [view showBadgeWithStyle:WBadgeStyleRedDot value:0 animationType:WBadgeAnimTypeNone];
    view.badgeCenterOffset = CGPointMake(-8, 8);
}

// 统计未读消息数
-(void)setupUnreadMessageCount
{
    NSArray *conversations = [[EMClient sharedClient].chatManager getAllConversations];
    NSInteger unreadCount = 0;
    for (EMConversation *conversation in conversations) {
        unreadCount += conversation.unreadMessagesCount;
    }
    if (_chatListVC) {
        if (unreadCount > 0) {
            //_chatListVC.tabBarItem.badgeValue = [NSString stringWithFormat:@"%i",(int)unreadCount];
            [self setupBadgeToView:self.tabButtons[1]];
        }else{
            //_chatListVC.tabBarItem.badgeValue = nil;
            [self.tabButtons[1] clearBadge];
        }
    }
    
    UIApplication *application = [UIApplication sharedApplication];
    [application setApplicationIconBadgeNumber:unreadCount];
    DebugLog(@"unread");
}

- (void)setupUntreatedApplyCount
{
    NSInteger unreadCount = [[[ApplyViewController shareController] dataSource] count];
    if (_contactsVC) {
        if (unreadCount > 0) {
            //_contactsVC.tabBarItem.badgeValue = [NSString stringWithFormat:@"%i",(int)unreadCount];
        }else{
            //_contactsVC.tabBarItem.badgeValue = nil;
        }
    }
}

- (void)networkChanged:(EMConnectionState)connectionState
{
    _connectionState = connectionState;
    [_chatListVC networkChanged:connectionState];
}

- (void)playSoundAndVibration{
    NSTimeInterval timeInterval = [[NSDate date]
                                   timeIntervalSinceDate:self.lastPlaySoundDate];
    if (timeInterval < kDefaultPlaySoundInterval) {
        //如果距离上次响铃和震动时间太短, 则跳过响铃
        NSLog(@"skip ringing & vibration %@, %@", [NSDate date], self.lastPlaySoundDate);
        return;
    }
    
    //保存最后一次响铃时间
    self.lastPlaySoundDate = [NSDate date];
    
    // 收到消息时，播放音频
    [[EMCDDeviceManager sharedInstance] playNewMessageSound];
    // 收到消息时，震动
    [[EMCDDeviceManager sharedInstance] playVibration];
}

- (void)showNotificationWithMessage:(EMMessage *)message
{
    EMPushOptions *options = [[EMClient sharedClient] pushOptions];
    //发送本地推送
    UILocalNotification *notification = [[UILocalNotification alloc] init];
    notification.fireDate = [NSDate date]; //触发通知的时间
    
    if (options.displayStyle == EMPushDisplayStyleMessageSummary) {
        EMMessageBody *messageBody = message.body;
        NSString *messageStr = nil;
        switch (messageBody.type) {
            case EMMessageBodyTypeText:
            {
                messageStr = ((EMTextMessageBody *)messageBody).text;
            }
                break;
            case EMMessageBodyTypeImage:
            {
                messageStr = NSLocalizedString(@"message.image", @"Image");
            }
                break;
            case EMMessageBodyTypeLocation:
            {
                messageStr = NSLocalizedString(@"message.location", @"Location");
            }
                break;
            case EMMessageBodyTypeVoice:
            {
                messageStr = NSLocalizedString(@"message.voice", @"Voice");
            }
                break;
            case EMMessageBodyTypeVideo:{
                messageStr = NSLocalizedString(@"message.video", @"Video");
            }
                break;
            default:
                break;
        }
        
        NSString *title = [[UserProfileManager sharedInstance] getNickNameWithUsername:message.from];
        if (message.chatType == EMChatTypeGroupChat) {
            NSArray *groupArray = [[EMClient sharedClient].groupManager getAllGroups];
            for (EMGroup *group in groupArray) {
                if ([group.groupId isEqualToString:message.conversationId]) {
                    title = [NSString stringWithFormat:@"%@(%@)", message.from, group.subject];
                    break;
                }
            }
        }
        else if (message.chatType == EMChatTypeChatRoom)
        {
            NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
            NSString *key = [NSString stringWithFormat:@"OnceJoinedChatrooms_%@", [[EMClient sharedClient] currentUsername]];
            NSMutableDictionary *chatrooms = [NSMutableDictionary dictionaryWithDictionary:[ud objectForKey:key]];
            NSString *chatroomName = [chatrooms objectForKey:message.conversationId];
            if (chatroomName)
            {
                title = [NSString stringWithFormat:@"%@(%@)", message.from, chatroomName];
            }
        }
        
        notification.alertBody = [NSString stringWithFormat:@"%@:%@", title, messageStr];
    }
    else{
        notification.alertBody = NSLocalizedString(@"receiveMessage", @"you have a new message");
    }
    
#warning 去掉注释会显示[本地]开头, 方便在开发中区分是否为本地推送
    //notification.alertBody = [[NSString alloc] initWithFormat:@"[本地]%@", notification.alertBody];
    
    notification.alertAction = NSLocalizedString(@"open", @"Open");
    notification.timeZone = [NSTimeZone defaultTimeZone];
    NSTimeInterval timeInterval = [[NSDate date] timeIntervalSinceDate:self.lastPlaySoundDate];
    if (timeInterval < kDefaultPlaySoundInterval) {
        NSLog(@"skip ringing & vibration %@, %@", [NSDate date], self.lastPlaySoundDate);
    } else {
        notification.soundName = UILocalNotificationDefaultSoundName;
        self.lastPlaySoundDate = [NSDate date];
    }
    
    NSMutableDictionary *userInfo = [NSMutableDictionary dictionary];
    [userInfo setObject:[NSNumber numberWithInt:message.chatType] forKey:kMessageType];
    [userInfo setObject:message.conversationId forKey:kConversationChatter];
    notification.userInfo = userInfo;
    
    //发送通知
    [[UIApplication sharedApplication] scheduleLocalNotification:notification];
    //    UIApplication *application = [UIApplication sharedApplication];
    //    application.applicationIconBadgeNumber += 1;
}

#pragma mark - 自动登录回调

- (void)willAutoReconnect{
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSNumber *showreconnect = [ud objectForKey:@"identifier_showreconnect_enable"];
    if (showreconnect && [showreconnect boolValue]) {
        [self hideHud];
        [self showHint:NSLocalizedString(@"reconnection.ongoing", @"reconnecting...")];
    }
}

- (void)didAutoReconnectFinishedWithError:(NSError *)error{
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSNumber *showreconnect = [ud objectForKey:@"identifier_showreconnect_enable"];
    if (showreconnect && [showreconnect boolValue]) {
        [self hideHud];
        if (error) {
            [self showHint:NSLocalizedString(@"reconnection.fail", @"reconnection failure, later will continue to reconnection")];
        }else{
            [self showHint:NSLocalizedString(@"reconnection.success", @"reconnection successful！")];
        }
    }
}

#pragma mark - public

- (void)jumpToChatList
{
    if ([self.navigationController.topViewController isKindOfClass:[ChatViewController class]]) {
        //        ChatViewController *chatController = (ChatViewController *)self.navigationController.topViewController;
        //        [chatController hideImagePicker];
    }
    else if(_chatListVC)
    {
        [self.navigationController popToViewController:self animated:NO];
//        [self setSelectedViewController:_chatListVC];
    }
}

- (EMConversationType)conversationTypeFromMessageType:(EMChatType)type
{
    EMConversationType conversatinType = EMConversationTypeChat;
    switch (type) {
        case EMChatTypeChat:
            conversatinType = EMConversationTypeChat;
            break;
        case EMChatTypeGroupChat:
            conversatinType = EMConversationTypeGroupChat;
            break;
        case EMChatTypeChatRoom:
            conversatinType = EMConversationTypeChatRoom;
            break;
        default:
            break;
    }
    return conversatinType;
}

- (void)didReceiveLocalNotification:(UILocalNotification *)notification
{
    NSDictionary *userInfo = notification.userInfo;
    if (userInfo)
    {
        if ([self.navigationController.topViewController isKindOfClass:[ChatViewController class]]) {
            //            ChatViewController *chatController = (ChatViewController *)self.navigationController.topViewController;
            //            [chatController hideImagePicker];
        }
        
        NSArray *viewControllers = self.navigationController.viewControllers;
        [viewControllers enumerateObjectsWithOptions:NSEnumerationReverse usingBlock:^(id obj, NSUInteger idx, BOOL *stop){
            if (obj != self)
            {
                if (![obj isKindOfClass:[ChatViewController class]])
                {
                    [self.navigationController popViewControllerAnimated:NO];
                }
                else
                {
                    NSString *conversationChatter = userInfo[kConversationChatter];
                    ChatViewController *chatViewController = (ChatViewController *)obj;
                    if (![chatViewController.conversation.conversationId isEqualToString:conversationChatter])
                    {
                        [self.navigationController popViewControllerAnimated:NO];
                        EMChatType messageType = [userInfo[kMessageType] intValue];
                        chatViewController = [[ChatViewController alloc] initWithConversationChatter:conversationChatter conversationType:[self conversationTypeFromMessageType:messageType]];
                        switch (messageType) {
                            case EMChatTypeChat:
                            {
                                NSArray *groupArray = [[EMClient sharedClient].groupManager getAllGroups];
                                for (EMGroup *group in groupArray) {
                                    if ([group.groupId isEqualToString:conversationChatter]) {
                                        chatViewController.title = group.subject;
                                        break;
                                    }
                                }
                            }
                                break;
                            default:
                                chatViewController.title = conversationChatter;
                                break;
                        }
                        [self.navigationController pushViewController:chatViewController animated:NO];
                    }
                    *stop= YES;
                }
            }
            else
            {
                ChatViewController *chatViewController = (ChatViewController *)obj;
                NSString *conversationChatter = userInfo[kConversationChatter];
                EMChatType messageType = [userInfo[kMessageType] intValue];
                chatViewController = [[ChatViewController alloc] initWithConversationChatter:conversationChatter conversationType:[self conversationTypeFromMessageType:messageType]];
                switch (messageType) {
                    case EMChatTypeGroupChat:
                    {
                        NSArray *groupArray = [[EMClient sharedClient].groupManager getAllGroups];
                        for (EMGroup *group in groupArray) {
                            if ([group.groupId isEqualToString:conversationChatter]) {
                                chatViewController.title = group.subject;
                                break;
                            }
                        }
                    }
                        break;
                    default:
                        chatViewController.title = conversationChatter;
                        break;
                }
                [self.navigationController pushViewController:chatViewController animated:NO];
            }
        }];
    }
    else if (_chatListVC)
    {
        [self.navigationController popToViewController:self animated:NO];
//        [self setSelectedViewController:_chatListVC];
    }
}

@end