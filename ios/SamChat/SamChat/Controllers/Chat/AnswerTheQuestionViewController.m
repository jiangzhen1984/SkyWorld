//
//  AnswerTheQuestionViewController.m
//  SamChat
//
//  Created by HJ on 4/5/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "AnswerTheQuestionViewController.h"
#import "SCChatToolBar.h"
#import "SendAnswer.h"
#import "SCChatMessageCell.h"
#import "ContactUser.h"

@interface AnswerTheQuestionViewController () <SCChatToolbarDelegate, UITableViewDataSource, UITableViewDelegate>
{
    CGFloat offsetToTop;
}

@property (nonatomic, strong) SCChatToolBar *chatToolBar;
@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong) NSMutableArray *answers;

@end

@implementation AnswerTheQuestionViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self loadAnswers];
    [self setupSubViews];
}

- (void)setupSubViews
{
    offsetToTop = self.navigationController.navigationBar.frame.size.height + [UIApplication sharedApplication].statusBarFrame.size.height;
    CGFloat chatbarHeight = [SCChatToolBar defaultHeight];
    self.chatToolBar = [[SCChatToolBar alloc] initWithFrame:CGRectMake(0, self.view.frame.size.height - chatbarHeight, self.view.frame.size.width, chatbarHeight)];
    self.chatToolBar.autoresizingMask = UIViewAutoresizingFlexibleTopMargin;
    [self.view addSubview:self.chatToolBar];
    self.chatToolBar.delegate = self;
    
    self.view.backgroundColor = [UIColor lightGrayColor];
    
    self.tableView = [[UITableView alloc] initWithFrame:CGRectMake(10, offsetToTop, self.view.frame.size.width-20, self.view.frame.size.height - chatbarHeight - offsetToTop - 5)];
    self.tableView.backgroundColor = [UIColor grayColor];
    self.tableView.autoresizingMask = UIViewAutoresizingFlexibleTopMargin;
    [self.view addSubview:self.tableView];
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
}

- (void)loadAnswers
{
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    NSArray *matches = [SendAnswer loadCurrentUsersAnswersOfQuestionID:self.receivedQuestion.question_id
                                                inManagedObjectContext:mainContext];
    self.answers = [NSMutableArray arrayWithArray:matches];
}

- (void)scrollTableViewToBottom:(BOOL)animated
{
    if (self.tableView.contentSize.height > self.tableView.frame.size.height)
    {
        CGPoint offset = CGPointMake(0, self.tableView.contentSize.height - self.tableView.frame.size.height);
        [self.tableView setContentOffset:offset animated:animated];
    }
}

#pragma mark - SCChatToolbarDelegate
- (void)chatToolbarDidChangeFrameToHeight:(CGFloat)toHeight;
{
    CGRect rect = self.tableView.frame;
    rect.origin.y = 0;
    rect.size.height = self.view.frame.size.height - toHeight - 5;
    self.tableView.frame = rect;
    [self scrollTableViewToBottom:NO];
}

- (void)didSendText:(NSString *)text
{
    DebugLog(@"token: %@", [SCUserProfileManager sharedInstance].token);
    if(text.length <= 0){
        return;
    }

    NSDictionary *answerInfo = @{SEND_ANSWER_QUESTION_ID:self.receivedQuestion.question_id,
                                 SEND_ANSWER_ANSWER:text,
                                 SEND_ANSWER_STATUS:SEND_ANSWER_SENDING,
                                 SEND_ANSWER_SENDTIME:[SCUtils currentTimeStamp]};
    NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
    SendAnswer *sendAnswer = [SendAnswer sendAnswerWithInfo:answerInfo inManagedObjectContext:mainContext];
    [self.answers addObject:sendAnswer];
    [self.tableView reloadData];
    
    NSInteger currentIndex = [self.answers count] - 1;
    
    NSInteger questionId = [self.receivedQuestion.question_id integerValue];
    [[SamChatClient sharedInstance] sendAnswer:text toQuestionID:questionId completion:^(BOOL success, SCSkyWorldError *error) {
        if(success){
            [self updateAnswerOfIndex:currentIndex withStatus:SEND_ANSWER_SENDSUCCEED];
        }else{
            [self updateAnswerOfIndex:currentIndex withStatus:SEND_ANSWER_SENDFAILED];
        }
        [self.tableView reloadData];
    }];

}

- (void)updateAnswerOfIndex:(NSInteger)index withStatus:(NSNumber *)status
{
    if((index >= 0) && (index < [self.answers count])){
        NSManagedObjectContext *mainContext = [SCCoreDataManager sharedInstance].mainObjectContext;
        [self.answers[index] updateStatus:status
                   inManagedObjectContext:mainContext];
    }
    [self.tableView reloadData];
}

