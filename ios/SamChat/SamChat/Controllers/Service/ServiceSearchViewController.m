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
    /*
    AFHTTPSessionManager *manager = [AFHTTPSessionManager manager];
    [manager GET:[self generateNewQuestionUrlString]
      parameters:nil
        progress:^(NSProgress *downloadProgress){
        }
         success:^(NSURLSessionDataTask *task, id responseObject) {
         }
         failure:^(NSURLSessionDataTask *task, NSError *error){
         }];
    */
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
