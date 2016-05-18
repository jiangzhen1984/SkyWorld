//
//  SAMCAppDelegate.m
//  SamChat
//
//  Created by HJ on 5/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#import "SAMCAppDelegate.h"
#import "NTESLoginViewController.h"
#import "NIMSDK.h"
#import "UIView+Toast.h"
#import "NTESService.h"
#import "NTESNotificationCenter.h"
#import "NTESLogManager.h"
#import "NTESDemoConfig.h"
#import "NTESSessionUtil.h"
#import "SAMCMainViewController.h"
#import "NTESCustomAttachmentDecoder.h"
#import "NTESClientUtil.h"
#import "NTESNotificationCenter.h"
#import "NIMKit.h"
#import "NTESDataManager.h"
#import "SAMCUserProfileManager.h"
#import "SamChatClient.h"

NSString *NTESNotificationLogout = @"NTESNotificationLogout";
@interface SAMCAppDelegate ()<SAMCLoginManagerDelegate>
@end

@implementation SAMCAppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    
    //配置 SDK 配置，需要在 SDK 启动之前进行配置 (如文件存储根目录等)
    //NSString *sdkPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
    //[[NIMSDKConfig sharedConfig] setupSDKDir:sdkPath];
        
    //appkey是应用的标识，不同应用之间的数据（用户、消息、群组等）是完全隔离的。
    //如需打网易云信Demo包，请勿修改appkey，开发自己的应用时，请替换为自己的appkey.
    //并请对应更换Demo代码中的获取好友列表、个人信息等网易云信SDK未提供的接口。
    NSString *appKey = [[NTESDemoConfig sharedConfig] appKey];
    NSString *cerName= [[NTESDemoConfig sharedConfig] cerName];
    
    [[NIMSDK sharedSDK] registerWithAppID:appKey
                                  cerName:cerName];

    [NIMCustomObject registerCustomDecoder:[NTESCustomAttachmentDecoder new]];
    

    [self setupServices];
    [self registerAPNs];
    
    [self commonInitListenEvents];
    
    [[NIMKit sharedKit] setProvider:[NTESDataManager sharedInstance]];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor grayColor];
    [self.window makeKeyAndVisible];
    [application setStatusBarStyle:UIStatusBarStyleLightContent];

    [self setupMainViewController];
    
    DDLogDebug(@"Dir:%@",NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES)[0]);
    return YES;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[SamChatClient sharedClient].accountManager removeDelegate:self];
}


#pragma mark - ApplicationDelegate
- (void)applicationWillResignActive:(UIApplication *)application {
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    NSInteger count = [[[NIMSDK sharedSDK] conversationManager] allUnreadCount];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:count];
    
    if ([SamChatClient sharedClient].settingManager.findNewVersion) {
        dispatch_async(dispatch_get_main_queue(), ^{
            UILocalNotification *localNotification = [[UILocalNotification alloc] init];
            if (localNotification) {
                localNotification.fireDate = [[[NSDate alloc] init] dateByAddingTimeInterval:2];
                localNotification.timeZone = [NSTimeZone defaultTimeZone];
                localNotification.alertBody = @"天际客户端有新的版本，点击到 App Store 升级。";
                localNotification.alertAction = @"升级";
                localNotification.soundName = @"";
                [application scheduleLocalNotification:localNotification];
            }
        });
    }
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    double delayInSeconds = 5.0;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [[SamChatClient sharedClient].settingManager checkVersionCompletion:^(BOOL findNew, NSString *versionInfo) {
            DDLogDebug(@"check version complete: %d", findNew);
        }];
    });
}

- (void)applicationWillTerminate:(UIApplication *)application {
}

- (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    [[NIMSDK sharedSDK] updateApnsToken:deviceToken];   
}

- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    DDLogError(@"fail to get apns token :%@",error);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo{
    DDLogInfo(@"receive remote notification:  %@", userInfo);
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    DDLogDebug(@"receive local notification:  %@", notification);
    NSString *appStoreId = @"375380948"; // TODO: replace it
    NSString *url = [NSString stringWithFormat:@"https://itunes.apple.com/app/id%@", appStoreId];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:url]];
}



