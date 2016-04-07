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
#import "SCPushDispatcher.h"
#import "SCTableViewCell.h"
#import "ReceivedAnswer.h"
#import "SendQuestion.h"
#import "AnswerDetailViewController.h"
#import "UserSettingViewController.h"

@interface ServiceSearchViewController () <SCAnswerPushDelegate,UITableViewDataSource,UITableViewDelegate>

@property (weak, nonatomic) IBOutlet UITextField *searchTextField;
@property (weak, nonatomic) IBOutlet UIImageView *homeImage;
@property (weak, nonatomic) IBOutlet UIView *searchBar;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIButton *buttonCancleSearch;

@property (nonatomic, strong) NSLayoutConstraint *searchBarTopSpaceConstraint;
@property (nonatomic, strong) NSLayoutConstraint *tableViewTopSpaceConstraint;
@property (nonatomic, assign) NSInteger currentQuestionID;
@property (nonatomic, copy) NSString *currentQuestion;
@property (nonatomic, assign) BOOL isSearching;

@property (nonatomic, strong) NSMutableArray *hotQuestions;
@property (nonatomic, strong) NSMutableArray *answers;
//- (void)hideHomeImage:(BOOL)hide withsearchBarToTop:(CGFloat)barTop duration:(CGFloat)duration;

@end

@implementation ServiceSearchViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self setupSubViewsConstraints];
    self.hotQuestions = [[NSMutableArray alloc] init];
    self.answers = [[NSMutableArray alloc] init];
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    
    [SCPushDispatcher sharedInstance].answerPushDelegate = self;
    
    // for test
    [self.hotQuestions addObject:@"硅谷比较好的学区在哪儿？"];
    [self.hotQuestions addObject:@"女儿去美国读高中，怎么样才能找到合适的"];
    
    self.isSearching = false;
}

- (void)setupSubViewsConstraints
{
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
    self.tableViewTopSpaceConstraint = [NSLayoutConstraint constraintWithItem:_tableView
                                                                    attribute:NSLayoutAttributeTop
                                                                    relatedBy:NSLayoutRelationEqual
                                                                       toItem:self.view
                                                                    attribute:NSLayoutAttributeTop
                                                                   multiplier:1.0f
                                                                     constant:200];
    
    [self.view addConstraints:@[self.searchBarTopSpaceConstraint, self.tableViewTopSpaceConstraint]];

}

#pragma mark - New Question Process
- (void)setIsSearching:(BOOL)isSearching
{
    if(_isSearching != isSearching) {
        _isSearching = isSearching;
        [self.tableView reloadData];
    }
    self.buttonCancleSearch.hidden = !_isSearching;
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
                     [self questionErrorWithErrorCode:errorCode];
                     return;
                 }
                 [self questionSuccessWithResponse:response];
             }
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
             DebugLog(@"Error: %@", error);
         }];
}


- (void)questionErrorWithErrorCode:(NSInteger)errorCode
{
    DebugLog(@"question error code: %ld", errorCode);
}

- (void)questionSuccessWithResponse:(NSDictionary *)response
{
    NSNumber *questionid = response[SKYWORLD_QUESTION_ID];
    self.currentQuestionID = [questionid integerValue];
    self.isSearching = true;
    
    NSDictionary *questionInfo = @{SEND_QUESTION_QUESTION:self.currentQuestion,
                                   SEND_QUESTION_QUESTION_ID:[NSString stringWithFormat:@"%ld", self.currentQuestionID]};
    
    NSManagedObjectContext *privateContext = [[SCCoreDataManager sharedInstance] privateObjectContext];
    [privateContext performBlockAndWait:^{
        [SendQuestion sendQuestionWithInfo:questionInfo inManagedObjectContext:privateContext];
    }];
}

#pragma mark - Cancle Question Process
- (IBAction)cancleSearch:(UIButton *)sender
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
//-(NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
//{
//    return 1;
//}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(self.isSearching){
        return self.answers.count;
    }else{
        return self.hotQuestions.count;
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    if(self.isSearching){
        static NSString *QuestionAnswerCellIdentifier = @"QuestionAnswerTableCell";
        cell = (SCTableViewCell *)[tableView dequeueReusableCellWithIdentifier:QuestionAnswerCellIdentifier];
        if (cell == nil) {
            cell = [[SCTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:QuestionAnswerCellIdentifier];
        }
        
        ((SCTableViewCell*)cell).model = self.answers[indexPath.row];
    }else{
        static NSString *HotQuestionCellIdentifier=@"HotQuestionTableCell";
        cell = [tableView dequeueReusableCellWithIdentifier:HotQuestionCellIdentifier];
        if (cell == nil) {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:HotQuestionCellIdentifier];
        }
        cell.textLabel.text = self.hotQuestions[indexPath.row];
    }
    return cell;
}

-(NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    if(self.isSearching){
        return nil;
    }else{
        return @"热门搜索";
    }
}

-(NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
    return @"更多答案即将到来...";
}

-(CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if(self.isSearching){
        return 0;
    }else{
        return 40;
    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

-(CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 40;
}

- (void)tableView:(UITableView *)tableView willDisplayFooterView:(UIView *)view forSection:(NSInteger)section
{
    view.tintColor = [UIColor clearColor];
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    DebugLog(@"table selected");
    if(self.isSearching){
        AnswerDetailViewController *answerDetailController = [self.storyboard instantiateViewControllerWithIdentifier:@"AnswerDetailView"];
        answerDetailController.question = self.currentQuestion;
        answerDetailController.answer = self.answers[indexPath.row];
        [self.navigationController pushViewController:answerDetailController animated:YES];
    }
}

#pragma mark - SCAnswerPushDelegate
- (void)didReceiveNewAnswer:(ReceivedAnswer *)answer
{
    if([answer.question_id isEqualToString:[NSString stringWithFormat:@"%ld", self.currentQuestionID]]){
        [self.answers addObject:answer];
        [self.tableView reloadData];
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
                         [self.tableViewTopSpaceConstraint setConstant:tableTop];
                         [self.view layoutIfNeeded];
                     }];
}

- (IBAction)backgroundTap:(id)sender
{
    [self.searchTextField resignFirstResponder];
    [self hideHomeImage:NO withsearchBarToTop:130 tableToTop:200 duration:0.4f];
}


@end
