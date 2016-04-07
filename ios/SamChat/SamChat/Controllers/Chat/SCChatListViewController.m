//
//  SCChatListViewController.m
//  SamChat
//
//  Created by HJ on 4/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCChatListViewController.h"
#import "QuestionAndAnswerViewController.h"
#import "SCArticleViewController.h"

#import "ChatViewController.h"
#import "RobotManager.h"
#import "RobotChatViewController.h"
#import "UserProfileManager.h"
#import "RealtimeSearchUtil.h"

@implementation EMConversation (search)

//根据用户昵称,环信机器人名称,群名称进行搜索
- (NSString*)showName
{
    if (self.type == EMConversationTypeChat) {
        if ([[RobotManager sharedInstance] isRobotWithUsername:self.conversationId]) {
            return [[RobotManager sharedInstance] getRobotNickWithUsername:self.conversationId];
        }
        return [[UserProfileManager sharedInstance] getNickNameWithUsername:self.conversationId];
    } else if (self.type == EMConversationTypeGroupChat) {
        if ([self.ext objectForKey:@"subject"] || [self.ext objectForKey:@"isPublic"]) {
            return [self.ext objectForKey:@"subject"];
        }
    }
    return self.conversationId;
}

@end

@interface SCChatListViewController ()<EaseConversationListViewControllerDelegate, EaseConversationListViewControllerDataSource>

@property (nonatomic, strong) UIView *networkStateView;
@property (nonatomic, assign) NSInteger tablecellExtCount;

@end

@implementation SCChatListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.showRefreshHeader = YES;
    self.delegate = self;
    self.dataSource = self;
    
    [self tableViewDidTriggerHeaderRefresh];

    self.tableView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    [self networkStateView];
    
    [self removeEmptyConversationsFromDB];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self checkUserType];
    [self refresh];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)checkUserType
{
    LoginUserInformation *currentLoginUserInformation = [SCUserProfileManager sharedInstance].currentLoginUserInformation;
    if([currentLoginUserInformation.usertype isEqualToNumber:LOGIN_USER_TYPE_SAMVENDOR]){
        self.tablecellExtCount = 2; // 天际问答+朋友圈
    }else{
        self.tablecellExtCount = 1; // 朋友圈
    }
}

- (void)removeEmptyConversationsFromDB
{
    NSArray *conversations = [[EMClient sharedClient].chatManager getAllConversations];
    NSMutableArray *needRemoveConversations;
    for (EMConversation *conversation in conversations) {
        if (!conversation.latestMessage || (conversation.type == EMConversationTypeChatRoom)) {
            if (!needRemoveConversations) {
                needRemoveConversations = [[NSMutableArray alloc] initWithCapacity:0];
            }
            
            [needRemoveConversations addObject:conversation];
        }
    }
    
    if (needRemoveConversations && needRemoveConversations.count > 0) {
        [[EMClient sharedClient].chatManager deleteConversations:needRemoveConversations deleteMessages:YES];
    }
}

#pragma mark - getter
- (UIView *)networkStateView
{
    if (_networkStateView == nil) {
        _networkStateView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.tableView.frame.size.width, 44)];
        _networkStateView.backgroundColor = [UIColor colorWithRed:255 / 255.0 green:199 / 255.0 blue:199 / 255.0 alpha:0.5];
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(10, (_networkStateView.frame.size.height - 20) / 2, 20, 20)];
        imageView.image = [UIImage imageNamed:@"messageSendFail"];
        [_networkStateView addSubview:imageView];
        
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(imageView.frame) + 5, 0, _networkStateView.frame.size.width - (CGRectGetMaxX(imageView.frame) + 15), _networkStateView.frame.size.height)];
        label.font = [UIFont systemFontOfSize:15.0];
        label.textColor = [UIColor grayColor];
        label.backgroundColor = [UIColor clearColor];
        label.text = NSLocalizedString(@"network.disconnection", @"Network disconnection");
        [_networkStateView addSubview:label];
    }
    
    return _networkStateView;
}


