//
//  HomeViewController.m
//  SamChat
//
//  Created by HJ on 4/26/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "HomeViewController.h"

#import "SettingsViewController.h"
#import "ApplyViewController.h"
#import "ChatViewController.h"
#import "ConversationListController.h"
#import "ContactListViewController.h"
#import "OfficalListTableViewController.h"
#import "UserSettingViewController.h"
#import "SCSettingViewController.h"

//两次提示的默认间隔
static const CGFloat kDefaultPlaySoundInterval = 3.0;
static NSString *kMessageType = @"MessageType";
static NSString *kConversationChatter = @"ConversationChatter";
static NSString *kGroupName = @"GroupName";

#if DEMO_CALL == 1
@interface HomeViewController () <UIAlertViewDelegate, EMCallManagerDelegate>
#else
@interface HomeViewController () <UIAlertViewDelegate>
#endif
{
    SCSearchConversationViewController *_searchConversationVC;
    SCNormalConversationViewController *_normalConversationVC;
    SCServiceConversationViewController *_serviceConversationVC;
    OfficalListTableViewController *_officeListVC;
    SCSettingViewController *_settingVC;
    
    UIBarButtonItem *_addFriendItem;
}

@property (strong, nonatomic) NSDate *lastPlaySoundDate;

@end

@implementation HomeViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
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
    self.selectedIndex = 0;
    
//    UIButton *addButton = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
//    [addButton setImage:[UIImage imageNamed:@"add.png"] forState:UIControlStateNormal];
//    [addButton addTarget:_contactsVC action:@selector(addFriendAction) forControlEvents:UIControlEventTouchUpInside];
//    _addFriendItem = [[UIBarButtonItem alloc] initWithCustomView:addButton];
    
    [self setupUnreadMessageCount];
    [self setupUntreatedApplyCount];
    
  //  [SamChatHelper shareHelper].contactViewVC = _contactsVC;
    [SamChatHelper shareHelper].normalConversationListVC = _normalConversationVC;
    [SamChatHelper shareHelper].searchConversationListVC = _searchConversationVC;
    [SamChatHelper shareHelper].serviceConversationListVC = _serviceConversationVC;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)dealloc
{
    
}

#pragma mark - lazy loading
- (SCNormalConversationViewController *)normalConversationVC
{
    if(_normalConversationVC == nil){
        _normalConversationVC = [[SCNormalConversationViewController alloc] initWithNibName:nil bundle:nil];
    }
    return _normalConversationVC;
}

- (SCServiceConversationViewController *)serviceConversationVC
{
    if(_serviceConversationVC == nil){
        _serviceConversationVC = [[SCServiceConversationViewController alloc] initWithNibName:nil bundle:nil];
    }
    return _serviceConversationVC;
}

#pragma mark - UITabBarDelegate

- (void)tabBar:(UITabBar *)tabBar didSelectItem:(UITabBarItem *)item
{
    if (item.tag == 0) {
        self.title = @"Search";
        self.navigationItem.rightBarButtonItem = nil;
    }else if (item.tag == 1){
        self.title = @"Chat";
        self.navigationItem.rightBarButtonItem = _addFriendItem;
    }else if (item.tag == 2){
        self.title = @"Public";
        self.navigationItem.rightBarButtonItem = nil;
       // [_settingsVC refreshConfig];
    }else if(item.tag == 3){
        self.title = @"Service";
        self.navigationItem.rightBarButtonItem = nil;
    }else if(item.tag == 4){
        self.title = @"Setting";
        self.navigationItem.rightBarButtonItem = nil;
    }
}

#pragma mark - private

