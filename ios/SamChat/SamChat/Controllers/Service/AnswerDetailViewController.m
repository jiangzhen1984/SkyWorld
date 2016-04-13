//
//  AnswerDetailViewController.m
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "AnswerDetailViewController.h"
#import "ChatViewController.h"

@interface AnswerDetailViewController ()

@property (weak, nonatomic) IBOutlet UILabel *labelQuestion;
@property (weak, nonatomic) IBOutlet UILabel *labelAnswer;

@end

@implementation AnswerDetailViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.labelQuestion.text = self.question;
    self.labelAnswer.text = self.answer.answer;
}

- (IBAction)chatNow:(UIButton *)sender
{
    NSString *nickname = [[SCUserProfileManager sharedInstance] getNickNameWithUsername:self.answer.fromWho.username];
    ChatViewController *chatController = [[ChatViewController alloc] initWithConversationChatter:nickname conversationType:EMConversationTypeChat];
    chatController.title = nickname;
    [self.navigationController pushViewController:chatController animated:YES];
}

- (IBAction)addAsFriend:(UIButton *)sender
{
}

- (IBAction)follow:(UIButton *)sender
{
    NSInteger userId = [self.answer.fromWho.unique_id integerValue];
    [[SamChatClient sharedInstance] makeFollow:YES withUser:userId
                                    completion:^(BOOL success, SCSkyWorldError *error) {
                                        if(success){
                                            [self showHint:@"关注成功"];
                                        }else{
                                            [self showHint:@"关注失败"];
                                        }
                                    }];
}

@end
