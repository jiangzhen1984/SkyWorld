//
//  ServiceSearchViewController.m
//  SamChat
//
//  Created by HJ on 3/25/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "ServiceSearchViewController.h"
#import "AFNetworking.h"
#import "SCSkyWorldAPI.h"
#import "SCConfig.h"
#import "SCTableViewCell.h"
#import "ReceivedAnswer.h"
#import "SendQuestion.h"
#import "AnswerDetailViewController.h"
#import "UserSettingViewController.h"
#import "SCHotTopicView.h"
#import "HotTopic.h"

@interface ServiceSearchViewController () <UITableViewDataSource,UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITextField *searchTextField;
@property (weak, nonatomic) IBOutlet UIImageView *homeImage;
@property (weak, nonatomic) IBOutlet UIView *searchBar;

@property (nonatomic, strong) SCHotTopicView *hotTopicView;
@property (nonatomic, strong) UITableView *receivedAnswerTableView;
@property (nonatomic, strong) UIButton *cancelSearchButton;

@property (nonatomic, strong) NSLayoutConstraint *searchBarTopSpaceConstraint;
@property (nonatomic, strong) NSLayoutConstraint *hotTopicTopSpaceConstraint;
@property (nonatomic, assign) NSString *currentQuestionID;
@property (nonatomic, copy) NSString *currentQuestion;
@property (nonatomic, assign) BOOL isSearching;

@property (nonatomic, strong) NSMutableArray *hotTopics;
@property (nonatomic, strong) NSMutableArray *answers;
//- (void)hideHomeImage:(BOOL)hide withsearchBarToTop:(CGFloat)barTop duration:(CGFloat)duration;

@end

@implementation ServiceSearchViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    [self setupSubViews];
    
    self.isSearching = false;
}

- (void)setupSubViews
{
    // hotTopicView
    _hotTopicView = [SCHotTopicView new];
    _hotTopicView.translatesAutoresizingMaskIntoConstraints = NO;
    _hotTopicView.tableView.dataSource = self;
    _hotTopicView.tableView.delegate = self;
    __weak typeof(self) weakSelf = self;
    [_hotTopicView setReloadHotTopicBlock:^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf asyncLoadHotTopicsFromServerWithReset:YES];
            [weakSelf.hotTopicView endReloadRefreshing];
        });
    }];
    [_hotTopicView setLoadMoreHotTopicBlock:^{
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf asyncLoadHotTopicsFromServerWithReset:NO];
            [weakSelf.hotTopicView endLoadMoreRefreshing];
        });
    }];
    [self.view addSubview:_hotTopicView];

    // receivedAnswerTableView
    _receivedAnswerTableView = [UITableView new];
    _receivedAnswerTableView.translatesAutoresizingMaskIntoConstraints = NO;
    _receivedAnswerTableView.dataSource = self;
    _receivedAnswerTableView.delegate = self;
    _receivedAnswerTableView.hidden = YES; // default hide the receivedAnswer View
    _receivedAnswerTableView.backgroundColor = [UIColor clearColor];
    [self.view addSubview:_receivedAnswerTableView];
    
    // cancelSearchButton
    _cancelSearchButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _cancelSearchButton.translatesAutoresizingMaskIntoConstraints = NO;
    _cancelSearchButton.backgroundColor = [UIColor orangeColor];
    [_cancelSearchButton setTitle:@"Cancel" forState:UIControlStateNormal];
    _cancelSearchButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    [_cancelSearchButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_cancelSearchButton addTarget:self action:@selector(cancelSearch:) forControlEvents:UIControlEventTouchUpInside];
    _cancelSearchButton.hidden = YES;
    [self.view addSubview:_cancelSearchButton];
    
    // hotTopic View Contraints
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-20-[_hotTopicView]-20-|"
                                                                     options:0
                                                                     metrics:nil
                                                                       views:NSDictionaryOfVariableBindings(_hotTopicView)]];
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[_hotTopicView]-20-|"
                                                                      options:0
                                                                      metrics:nil
                                                                        views:NSDictionaryOfVariableBindings(_searchBar, _hotTopicView)]];
    self.hotTopicTopSpaceConstraint = [NSLayoutConstraint constraintWithItem:_hotTopicView
                                                                   attribute:NSLayoutAttributeTop
                                                                   relatedBy:NSLayoutRelationEqual
                                                                      toItem:self.view
                                                                   attribute:NSLayoutAttributeTop
                                                                  multiplier:1.0f
                                                                    constant:240];
    [self.view addConstraint:self.hotTopicTopSpaceConstraint];
    
    // receivedAnswer View Contraints
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_receivedAnswerTableView]|"
                                                                     options:0
                                                                     metrics:nil
                                                                       views:NSDictionaryOfVariableBindings(_receivedAnswerTableView)]];
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-120-[_receivedAnswerTableView][_cancelSearchButton(40)]|"
                                                                      options:0
                                                                      metrics:nil
                                                                        views:NSDictionaryOfVariableBindings(_receivedAnswerTableView, _cancelSearchButton)]];
    
    // cancelSearchButton Contraints
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[_cancelSearchButton]|"
                                                                      options:0
                                                                      metrics:nil
                                                                        views:NSDictionaryOfVariableBindings(_cancelSearchButton)]];
    
    // searchBar
    [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-20-[_searchBar]-20-|"
                                                                      options:0
                                                                      metrics:nil
                                                                        views:NSDictionaryOfVariableBindings(_searchBar)]];
    
    [_searchBar addConstraint:[NSLayoutConstraint constraintWithItem:_searchBar
                                                           attribute:NSLayoutAttributeHeight
                                                           relatedBy:NSLayoutRelationEqual
                                                              toItem:nil
                                                           attribute:NSLayoutAttributeNotAnAttribute
                                                          multiplier:1.0f
                                                            constant:44]];
    self.searchBarTopSpaceConstraint = [NSLayoutConstraint constraintWithItem:_searchBar
                                                                    attribute:NSLayoutAttributeTop
                                                                    relatedBy:NSLayoutRelationEqual
                                                                       toItem:self.view
                                                                    attribute:NSLayoutAttributeTop
                                                                   multiplier:1.0f
                                                                     constant:130];
    
    [self.view addConstraint:self.searchBarTopSpaceConstraint];
}

