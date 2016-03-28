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

@end

@implementation ServiceSearchViewController


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

@end