#pragma mark - EaseConversationListViewControllerDelegate
- (void)conversationListViewController:(EaseConversationListViewController *)conversationListViewController
            didSelectConversationModel:(id<IConversationModel>)conversationModel
{
    if (conversationModel) {
        EMConversation *conversation = conversationModel.conversation;
        if (conversation) {
            if ([[RobotManager sharedInstance] isRobotWithUsername:conversation.conversationId]) {
                RobotChatViewController *chatController = [[RobotChatViewController alloc] initWithConversationChatter:conversation.conversationId conversationType:conversation.type];
                chatController.title = [[RobotManager sharedInstance] getRobotNickWithUsername:conversation.conversationId];
                [self.navigationController pushViewController:chatController animated:YES];
            } else {
                ChatViewController *chatController = [[ChatViewController alloc] initWithConversationChatter:conversation.conversationId conversationType:conversation.type];
                chatController.title = conversationModel.title;
                [self.navigationController pushViewController:chatController animated:YES];
            }
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:@"setupUnreadMessageCount" object:nil];
        [self.tableView reloadData];
    }
}

#pragma mark - EaseConversationListViewControllerDataSource
- (id<IConversationModel>)conversationListViewController:(EaseConversationListViewController *)conversationListViewController
                                    modelForConversation:(EMConversation *)conversation
{
    EaseConversationModel *model = [[EaseConversationModel alloc] initWithConversation:conversation];
    if (model.conversation.type == EMConversationTypeChat) {
        if ([[RobotManager sharedInstance] isRobotWithUsername:conversation.conversationId]) {
            model.title = [[RobotManager sharedInstance] getRobotNickWithUsername:conversation.conversationId];
        } else {
            UserProfileEntity *profileEntity = [[UserProfileManager sharedInstance] getUserProfileByUsername:conversation.conversationId];
            if (profileEntity) {
                model.title = profileEntity.nickname == nil ? profileEntity.username : profileEntity.nickname;
                model.avatarURLPath = profileEntity.imageUrl;
            }
        }
    } else if (model.conversation.type == EMConversationTypeGroupChat) {
        NSString *imageName = @"groupPublicHeader";
        if (![conversation.ext objectForKey:@"subject"])
        {
            NSArray *groupArray = [[EMClient sharedClient].groupManager getAllGroups];
            for (EMGroup *group in groupArray) {
                if ([group.groupId isEqualToString:conversation.conversationId]) {
                    NSMutableDictionary *ext = [NSMutableDictionary dictionaryWithDictionary:conversation.ext];
                    [ext setObject:group.subject forKey:@"subject"];
                    [ext setObject:[NSNumber numberWithBool:group.isPublic] forKey:@"isPublic"];
                    conversation.ext = ext;
                    break;
                }
            }
        }
        model.title = [conversation.ext objectForKey:@"subject"];
        imageName = [[conversation.ext objectForKey:@"isPublic"] boolValue] ? @"groupPublicHeader" : @"groupPrivateHeader";
        model.avatarImage = [UIImage imageNamed:imageName];
    }
    return model;
}

- (NSString *)conversationListViewController:(EaseConversationListViewController *)conversationListViewController
      latestMessageTitleForConversationModel:(id<IConversationModel>)conversationModel
{
    NSString *latestMessageTitle = @"";
    EMMessage *lastMessage = [conversationModel.conversation latestMessage];
    if (lastMessage) {
        EMMessageBody *messageBody = lastMessage.body;
        switch (messageBody.type) {
            case EMMessageBodyTypeImage:{
                latestMessageTitle = NSLocalizedString(@"message.image1", @"[image]");
            } break;
            case EMMessageBodyTypeText:{
                // 表情映射。
                NSString *didReceiveText = [EaseConvertToCommonEmoticonsHelper
                                            convertToSystemEmoticons:((EMTextMessageBody *)messageBody).text];
                latestMessageTitle = didReceiveText;
                if ([lastMessage.ext objectForKey:MESSAGE_ATTR_IS_BIG_EXPRESSION]) {
                    latestMessageTitle = @"[动画表情]";
                }
            } break;
            case EMMessageBodyTypeVoice:{
                latestMessageTitle = NSLocalizedString(@"message.voice1", @"[voice]");
            } break;
            case EMMessageBodyTypeLocation: {
                latestMessageTitle = NSLocalizedString(@"message.location1", @"[location]");
            } break;
            case EMMessageBodyTypeVideo: {
                latestMessageTitle = NSLocalizedString(@"message.video1", @"[video]");
            } break;
            case EMMessageBodyTypeFile: {
                latestMessageTitle = NSLocalizedString(@"message.file1", @"[file]");
            } break;
            default: {
            } break;
        }
    }
    
    return latestMessageTitle;
}

