//
//  SAMCSessionListViewController.h
//  NIMKit
//
//  Created by HJ on 5/6/16.
//  Copyright © 2016 NetEase. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NIMCellConfig.h"
#import "NIMSDK.h"

@interface SAMCSessionListViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,NIMLoginManagerDelegate>

@property (nonatomic,strong)   UITableView *tableView;

@property (nonatomic,readonly) NSMutableArray * recentSessions;

@property (nonatomic,assign)   BOOL autoRemoveRemoteSession;

//SAMC_BEGIN
@property (strong, nonatomic) NSNumber *currentListMessageFromView;

- (BOOL)shouldIncludeRecentSession:(NIMRecentSession *)recentSession;
//SAMC_END

- (void)onSelectedRecent:(NIMRecentSession *)recent
             atIndexPath:(NSIndexPath *)indexPath;

- (void)onSelectedAvatar:(NIMRecentSession *)recent
             atIndexPath:(NSIndexPath *)indexPath;

- (void)onDeleteRecentAtIndexPath:(NIMRecentSession *)recent
                      atIndexPath:(NSIndexPath *)indexPath;

- (NSString *)nameForRecentSession:(NIMRecentSession *)recent;

- (NSString *)contentForRecentSession:(NIMRecentSession *)recent;

- (NSString *)timestampDescriptionForRecentSession:(NIMRecentSession *)recent;

- (void)reload;


@end