#pragma mark - misc
- (void)registerAPNs
{
    if ([[UIApplication sharedApplication] respondsToSelector:@selector(registerForRemoteNotifications)])
    {
        UIUserNotificationType types = UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert;
        UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:types
                                                                                 categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:settings];
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }
    else
    {
        UIRemoteNotificationType types = UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeBadge;
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:types];
    }
}

- (void)setupMainViewController
{
    LoginData *data = [[SAMCUserProfileManager sharedManager] currentLoginData];
    NSString *account = [data account];
    NSString *token = [data token];
    
    //如果有缓存用户名密码推荐使用自动登录
    if ([account length] && [token length])
    {
        [[SamChatClient sharedClient].accountManager autoLogin:account token:token];
        [[NTESServiceManager sharedManager] start];
        SAMCMainViewController *mainTab = [[SAMCMainViewController alloc] initWithNibName:nil bundle:nil];
        self.window.rootViewController = mainTab;
        
        //TODO: 暂时调试用，换成通知处理登录情况，现在是有几个登录口分别对mainviewcontroller配置的
        [[SamChatClient sharedClient].pushManager asyncWaitingPush];
    }
    else
    {
        [self setupLoginViewController];
    }
}

- (void)commonInitListenEvents
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(logout:)
                                                 name:NTESNotificationLogout
                                               object:nil];
    
    [[SamChatClient sharedClient].accountManager addDelegate:self];
}

- (void)setupLoginViewController
{
    UIStoryboard *storyBoard = [UIStoryboard storyboardWithName:@"LoginCtrl" bundle:[NSBundle mainBundle]];
    UIViewController *loginController = [storyBoard instantiateViewControllerWithIdentifier:@"LoginView"];
    
//    NTESLoginViewController *loginController = [[NTESLoginViewController alloc] init];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:loginController];
    self.window.rootViewController = nav;
}


#pragma mark - 注销
-(void)logout:(NSNotification*)note
{
    [self doLogout];
}

- (void)doLogout
{
    [[SAMCUserProfileManager sharedManager] setCurrentLoginData:nil];
    [[NTESServiceManager sharedManager] destory];
    [self setupLoginViewController];
}


#pragma SAMCLoginManagerDelegate
-(void)onKick:(NIMKickReason)code clientType:(NIMLoginClientType)clientType
{
    NSString *reason = @"你被踢下线";
    switch (code) {
        case NIMKickReasonByClient:
        case NIMKickReasonByClientManually:{
            NSString *clientName = [NTESClientUtil clientName:clientType];
            reason = clientName.length ? [NSString stringWithFormat:@"你的帐号被%@端踢出下线，请注意帐号信息安全",clientName] : @"你的帐号被踢出下线，请注意帐号信息安全";
            break;
        }
        case NIMKickReasonByServer:
            reason = @"你被服务器踢下线";
            break;
        default:
            break;
    }
    [[[NIMSDK sharedSDK] loginManager] logout:^(NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:NTESNotificationLogout object:nil];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"下线通知" message:reason delegate:nil cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
        [alert show];
    }];
}

- (void)onAutoLoginFailed:(NSError *)error
{
    //只有连接发生严重错误才会走这个回调，在这个回调里应该登出，返回界面等待用户手动重新登录。
    DDLogInfo(@"onAutoLoginFailed %zd",error.code);
    NSString *toast = [NSString stringWithFormat:@"登录失败: %zd",error.code];
    [self.window makeToast:toast duration:2.0 position:CSToastPositionCenter];
    [[[NIMSDK sharedSDK] loginManager] logout:^(NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:NTESNotificationLogout object:nil];
    }];
}


#pragma mark - logic impl
- (void)setupServices
{
    [[NTESLogManager sharedManager] start];
    [[NTESNotificationCenter sharedCenter] start];
}


@end
