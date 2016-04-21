//
//  SamChatHelper.h
//  SamChat
//
//  Created by HJ on 4/20/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ContactListViewController.h"
#import "HomeViewController.h"
#import "ChatViewController.h"
#import "SCNormalConversationViewController.h"

#if DEMO_CALL == 1

#import "CallViewController.h"
@interface SamChatHelper: NSObject <EMClientDelegate,EMChatManagerDelegate,EMContactManagerDelegate,EMGroupManagerDelegate,EMChatroomManagerDelegate,EMCallManagerDelegate,SCPushDelegate>
#else
@interface SamChatHelper : NSObject <EMClientDelegate,EMChatManagerDelegate,EMContactManagerDelegate,EMGroupManagerDelegate,EMChatroomManagerDelegate,SCPushDelegate>
#endif

@property (nonatomic, weak) ContactListViewController *contactViewVC;
@property (nonatomic, weak) SCNormalConversationViewController *conversationListVC;
@property (nonatomic, weak) HomeViewController *mainVC;
@property (nonatomic, weak) ChatViewController *chatVC;

#if DEMO_CALL == 1
@property (strong, nonatomic) EMCallSession *callSession;
@property (strong, nonatomic) CallViewController *callController;
#endif

+ (instancetype)shareHelper;
- (void)asyncPushOptions;
- (void)asyncGroupFromServer;
- (void)asyncConversationFromDB;
- (void)asyncPush;
#if DEMO_CALL == 1
- (void)makeCallWithUsername:(NSString *)aUsername
                     isVideo:(BOOL)aIsVideo;
- (void)hangupCallWithReason:(EMCallEndReason)aReason;
- (void)answerCall;
#endif

@end
