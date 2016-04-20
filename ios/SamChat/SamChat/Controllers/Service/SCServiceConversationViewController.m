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

- (void)viewDidLoad {
    [super viewDidLoad];
    self.delegate = self;
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
           ([[conversation.ext valueForKey:CONVERSATION_TYPE_KEY_QUESTION] isEqualToNumber:[NSNumber numberWithBool:YES]])) {
            [serviceConversations addObject:conversation];
        }
    }
    return serviceConversations;
}

@end
