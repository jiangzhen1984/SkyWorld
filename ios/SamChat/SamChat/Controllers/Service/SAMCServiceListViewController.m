//
//  SAMCServiceListViewController.m
//  SamChat
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCServiceListViewController.h"
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
#import "ReceivedQuestion.h"
#import "SCCoreDataManager.h"
#import "SessionExtension.h"

#define SessionListTitle @"天际商家"

@interface SAMCServiceListViewController ()<NIMLoginManagerDelegate,NTESListHeaderDelegate>

@property (nonatomic,strong) UILabel *titleLabel;

@property (nonatomic,strong) NTESListHeader *header;
@end

@implementation SAMCServiceListViewController

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.autoRemoveRemoteSession = [[NTESBundleSetting sharedConfig] autoRemoveRemoteSession];
    }
    return self;
}

- (void)dealloc{
    [[NIMSDK sharedSDK].loginManager removeDelegate:self];
}


- (void)viewDidLoad{
    [super viewDidLoad];
    self.currentListMessageFromView = MESSAGE_FROM_VIEW_SEARCH;
    [[NIMSDK sharedSDK].loginManager addDelegate:self];
    self.header = [[NTESListHeader alloc] initWithFrame:CGRectMake(0, 0, self.view.width, 0)];
    self.header.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    self.header.delegate = self;
    [self.view addSubview:self.header];
    
    self.emptyTipLabel = [[UILabel alloc] init];
    self.emptyTipLabel.text = @"还没有会话，在通讯录中找个人聊聊吧";
    [self.emptyTipLabel sizeToFit];
    self.emptyTipLabel.hidden = self.recentSessions.count;
    [self.view addSubview:self.emptyTipLabel];
    
    self.navigationItem.titleView  = [self titleView];
}

- (void)reload{
    [super reload];
    self.emptyTipLabel.hidden = self.recentSessions.count;
}

- (BOOL)shouldIncludeRecentSession:(NIMRecentSession *)recentSession
{
    //return YES; // TODO: delete
    if (recentSession.session.sessionType != NIMSessionTypeP2P) {
        return NO;
    }else{
        NSManagedObjectContext *context = [SCCoreDataManager sharedInstance].confinementObjectContextOfmainContext;
        __block BOOL flag = [SessionExtension serviceTagOfSession:recentSession.session.sessionId inManagedObjectContext:context];
        if (flag == NO) {
            NSArray *messages = [[NIMSDK sharedSDK].conversationManager messagesInSession:recentSession.session
                                                                                  message:nil
                                                                                    limit:recentSession.unreadCount];
            [messages enumerateObjectsUsingBlock:^(NIMMessage *message, NSUInteger idx, BOOL * _Nonnull stop) {
                NSNumber *messageFromView = [message.remoteExt valueForKey:MESSAGE_FROM_VIEW];
                if([messageFromView isEqualToNumber:MESSAGE_FROM_VIEW_SEARCH]) {
                    *stop = YES;
                    flag = YES;
                }
            }];
        }
        return flag;
    }
}

- (void)onSelectedRecent:(NIMRecentSession *)recent atIndexPath:(NSIndexPath *)indexPath{
    NTESSessionViewController *vc = [[NTESSessionViewController alloc] initWithSession:recent.session];
    vc.messageExtDictionary = [self getMessageExtDictionaryWithSessionId:recent.session.sessionId];
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
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [SessionExtension updateSession:recent.session.sessionId
                         serviceTag:NO
             inManagedObjectContext:mainContext];
    
    //清理本地数据
    [self.recentSessions removeObjectAtIndex:indexPath.row];
    [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
    
    id<NIMConversationManager> manager = [[NIMSDK sharedSDK] conversationManager];
    [manager markAllMessagesReadInSession:recent.session];
    if ([SessionExtension shouldDeleteSession:recent.session.sessionId]) {
        //id<NIMConversationManager> manager = [[NIMSDK sharedSDK] conversationManager];
        //[manager deleteRecentSession:recent];
        [manager deleteAllmessagesInSession:recent.session removeRecentSession:YES];
        //如果删除本地会话后就不允许漫游当前会话，则需要进行一次删除服务器会话的操作
        if (self.autoRemoveRemoteSession)
        {
            [manager deleteRemoteSessions:@[recent.session]
                               completion:nil];
        }
    }
    
    self.emptyTipLabel.hidden = self.recentSessions.count;
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
    self.tableView.top = self.header.height;
    self.tableView.height = self.view.height - self.tableView.top;
    self.header.bottom    = self.tableView.top + self.tableView.contentInset.top;
    self.emptyTipLabel.centerX = self.view.width * .5f;
    self.emptyTipLabel.centerY = self.tableView.height * .5f;
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

#pragma mark - Get Message Ext Dictionary
- (NSDictionary *)getMessageExtDictionaryWithSessionId:(NSString *)sessionId
{
    NSMutableDictionary *extDic = [NSMutableDictionary dictionaryWithDictionary:@{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_VENDOR}];
    NSString *username = sessionId;
    NSArray *questionIds = [ReceivedQuestion unresponsedQuestionIdsFrom:username
                                                          markResponsed:YES
                                                 inManagedObjectContext:[SCCoreDataManager sharedInstance].confinementObjectContextOfmainContext];
    if(questionIds && (questionIds.count>0)){
        NSMutableString *idString = [[NSMutableString alloc] initWithString:questionIds[0]];
        for (int i=1; i<questionIds.count; i++) {
            [idString appendFormat:@" %@",questionIds[i]];
        }
        [extDic addEntriesFromDictionary:@{MESSAGE_QUESTIONS:idString}];
    }
    return extDic;
}

@end
