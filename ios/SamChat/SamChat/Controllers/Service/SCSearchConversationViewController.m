//
//  SCSearchConversationViewController.m
//  SamChat
//
//  Created by HJ on 4/21/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCSearchConversationViewController.h"
#import "SCServiceSearchBar.h"
#import "SCHotTopicsView.h"

@interface SCSearchConversationViewController ()<EaseConversationListViewControllerDelegate,SCServiceSearchBarDelegate,SCHotTopicsDelegete>

@property (strong, nonatomic) SCServiceSearchBar *serviceSearchBar;
@property (strong, nonatomic) SCHotTopicsView *hotpicsView;

@end

@implementation SCSearchConversationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.delegate = self;
    [self setupSubviews];
    [self tableViewDidTriggerHeaderRefresh];
}

- (void)setupSubviews
{
    _serviceSearchBar = [[SCServiceSearchBar alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 44)];
    _serviceSearchBar.delegate = self;
    [self.view addSubview:_serviceSearchBar];
    
    //[self.view addSubview:self.hotpicsView];
    
    self.tableView.frame = CGRectMake(0, 44, self.view.frame.size.width, self.view.frame.size.height-44);
    //self.tableView.hidden = YES;
}

#pragma mark - lazy loading
- (SCHotTopicsView *)hotpicsView
{
    if(_hotpicsView == nil){
        _hotpicsView = [[SCHotTopicsView alloc] initWithFrame:CGRectMake(0, 44, self.view.frame.size.width, self.view.frame.size.height-44)];
        _hotpicsView.delegate = self;
    }
    return _hotpicsView;
}

#pragma mark - Push Question
- (void)pushNewQuestion:(NSString *)question
{
    DebugLog(@"token: %@", [SCUserProfileManager sharedInstance].token);
    NSString *urlString = [SCSkyWorldAPI urlNewQuestionWithQuestion:question];
    //    DebugLog(@"pushing new question with: %@", urlString);
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DebugLog(@"%@", responseObject);
                 NSDictionary *response = responseObject;
                 NSInteger errorCode = [(NSNumber *)response[SKYWORLD_RET] integerValue];
                 if(errorCode) {
                     [self questionFailedWithErrorCode:errorCode];
                     return;
                 }
                 [self question:question SuccessWithResponse:response];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DebugLog(@"Error: %@", error);
         }];
}


- (void)questionFailedWithErrorCode:(NSInteger)errorCode
{
    DebugLog(@"question error code: %ld", errorCode);
}

- (void)question:(NSString *)question SuccessWithResponse:(NSDictionary *)response
{
    NSString *questionId = [response[SKYWORLD_QUESTION_ID] stringValue];
    
    NSDictionary *questionInfo = @{SEND_QUESTION_QUESTION:question,
                                   SEND_QUESTION_QUESTION_ID:questionId};
    
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        [SendQuestion sendQuestionWithInfo:questionInfo
                    inManagedObjectContext:mainContext];
    }];
}

#pragma mark - SCServiceSearchBarDelegate
- (void)searchEditingDidBegin
{
    DebugLog(@"begin edit");
}

- (void)searchEditingDidEndOnExit
{
    DebugLog(@"end edit");
    NSString *question = self.serviceSearchBar.searchContent;
    if ((question==nil) || (question.length<=0)) {
        return;
    }
    [self pushNewQuestion:self.serviceSearchBar.searchContent];
}

#pragma mark - SCHotTopicsDelegete
- (void)didSelectHotTopic:(NSString *)topicContent
{
    DebugLog(@"select topic: %@", topicContent);
}

#pragma mark - EaseConversationListViewControllerDelegate
- (void)conversationListViewController:(EaseConversationListViewController *)conversationListViewController
            didSelectConversationModel:(id<IConversationModel>)conversationModel
{
    if (conversationModel) {
        EMConversation *conversation = conversationModel.conversation;
        if (conversation) {
            ChatViewController *chatController = [[ChatViewController alloc] initWithConversationChatter:conversation.conversationId conversationType:conversation.type];
            chatController.messageExtDictionary = @{MESSAGE_FROM_VIEW:MESSAGE_FROM_VIEW_SEARCH};
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
           ([[conversation.ext valueForKey:MESSAGE_FROM_VIEW_VENDOR] isEqualToNumber:[NSNumber numberWithBool:YES]])) {
            [serviceConversations addObject:conversation];
        }
    }
    return serviceConversations;
}

- (NSString *)currentListMessageFromView
{
    return MESSAGE_FROM_VIEW_VENDOR;
}

@end
