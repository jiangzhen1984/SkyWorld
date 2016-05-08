//
//  SCServiceConversationViewController.m
//  SamChat
//
//  Created by HJ on 4/20/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCServiceConversationViewController.h"
#import "ChatViewController.h"

@interface SCServiceConversationViewController ()<EaseConversationListViewControllerDelegate>

@end

@implementation SCServiceConversationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.delegate = self;
    self.currentListMessageFromView = MESSAGE_FROM_VIEW_SEARCH;
    [self tableViewDidTriggerHeaderRefresh];
}

#pragma mark - EaseConversationListViewControllerDelegate
- (void)conversationListViewController:(EaseConversationListViewController *)conversationListViewController
            didSelectConversationModel:(id<IConversationModel>)conversationModel
{
    if (conversationModel) {
        EMConversation *conversation = conversationModel.conversation;
        if (conversation) {
            ChatViewController *chatController = [[ChatViewController alloc] initWithConversationChatter:conversation.conversationId conversationType:conversation.type];
            //chatController.messageExtDictionary = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_VENDOR};
            chatController.messageExtDictionary = [self getMessageExtDictionaryWithConversationId:conversation.conversationId];
            chatController.title = conversationModel.title;
            [self.navigationController pushViewController:chatController animated:YES];
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:@"setupUnreadMessageCount" object:nil];
        [self.tableView reloadData];
    }
}

- (NSArray *)getAllConversations
{
    NSArray *conversations = [[EMClient sharedClient].chatManager getAllConversations];
    NSMutableArray *serviceConversations = [[NSMutableArray alloc] init];
    for (EMConversation *conversation in conversations) {
        if((conversation.ext!=nil) &&
           ([[conversation.ext valueForKey:MESSAGE_FROM_VIEW_SEARCH] isEqualToNumber:[NSNumber numberWithBool:YES]])) {
            [serviceConversations addObject:conversation];
        }
    }
    return serviceConversations;
}

#pragma mark - Get Message Ext Dictionary
- (NSDictionary *)getMessageExtDictionaryWithConversationId:(NSString *)conversationId
{
    NSMutableDictionary *extDic = [NSMutableDictionary dictionaryWithDictionary:@{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_VENDOR}];
    NSString *username = conversationId;
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
