//
//  SCNormalConversationViewController.m
//  SamChat
//
//  Created by HJ on 4/20/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCNormalConversationViewController.h"
#import "ChatViewController.h"

@interface SCNormalConversationViewController ()<EaseConversationListViewControllerDelegate>

@end

@implementation SCNormalConversationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.currentListMessageFromView = MESSAGE_FROM_VIEW_CHAT;
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
            chatController.messageExtDictionary = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_CHAT};
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
        DebugLog(@"normal list:%@", conversation.ext);
        if((conversation.ext==nil) || conversation.type != EMConversationTypeChat){
            [serviceConversations addObject:conversation];
        }else if([[conversation.ext valueForKey:MESSAGE_FROM_VIEW_CHAT] isEqualToNumber:[NSNumber numberWithBool:YES]]) {
            [serviceConversations addObject:conversation];
        }
    }
    return serviceConversations;
}

@end