- (NSString *)conversationListViewController:(EaseConversationListViewController *)conversationListViewController
       latestMessageTimeForConversationModel:(id<IConversationModel>)conversationModel
{
    NSString *latestMessageTime = @"";
    EMMessage *lastMessage = [conversationModel.conversation latestMessage];;
    if (lastMessage) {
        latestMessageTime = [NSDate formattedTimeFromTimeInterval:lastMessage.timestamp];
    }
    return latestMessageTime;
}

#pragma mark - Table view data source
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.dataArray count] + self.tablecellExtCount;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell;
    if(((self.tablecellExtCount == 1) && (indexPath.row == 0))
       || ((self.tablecellExtCount == 2) && (indexPath.row == 1))){
        cell = [[UITableViewCell alloc] init];
        cell.textLabel.text = @"朋友圈";
        return cell;
    }else if((self.tablecellExtCount == 2) && (indexPath.row == 0)){
        cell = [[UITableViewCell alloc] init];
        cell.textLabel.text = @"天际问答";
        return cell;
    }
    
    NSIndexPath *index = [NSIndexPath indexPathForRow:indexPath.row-self.tablecellExtCount inSection:indexPath.section];
    return [super tableView:tableView cellForRowAtIndexPath:index];
}

#pragma mark - Table view delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(((self.tablecellExtCount == 1) && (indexPath.row == 0))
       || ((self.tablecellExtCount == 2) && (indexPath.row == 1))){
        [self presentArticleView];
        return;
    }else if((self.tablecellExtCount == 2) && (indexPath.row == 0)){
        [self presentQuestionAndAnswerView];
        return;
    }
    
    NSIndexPath *index = [NSIndexPath indexPathForRow:indexPath.row-self.tablecellExtCount inSection:indexPath.section];
    [super tableView:tableView didSelectRowAtIndexPath:index];
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(indexPath.row < self.tablecellExtCount){
        return NO;
    }else{
        return YES;
    }
}

- (void)presentQuestionAndAnswerView
{
    QuestionAndAnswerViewController *questionAndAnswerViewController = [[QuestionAndAnswerViewController alloc] init];
    [self.navigationController pushViewController:questionAndAnswerViewController animated:YES];
}

- (void)presentArticleView
{
    SCArticleViewController *articleViewController = [[SCUtils mainStoryBoard] instantiateViewControllerWithIdentifier:@"ArticleView"];
    [self.navigationController pushViewController:articleViewController animated:YES];
}


#pragma mark - public

-(void)refresh
{
    [self refreshAndSortView];
}

-(void)refreshDataSource
{
    [self tableViewDidTriggerHeaderRefresh];
}

- (void)isConnect:(BOOL)isConnect{
    if (!isConnect) {
        self.tableView.tableHeaderView = _networkStateView;
    }
    else{
        self.tableView.tableHeaderView = nil;
    }
    
}

- (void)networkChanged:(EMConnectionState)connectionState
{
    if (connectionState == EMConnectionDisconnected) {
        self.tableView.tableHeaderView = _networkStateView;
    }
    else{
        self.tableView.tableHeaderView = nil;
    }
}

@end