- (void)setupSubviews
{
    self.tabBar.backgroundImage = [[UIImage imageNamed:@"tabbarBackground"] stretchableImageWithLeftCapWidth:25 topCapHeight:25];
    self.tabBar.selectionIndicatorImage = [[UIImage imageNamed:@"tabbarSelectBg"] stretchableImageWithLeftCapWidth:25 topCapHeight:25];
    
    _searchConversationVC = [[SCSearchConversationViewController alloc] initWithNibName:nil bundle:nil];
    [_searchConversationVC networkChanged:_connectionState];
    _searchConversationVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Search"
                                                                         image:nil
                                                                           tag:0];
    
    _normalConversationVC = [[SCNormalConversationViewController alloc] initWithNibName:nil bundle:nil];
    [_normalConversationVC networkChanged:_connectionState];
    _normalConversationVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Chat"
                                                                         image:nil
                                                                           tag:1];
    
    _officeListVC = [[OfficalListTableViewController alloc] initWithNibName:nil bundle:nil];
    _officeListVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Public"
                                                             image:nil
                                                               tag:2];
    
    _serviceConversationVC = [[SCServiceConversationViewController alloc] initWithNibName:nil bundle:nil];
    [_serviceConversationVC networkChanged:_connectionState];
    _serviceConversationVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Service"
                                                                      image:nil
                                                                        tag:3];
    
    UIStoryboard *settingStoryboard = [UIStoryboard storyboardWithName:@"Setting" bundle:[NSBundle mainBundle]];
    _settingVC = [settingStoryboard instantiateViewControllerWithIdentifier:@"SettingView"];
    _settingVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"Setting"
                                                          image:nil
                                                            tag:4];
    
    self.viewControllers = @[_searchConversationVC, _normalConversationVC, _officeListVC, _serviceConversationVC, _settingVC];
    [self selectedTapTabBarItems:_searchConversationVC.tabBarItem];
//    _chatListVC = [[ConversationListController alloc] initWithNibName:nil bundle:nil];
//    [_chatListVC networkChanged:_connectionState];
//    _chatListVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"title.conversation", @"Conversations")
//                                                           image:nil
//                                                             tag:0];
//    [_chatListVC.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"tabbar_chatsHL"]
//                         withFinishedUnselectedImage:[UIImage imageNamed:@"tabbar_chats"]];
//    [self unSelectedTapTabBarItems:_chatListVC.tabBarItem];
//    [self selectedTapTabBarItems:_chatListVC.tabBarItem];
//    
//    _contactsVC = [[ContactListViewController alloc] initWithNibName:nil bundle:nil];
//    _contactsVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"title.addressbook", @"AddressBook")
//                                                           image:nil
//                                                             tag:1];
//    [_contactsVC.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"tabbar_contactsHL"]
//                         withFinishedUnselectedImage:[UIImage imageNamed:@"tabbar_contacts"]];
//    [self unSelectedTapTabBarItems:_contactsVC.tabBarItem];
//    [self selectedTapTabBarItems:_contactsVC.tabBarItem];
//    
//    _settingsVC = [[SettingsViewController alloc] init];
//    _settingsVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"title.setting", @"Setting")
//                                                           image:nil
//                                                             tag:2];
//    [_settingsVC.tabBarItem setFinishedSelectedImage:[UIImage imageNamed:@"tabbar_settingHL"]
//                         withFinishedUnselectedImage:[UIImage imageNamed:@"tabbar_setting"]];
//    _settingsVC.view.autoresizingMask = UIViewAutoresizingFlexibleHeight;
//    [self unSelectedTapTabBarItems:_settingsVC.tabBarItem];
//    [self selectedTapTabBarItems:_settingsVC.tabBarItem];
//    
//    self.viewControllers = @[_chatListVC, _contactsVC, _settingsVC];
//    [self selectedTapTabBarItems:_chatListVC.tabBarItem];
}

-(void)unSelectedTapTabBarItems:(UITabBarItem *)tabBarItem
{
    [tabBarItem setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                        [UIFont systemFontOfSize:14], UITextAttributeFont,[UIColor whiteColor],UITextAttributeTextColor,
                                        nil] forState:UIControlStateNormal];
}

-(void)selectedTapTabBarItems:(UITabBarItem *)tabBarItem
{
    [tabBarItem setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                        [UIFont systemFontOfSize:14],
                                        UITextAttributeFont,RGBACOLOR(0x00, 0xac, 0xff, 1),UITextAttributeTextColor,
                                        nil] forState:UIControlStateSelected];
}

