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

@interface ServiceSearchViewController ()
@property (weak, nonatomic) IBOutlet UITextField *searchTextField;
@property (weak, nonatomic) IBOutlet UIImageView *homeImage;
@property (weak, nonatomic) IBOutlet UIView *searchBar;
@property (weak, nonatomic) IBOutlet UITableView *hotQuestionTable;

@property (nonatomic, strong) NSLayoutConstraint *searchBarTopSpaceConstraint;
@property (nonatomic, strong) NSLayoutConstraint *tableViewTopSpaceConstraint;

//- (void)hideHomeImage:(BOOL)hide withsearchBarToTop:(CGFloat)barTop duration:(CGFloat)duration;

@end

@implementation ServiceSearchViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self setupSubViewsConstraints];
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
    self.tableViewTopSpaceConstraint = [NSLayoutConstraint constraintWithItem:_hotQuestionTable
                                                                    attribute:NSLayoutAttributeTop
                                                                    relatedBy:NSLayoutRelationEqual
                                                                       toItem:self.view
                                                                    attribute:NSLayoutAttributeTop
                                                                   multiplier:1.0f
                                                                     constant:200];
    
    [self.view addConstraints:@[self.searchBarTopSpaceConstraint, self.tableViewTopSpaceConstraint]];

}

- (NSString *)generateNewQuestionUrlString
{
    NSDictionary *header = @{SKYWORLD_CATEGORY: SKYWORLD_QUESTION};
    //NSDictionary *body = @{SKYWORLD_ANSWER};
    return nil;
}

- (IBAction)pushNewQuestion:(id)sender
{
    DebugLog(@"search");
    
    if((!self.searchTextField.text) || (self.searchTextField.text.length <= 0)){
        return;
    }
    NSString *urlString = [SCSkyWorldAPI urlNewQuestionWithQuestion:self.searchTextField.text];
    DebugLog(@"pushing new question with: %@", urlString);
    
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

}

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