#pragma mark - Lazy initialization
- (NSMutableArray *)hotTopics
{
    if(_hotTopics == nil){
        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
        _hotTopics = [NSMutableArray arrayWithArray:[HotTopic hotTopicsWithType:0 inManagedObjectContext:mainContext]];
        [self asyncLoadHotTopicsFromServerWithReset:YES];
    }
    return _hotTopics;
}

- (NSMutableArray *)answers
{
    if(_answers == nil){
        _answers = [[NSMutableArray alloc] init];
    }
    return _answers;
}

- (void)asyncLoadHotTopicsFromServerWithReset:(BOOL)resetflag
{
    [[SamChatClient sharedInstance] queryTopicListWithOptType:0
                                                    topicType:0
                                                        reset:resetflag
                                                   completion:^(BOOL success, NSArray *topics, SCSkyWorldError *error) {
                                                       if(success && ([topics count] > 0)){
                                                           if(resetflag){
                                                               [self.hotTopics removeAllObjects];
                                                           }
                                                           [self.hotTopics addObjectsFromArray:topics];
                                                           [HotTopic updateHotTopicsInPrivateManagedObjectContextWithArray:topics];
                                                           [self.hotTopicView.tableView reloadData];
                                                       }
                                                   }];
}


#pragma mark - New Question Process
- (void)setIsSearching:(BOOL)isSearching
{
    if(_isSearching != isSearching) {
        _isSearching = isSearching;
    }
    self.hotTopicView.hidden = isSearching;
    self.receivedAnswerTableView.hidden = !isSearching;
    self.cancelSearchButton.hidden = !isSearching;
}


- (IBAction)pushNewQuestion:(id)sender
{
    DebugLog(@"token: %@", [SCUserProfileManager sharedInstance].token);
    [self.answers removeAllObjects];
    if((!self.searchTextField.text) || (self.searchTextField.text.length <= 0)){
        return;
    }
    self.currentQuestion = self.searchTextField.text;
    NSString *urlString = [SCSkyWorldAPI urlNewQuestionWithQuestion:self.currentQuestion];
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
                 [self questionSuccessWithResponse:response];
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

- (void)questionSuccessWithResponse:(NSDictionary *)response
{
    self.currentQuestionID = [response[SKYWORLD_QUESTION_ID] stringValue];
    self.isSearching = true;
    
    NSDictionary *questionInfo = @{SEND_QUESTION_QUESTION:self.currentQuestion,
                                   SEND_QUESTION_QUESTION_ID:self.currentQuestionID};
    
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    [mainContext performBlockAndWait:^{
        [SendQuestion sendQuestionWithInfo:questionInfo
                    inManagedObjectContext:mainContext];
    }];
}

#pragma mark - Cancel Question Process
- (IBAction)cancelSearch:(UIButton *)sender
{
    self.isSearching = false;
    NSString *urlString = [SCSkyWorldAPI urlCancleQuestionWithQuestionID:self.currentQuestionID];
    
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:urlString
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
             if([responseObject isKindOfClass:[NSDictionary class]]) {
                 DebugLog(@"%@", responseObject);
                 
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DebugLog(@"Error: %@", error);
         }];
}

