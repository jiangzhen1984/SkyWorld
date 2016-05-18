//
//  SAMCSearchListViewController.m
//  SamChat
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCSearchListViewController.h"
#import "NTESSessionViewController.h"
#import "UIView+NTES.h"
#import "NTESBundleSetting.h"
#import "NTESListHeader.h"
#import "NTESClientsTableViewController.h"
#import "NTESSnapchatAttachment.h"
#import "NTESJanKenPonAttachment.h"
#import "NTESChartletAttachment.h"
#import "NTESWhiteboardAttachment.h"
#import "NTESSessionUtil.h"
#import "NTESPersonalCardViewController.h"
#import "SCServiceSearchBar.h"
#import "SamChatClient.h"
#import "SAMCHotTopicsView.h"

#define SessionListTitle @"天际搜索"

@interface SAMCSearchListViewController ()<NIMLoginManagerDelegate,NTESListHeaderDelegate,SCServiceSearchBarDelegate,SAMCHotTopicsDelegete>

@property (strong, nonatomic) SCServiceSearchBar *serviceSearchBar;
@property (nonatomic, strong) SAMCHotTopicsView *hotTopicsView;

@property (nonatomic,strong) UILabel *titleLabel;

@property (nonatomic,strong) NTESListHeader *header;
@end

@implementation SAMCSearchListViewController

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.autoRemoveRemoteSession = [[NTESBundleSetting sharedConfig] autoRemoveRemoteSession];
    }
    return self;
}

- (void)dealloc{
    [[NIMSDK sharedSDK].loginManager removeDelegate:self];
    if (_hotTopicsView) {
        _hotTopicsView.delegate = nil;
    }
}

- (void)viewDidLoad{
    [super viewDidLoad];
    self.currentListMessageFromView = MESSAGE_FROM_VIEW_VENDOR;
    [[NIMSDK sharedSDK].loginManager addDelegate:self];
    self.header = [[NTESListHeader alloc] initWithFrame:CGRectMake(0, 0, self.view.width, 0)];
    self.header.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    self.header.delegate = self;
    [self.view addSubview:self.header];
    
    [self setupSubviews];
    self.navigationItem.titleView  = [self titleView];
}

- (void)setupSubviews
{
    _serviceSearchBar = [[SCServiceSearchBar alloc] initWithFrame:CGRectMake(0, 64, self.view.frame.size.width, 44)];
    _serviceSearchBar.delegate = self;
    [self.view addSubview:_serviceSearchBar];
}

#pragma mark - lazy loading
- (SAMCHotTopicsView *)hotTopicsView
{
    if (_hotTopicsView == nil) {
        _hotTopicsView = [[SAMCHotTopicsView alloc] initWithFrame:CGRectMake(0, 108, self.view.frame.size.width, self.view.frame.size.height-108-44)];
        _hotTopicsView.delegate = self;
    }
    return _hotTopicsView;
}

- (void)reload{
    //[super reload];
    if (!self.recentSessions.count) {
        self.tableView.hidden = YES;
        [self.view addSubview:self.hotTopicsView];
    }else{
        self.tableView.hidden = NO;
        [self.tableView reloadData];
        if (_hotTopicsView) {
            [_hotTopicsView removeFromSuperview];
            _hotTopicsView.delegate = nil;
            _hotTopicsView = nil;
        }
    }
}

- (BOOL)shouldIncludeRecentSession:(NIMRecentSession *)recentSession
{
    if (recentSession.session.sessionType != NIMSessionTypeP2P) {
        return NO;
    }else{
        __block BOOL flag = [[SamChatClient sharedClient].sessionManager searchTagOfSession:recentSession.session.sessionId];
        if ((flag == NO) && (recentSession.unreadCount > 0)) {
            NSArray *messages = [[NIMSDK sharedSDK].conversationManager messagesInSession:recentSession.session
                                                                                  message:nil
                                                                                    limit:recentSession.unreadCount];
            [messages enumerateObjectsUsingBlock:^(NIMMessage *message, NSUInteger idx, BOOL * _Nonnull stop) {
                NSNumber *messageFromView = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
                if([messageFromView isEqualToNumber:MESSAGE_FROM_VIEW_VENDOR]) {
                    *stop = YES;
                    flag = YES;
                }
            }];
        }
        return flag;
    }
}

