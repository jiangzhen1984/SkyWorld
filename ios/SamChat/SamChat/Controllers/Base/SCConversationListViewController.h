//
//  SCConversationListViewController.h
//  SamChat
//
//  Created by HJ on 4/20/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "EaseConversationListViewController.h"

@interface SCConversationListViewController : EaseConversationListViewController

@property (strong, nonatomic) NSMutableArray *conversationsArray;

- (void)refresh;
- (void)refreshDataSource;

- (void)isConnect:(BOOL)isConnect;
- (void)networkChanged:(EMConnectionState)connectionState;

@end