#pragma mark - Table Data Source & Delegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSInteger count = 0;
    if(tableView == self.hotTopicView.tableView){
        count = self.hotTopics.count;
    }else if(tableView == self.receivedAnswerTableView){
        count = self.answers.count;
    }
    return count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    if(tableView == self.receivedAnswerTableView){
        static NSString *QuestionAnswerCellIdentifier = @"QuestionAnswerTableCell";
        cell = (SCTableViewCell *)[tableView dequeueReusableCellWithIdentifier:QuestionAnswerCellIdentifier];
        if (cell == nil) {
            cell = [[SCTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:QuestionAnswerCellIdentifier];
        }
        
        ((SCTableViewCell*)cell).model = self.answers[indexPath.row];
    }else if(tableView == self.hotTopicView.tableView){
        static NSString *HotQuestionCellIdentifier=@"HotQuestionTableCell";
        cell = [tableView dequeueReusableCellWithIdentifier:HotQuestionCellIdentifier];
        if (cell == nil) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:HotQuestionCellIdentifier];
        }
        //cell.textLabel.text = self.hotTopics[indexPath.row];
        HotTopicCellModel *topic = self.hotTopics[indexPath.row];
        cell.textLabel.text = topic.name;
    }
    return cell;
}

-(NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
    if(tableView == self.receivedAnswerTableView){
        return @"更多答案即将到来...";
    }else{
        return nil;
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat height = 0;
    if(tableView == self.hotTopicView.tableView){
        height = 40;
    }else if(tableView == self.receivedAnswerTableView){
        height = 60;
    }
    return height;
}

//-(CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
//{
//    return 40;
//}

//- (void)tableView:(UITableView *)tableView willDisplayFooterView:(UIView *)view forSection:(NSInteger)section
//{
//    view.tintColor = [UIColor clearColor];
//}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if(self.isSearching && (tableView == self.receivedAnswerTableView)){
        AnswerDetailViewController *answerDetailController = [self.storyboard instantiateViewControllerWithIdentifier:@"AnswerDetailView"];
        answerDetailController.question = self.currentQuestion;
        answerDetailController.answer = self.answers[indexPath.row];
        [self.navigationController pushViewController:answerDetailController animated:YES];
    }else if(tableView == self.hotTopicView.tableView){
        self.searchTextField.text = ((HotTopicCellModel *)self.hotTopics[indexPath.row]).name;
        [self.searchTextField becomeFirstResponder];
        [self pushNewQuestion:nil];
    }
}

#pragma mark - SCAnswerPushDelegate
- (void)didReceiveNewAnswer:(ReceivedAnswer *)answer
{
    if([answer.question_id isEqualToString:self.currentQuestionID]){
        [self.answers addObject:answer];
        [self.receivedAnswerTableView reloadData];
    }
}

#pragma mark - UI Animation

- (IBAction)begingEditQuestion:(UITextField *)sender
{
    DebugLog(@"edit beging");
    [self hideHomeImage:YES withsearchBarToTop:20 tableToTop:80 duration:0.4f];
    
//    [UIView animateWithDuration:0.4f
//                     animations:^{
//                         [self.view addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|-30-[_searchBar]"
//                                                                                           options:0
//                                                                                           metrics:nil
//                                                                                             views:NSDictionaryOfVariableBindings(_searchBar)]];
//                         [self.view layoutIfNeeded];
//                     }];
    
    
//    [UIView animateWithDuration:0.4f
//                     animations:^{
//                         self.homeImage.hidden = YES;
//                         [self.searchBarToTop setConstant:20];
//                         [self.tableViewToTop setConstant:self.searchBar.frame.size.height+40];
//                         [self.view layoutIfNeeded];
//                     }];
    
}

- (void)hideHomeImage:(BOOL)hide withsearchBarToTop:(CGFloat)barTop tableToTop:(CGFloat)tableTop duration:(CGFloat)duration;
{
    [UIView animateWithDuration:duration
                     animations:^{
                         self.homeImage.hidden = hide;
                         [self.searchBarTopSpaceConstraint setConstant:barTop];
                         [self.hotTopicTopSpaceConstraint setConstant:tableTop];
                         [self.view layoutIfNeeded];
                     }];
}

- (IBAction)backgroundTap:(id)sender
{
    [self.searchTextField resignFirstResponder];
    [self hideHomeImage:NO withsearchBarToTop:130 tableToTop:200 duration:0.4f];
}


@end