// 统计未读消息数
-(void)setupUnreadMessageCount
{
    NSArray *conversations = [[EMClient sharedClient].chatManager getAllConversations];
    NSInteger normalUnreadCount = 0;
    NSInteger searchUnreadCount = 0;
    NSInteger serviceUnreadCount = 0;
    for (EMConversation *conversation in conversations) {
        // 从最新的开始读取未读数量的消息，这些消息都是收到的消息，因为如果回复过，则之前的都是已读
        NSArray *messages = [conversation loadMoreMessagesFromId:nil limit:conversation.unreadMessagesCount];
        for (EMMessage *message in messages) {
            if ([[message.ext valueForKey:MESSAGE_FROM_VIEW] isEqualToString:MESSAGE_FROM_VIEW_SEARCH]) {
                serviceUnreadCount ++;
            }else if([[message.ext valueForKey:MESSAGE_FROM_VIEW] isEqualToString:MESSAGE_FROM_VIEW_VENDOR]) {
                searchUnreadCount ++;
            }else{
                normalUnreadCount ++;
            }
        }
    }
    DebugLog(@"unread count:%ld, %ld, %ld", searchUnreadCount, normalUnreadCount, serviceUnreadCount);
    //unreadCount += [[SCUserProfileManager sharedInstance].currentLoginUserInformation.unreadquestioncount integerValue];
//    if (self.searchConversationVC) {
//        if (searchUnreadCount) {
//            [self setupBadgeToView:self.tabButtons[0]];
//        }else{
//            [self.tabButtons[0] clearBadge];
//        }
//    }
//    if (self.normalConversationVC) {
//        if (normalUnreadCount > 0) {
//            [self setupBadgeToView:self.tabButtons[1]];
//        }else{
//            [self.tabButtons[1] clearBadge];
//        }
//    }
//    if (self.serviceConversationVC) {
//        if (serviceUnreadCount) {
//            [self setupBadgeToView:self.tabButtons[2]];
//        }else{
//            [self.tabButtons[2] clearBadge];
//        }
//    }
    if (_searchConversationVC) {
        if (searchUnreadCount) {
            _searchConversationVC.tabBarItem.badgeValue = [NSString stringWithFormat:@"%i",(int)searchUnreadCount];
        } else {
            _searchConversationVC.tabBarItem.badgeValue = nil;
        }
    }
    if (_normalConversationVC) {
        if (normalUnreadCount) {
            _normalConversationVC.tabBarItem.badgeValue = [NSString stringWithFormat:@"%i",(int)normalUnreadCount];
        } else {
            _normalConversationVC.tabBarItem.badgeValue = nil;
        }
    }
    if (_serviceConversationVC) {
        if (serviceUnreadCount) {
            _serviceConversationVC.tabBarItem.badgeValue = [NSString stringWithFormat:@"%i",(int)serviceUnreadCount];
        } else {
            _serviceConversationVC.tabBarItem.badgeValue = nil;
        }
    }
    
    NSInteger unreadCount = normalUnreadCount+searchUnreadCount+serviceUnreadCount;
    UIApplication *application = [UIApplication sharedApplication];
    [application setApplicationIconBadgeNumber:unreadCount];
}

- (void)setupUntreatedApplyCount
{
//    NSInteger unreadCount = [[[ApplyViewController shareController] dataSource] count];
//    if (_contactsVC) {
//        if (unreadCount > 0) {
//            _contactsVC.tabBarItem.badgeValue = [NSString stringWithFormat:@"%i",(int)unreadCount];
//        }else{
//            _contactsVC.tabBarItem.badgeValue = nil;
//        }
//    }
}

- (void)networkChanged:(EMConnectionState)connectionState
{
    _connectionState = connectionState;
    [_normalConversationVC networkChanged:connectionState];
    [_searchConversationVC networkChanged:connectionState];
    [_serviceConversationVC networkChanged:connectionState];
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
        
        NSString *title = [[SCUserProfileManager sharedInstance] getNickNameWithUsername:message.from];
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
//    else if(_chatListVC)
//    {
//        [self.navigationController popToViewController:self animated:NO];
//        [self setSelectedViewController:_chatListVC];
//    }
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
//    else if (_chatListVC)
//    {
//        [self.navigationController popToViewController:self animated:NO];
//        [self setSelectedViewController:_chatListVC];
//    }
}

@end