- (void)onSelectedRecent:(NIMRecentSession *)recent atIndexPath:(NSIndexPath *)indexPath{
    //NTESSessionViewController *vc = [[NTESSessionViewController alloc] initWithSession:recent.session];
    //vc.messageExtDictionary = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_SEARCH};
    NSDictionary *messageExtDictionary = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_SEARCH};
    NTESSessionViewController *vc = [[NTESSessionViewController alloc] initWithSession:recent.session
                                                                  messageExtDictionary:messageExtDictionary];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)onSelectedAvatar:(NIMRecentSession *)recent
             atIndexPath:(NSIndexPath *)indexPath{
    if (recent.session.sessionType == NIMSessionTypeP2P) {
        NTESPersonalCardViewController *vc = [[NTESPersonalCardViewController alloc] initWithUserId:recent.session.sessionId];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

- (void)onDeleteRecentAtIndexPath:(NIMRecentSession *)recent atIndexPath:(NSIndexPath *)indexPath{
    //[super onDeleteRecentAtIndexPath:recent atIndexPath:indexPath];
    BOOL deleteFlag = [[SamChatClient sharedClient].sessionManager deleteSession:recent.session.sessionId
                                                       ifNeededAfterClearTagType:MESSAGE_FROM_VIEW_VENDOR];
    //清理本地数据
    [self.recentSessions removeObjectAtIndex:indexPath.row];
    [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    
    id<NIMConversationManager> manager = [[NIMSDK sharedSDK] conversationManager];
    [manager markAllMessagesReadInSession:recent.session];
    if (deleteFlag) {
        //id<NIMConversationManager> manager = [[NIMSDK sharedSDK] conversationManager];
        //[manager deleteRecentSession:recent];
        [manager deleteAllmessagesInSession:recent.session removeRecentSession:YES];
        [[SamChatClient sharedClient].searchManager deleteAllQuestionMessagesWithSessionId:recent.session.sessionId];
        //如果删除本地会话后就不允许漫游当前会话，则需要进行一次删除服务器会话的操作
        if (self.autoRemoveRemoteSession)
        {
            [manager deleteRemoteSessions:@[recent.session]
                               completion:nil];
        }
    }
    
    if (!self.recentSessions.count) {
        [self reload];
    }
}

- (void)viewDidLayoutSubviews{
    [super viewDidLayoutSubviews];
    [self refreshSubview];
}


- (NSString *)nameForRecentSession:(NIMRecentSession *)recent{
    if ([recent.session.sessionId isEqualToString:[[NIMSDK sharedSDK].loginManager currentAccount]]) {
        return @"我的电脑";
    }
    return [super nameForRecentSession:recent];
}

#pragma mark - SCServiceSearchBarDelegate
- (void)searchEditingDidBegin
{
    DDLogDebug(@"begin edit");
}

- (void)searchEditingDidEndOnExit
{
    DDLogDebug(@"end edit");
    NSString *question = self.serviceSearchBar.searchContent;
    if ((question==nil) || (question.length<=0)) {
        return;
    }
    [[SamChatClient sharedClient].searchManager sendNewQuestion:question completion:^(NSError *error) {
        if (error == nil) {
            
        }else{
        }
    }];
}

#pragma mark - SAMCHotTopicsDelegete
- (void)didSelectHotTopic:(NSString *)topicContent
{
    DDLogDebug(@"select topic: %@", topicContent);
}

#pragma mark - SessionListHeaderDelegate

- (void)didSelectRowType:(NTESListHeaderType)type{
    //多人登录
    switch (type) {
        case ListHeaderTypeLoginClients:{
            NTESClientsTableViewController *vc = [[NTESClientsTableViewController alloc] initWithNibName:nil bundle:nil];
            [self.navigationController pushViewController:vc animated:YES];
            break;
        }
        default:
            break;
    }
}


#pragma mark - NIMLoginManagerDelegate
- (void)onLogin:(NIMLoginStep)step{
    [super onLogin:step];
    switch (step) {
        case NIMLoginStepLinkFailed:
            self.titleLabel.text = [SessionListTitle stringByAppendingString:@"(未连接)"];
            break;
        case NIMLoginStepLinking:
            self.titleLabel.text = [SessionListTitle stringByAppendingString:@"(连接中)"];
            break;
        case NIMLoginStepLinkOK:
        case NIMLoginStepSyncOK:
            self.titleLabel.text = SessionListTitle;
            break;
        case NIMLoginStepSyncing:
            self.titleLabel.text = [SessionListTitle stringByAppendingString:@"(同步数据)"];
            break;
        default:
            break;
    }
    [self.titleLabel sizeToFit];
    self.titleLabel.centerX   = self.navigationItem.titleView.width * .5f;
    [self.header refreshWithType:ListHeaderTypeNetStauts value:@(step)];
    [self.view setNeedsLayout];
}

- (void)onMultiLoginClientsChanged
{
    [self.header refreshWithType:ListHeaderTypeLoginClients value:[NIMSDK sharedSDK].loginManager.currentLoginClients];
    [self.view setNeedsLayout];
}

#pragma mark - Private
- (void)refreshSubview{
    [self.titleLabel sizeToFit];
    self.titleLabel.centerX   = self.navigationItem.titleView.width * .5f;
    //self.tableView.top = self.header.height+self.header.frame.origin.y;
    self.tableView.top = self.header.height + 44;
    self.tableView.height = self.view.height - self.tableView.top;
    self.header.bottom    = self.tableView.top + self.tableView.contentInset.top;
}

- (UIView*)titleView{
    self.titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.titleLabel.text =  SessionListTitle;
    self.titleLabel.font = [UIFont boldSystemFontOfSize:15.f];
    [self.titleLabel sizeToFit];
    
    UIView *titleView = [[UIView alloc] init];
    titleView.height = self.titleLabel.height;
    
    [titleView addSubview:self.titleLabel];
    return titleView;
}


- (NSString *)contentForRecentSession:(NIMRecentSession *)recent{
    if (recent.lastMessage.messageType == NIMMessageTypeCustom) {
        NIMCustomObject *object = recent.lastMessage.messageObject;
        NSString *text = @"";
        if ([object.attachment isKindOfClass:[NTESSnapchatAttachment class]]) {
            text = @"[阅后即焚]";
        }
        else if ([object.attachment isKindOfClass:[NTESJanKenPonAttachment class]]) {
            text = @"[猜拳]";
        }
        else if ([object.attachment isKindOfClass:[NTESChartletAttachment class]]) {
            text = @"[贴图]";
        }
        else if ([object.attachment isKindOfClass:[NTESWhiteboardAttachment class]]) {
            text = @"[白板]";
        }else{
            text = @"[未知消息]";
        }
        if (recent.session.sessionType == NIMSessionTypeP2P) {
            return text;
        }else{
            NSString *nickName = [NTESSessionUtil showNick:recent.lastMessage.from inSession:recent.lastMessage.session];
            return nickName.length ? [nickName stringByAppendingFormat:@" : %@",text] : @"";
        }
    }
    return [super contentForRecentSession:recent];
}

@end
