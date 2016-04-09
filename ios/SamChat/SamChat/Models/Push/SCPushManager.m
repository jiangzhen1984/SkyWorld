//
//  SCPushManager.m
//  SamChat
//
//  Created by HJ on 4/9/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SCPushManager.h"

@interface SCPushManager ()
{
    dispatch_queue_t _pushQueue;
}

@property (nonatomic, assign) BOOL pulling;

@end

@implementation SCPushManager
- (instancetype)init
{
    self = [super init];
    if(self){
        _pushQueue = dispatch_queue_create("SCPushDispatcher", NULL);
        //self.pulling = false;
    }
    return self;
}

- (void)setPulling:(BOOL)pulling
{
    if(pulling == false){
        [self pullFromServerOnce];
    }
    _pulling = pulling;
}

- (void)asyncWaitingPush
{
    DebugLog(@"main Thread: %@", [NSThread currentThread]);
    self.pulling = false;
    //    [NSTimer scheduledTimerWithTimeInterval:2
    //                                     target:self
    //                                   selector:@selector(pullFromServerOnce)
    //                                   userInfo:nil
    //                                    repeats:YES];
    
    
    //    __weak typeof(self) weakSelf = self;
    //    dispatch_async(dispatch_get_main_queue(), ^{
    //        while (true) {
    //            [weakSelf pullFromServerOnce];
    //            //[NSThread sleepForTimeInterval:20];
    //            //DebugLog(@"########################################");
    //            break;
    //        }
    
    ////        dispatch_async(dispatch_get_main_queue(), ^{
    ////            [weakSelf.dataArray addObjectsFromArray:messages];
    ////            [weakSelf.tableView reloadData];
    ////            [weakSelf.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:[weakSelf.dataArray count] - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    //        });
    //    });
    
}

- (void)pullFromServerOnce
{
    if(self.pulling) return;
    self.pulling = true;
    NSURL *url = [NSURL URLWithString:SKYWORLD_API_PUSH];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    [request setValue:[SCUserProfileManager sharedInstance].token forHTTPHeaderField:@"Authorization"];
    NSURLConnection *conn = [NSURLConnection connectionWithRequest:request delegate:self];
    [conn start];
    //    NSData *data=[NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
    //    DebugLog(@"-------------data:%@", data);
    //    NSDictionary *content = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];//转换数据格式
    //    DebugLog(@"-------------content:%@", content);
    DebugLog(@"pull Thread: %@", [NSThread currentThread]);
}


#pragma mark- NSURLConnection Data Delegate
-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    NSLog(@"接收到服务器的响应 %@:%@", [NSThread currentThread], response);
}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // 数据大时候可能多次调用
    NSLog(@"接收到服务器的数据 %@:%@",[NSThread currentThread], data);
    if(data) {
        NSDictionary *content = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
        [self.delegate didReceivePushData:content];
    }
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    NSLog(@"服务器的数据加载完毕%@", [NSThread currentThread]);
    self.pulling = false;
}

-(void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"请求错误 %@:%@", [NSThread currentThread],error);
    self.pulling = false;
}

@end