#pragma mark - Table Data Source & Delegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1 + [self.answers count];
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *SendAnswerCellIdentifier = @"SendAnswerTableCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:SendAnswerCellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:SendAnswerCellIdentifier];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
    }else{
        for (UIView *cellView in cell.subviews){
            [cellView removeFromSuperview];
        }
    }
    
    UIImageView *avatar;
    if(indexPath.row == 0){
        avatar = [[UIImageView alloc] initWithFrame:CGRectMake(0, 10, 50, 50)];

        [avatar sd_setImageWithURL:[NSURL URLWithString:self.receivedQuestion.fromWho.imagefile]
                  placeholderImage:[UIImage imageNamed:SC_CHAT_RECEIVER_DEFAULT_AVATAR]];
        [cell addSubview:avatar];
        [cell addSubview:[self bubbleView:self.receivedQuestion.question isLeft:YES withPosition:55]];
    }else{
        avatar = [[UIImageView alloc] initWithFrame:CGRectMake(self.view.frame.size.width-70, 10, 50, 50)];
        [avatar sd_setImageWithURL:[NSURL URLWithString:[SCUserProfileManager sharedInstance].currentLoginUserInformation.imagefile]
                  placeholderImage:[UIImage imageNamed:SC_CHAT_SENDER_DEFAULT_AVATAR]];
        [cell addSubview:avatar];
        [cell addSubview:[self bubbleView:((SendAnswer *)self.answers[indexPath.row-1]).answer isLeft:NO withPosition:65]];
    }
    return cell;
//    id<ISCChatMessageModel> model = nil;
//    if(indexPath.row == 0){
//        model = self.receivedQuestion;
//    }else{
//        model = self.answers[indexPath.row -1];
//    }
//    SCChatMessageCell *cell = [tableView dequeueReusableCellWithIdentifier:[SCChatMessageCell cellIdentifierWithModel:model]];
//    if(cell == nil){
//        cell = [[SCChatMessageCell alloc] initWithStyle:UITableViewCellStyleDefault
//                                        reuseIdentifier:[SCChatMessageCell cellIdentifierWithModel:model]
//                                                  model:model];
//    }
//    cell.model = model;
//    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *text = nil;
    if(indexPath.row == 0){
        text = self.receivedQuestion.question;
    }else{
        text = ((SendAnswer *)self.answers[indexPath.row-1]).answer;
    }
    CGSize size = [self CalcSizeOfMessage:text];
    return size.height+44;
//    id<ISCChatMessageModel> model = nil;
//    if(indexPath.row == 0){
//        model = self.receivedQuestion;
//    }else{
//        model = self.answers[indexPath.row -1];
//    }
//    return [SCChatMessageCell cellHeightWithModel:model];
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
//    DebugLog(@"table selected");
//    if(self.isSearching){
//        AnswerDetailViewController *answerDetailController = [self.storyboard instantiateViewControllerWithIdentifier:@"AnswerDetailView"];
//        answerDetailController.question = self.currentQuestion;
//        answerDetailController.answer = self.answers[indexPath.row];
//        [self.navigationController pushViewController:answerDetailController animated:YES];
//    }
}

#pragma mark - UIView
- (CGSize)CalcSizeOfMessage:(NSString *)message
{
    CGFloat margin = 140;
    CGSize maxSize = CGSizeMake(self.view.frame.size.width-margin, CGFLOAT_MAX);
    NSStringDrawingOptions opts = NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading;
    UIFont *font = [UIFont systemFontOfSize:14];
    NSMutableParagraphStyle *style = [[NSMutableParagraphStyle alloc] init];
    [style setLineBreakMode:NSLineBreakByCharWrapping];
    
    NSDictionary *attributes = @{NSFontAttributeName:font, NSParagraphStyleAttributeName:style};
    
    CGRect rect = [message boundingRectWithSize:maxSize
                                     options:opts
                                  attributes:attributes
                                     context:nil];
    return rect.size;
}

- (UIView *)bubbleView:(NSString *)text isLeft:(BOOL)isLeft withPosition:(int)position
{
    UIFont *font = [UIFont systemFontOfSize:14];
    CGSize size = [self CalcSizeOfMessage:text];
    
    // build single chat bubble cell with given text
    UIView *returnView = [[UIView alloc] initWithFrame:CGRectZero];
    returnView.backgroundColor = [UIColor clearColor];
    
    UIImage *bubble;
    bubble = [bubble stretchableImageWithLeftCapWidth:bubble.size.width/2 topCapHeight:35];
    if(isLeft){
        bubble = [[UIImage imageNamed:@"ChatReceiverBg"] stretchableImageWithLeftCapWidth:35 topCapHeight:35];
    }else{
        bubble = [[UIImage imageNamed:@"ChatSenderBg"] stretchableImageWithLeftCapWidth:5 topCapHeight:35];
    }

    UIImageView *bubbleImageView = [[UIImageView alloc] initWithImage:bubble];
    
    UILabel *bubbleText = [[UILabel alloc] initWithFrame:CGRectMake(isLeft?22.0f:15.0f, 20.0f, size.width+10, size.height+10)];
    bubbleText.backgroundColor = [UIColor clearColor];
    bubbleText.font = font;
    bubbleText.numberOfLines = 0;
    bubbleText.lineBreakMode = NSLineBreakByWordWrapping;
    bubbleText.text = text;
    
    bubbleImageView.frame = CGRectMake(0.0f, 14.0f, bubbleText.frame.size.width+30.0f, bubbleText.frame.size.height+20.0f);
    
    if(isLeft){
        returnView.frame = CGRectMake(position, 0.0f, bubbleText.frame.size.width+30.0f, bubbleText.frame.size.height+30.0f);
    }
    else{
        returnView.frame = CGRectMake(self.view.frame.size.width-position-(bubbleText.frame.size.width+40.0f), 0.0f, bubbleText.frame.size.width+30.0f, bubbleText.frame.size.height+30.0f);
    }
    
    [returnView addSubview:bubbleImageView];
    [returnView addSubview:bubbleText];
    
    return returnView;
}


@end
