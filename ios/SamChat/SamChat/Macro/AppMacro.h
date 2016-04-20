//
//  AppMacro.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#ifndef AppMacro_h
#define AppMacro_h

#pragma mark - Inline function
#define SC_RGB(r,g,b)               [UIColor colorWithRed:((r) / 255.0) green:((g) / 255.0) blue:((b) / 255.0) alpha:1.0]
#define SC_RGBAlpha(r, g, b, a)     [UIColor colorWithRed:((r) / 255.0) green:((g) / 255.0) blue:((b) / 255.0) alpha:(a)]

#define SC_DEBUG
#ifdef SC_DEBUG
#   define DebugLog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__)
#else
#   define DebugLog(...)
#endif


#define SC_MAIN_COLOR       SC_RGB(2, 168, 244)

#pragma mark - Login User Information

#define SC_CURRENT_LOGIN_USERNAME      @"currentloginusername"
#define SC_CURRENT_LOGIN_TOKEN         @"token"
#define SC_LATEST_ALERT_VERSION         @"latestalertversion"

#define SC_LOGINUSER_NO_LOGIN           0
#define SC_LOGINUSER_LOGIN              1


#define SC_MINIMUM_USERNAME_LENGTH      4
#define SC_MINIMUM_PASSWORD_LENGTH      6

#define SC_CHAT_RECEIVER_DEFAULT_AVATAR @"ReceivedAnswerDefaultAvatar"
#define SC_CHAT_SENDER_DEFAULT_AVATAR   @"ReceivedAnswerDefaultAvatar"


#define SC_ARTICLE_CELL_HIGHLIGHT_COLOR [UIColor colorWithRed:92/255.0 green:140/255.0 blue:193/255.0 alpha:1.0]

#define SC_CHATTAB_STORYBOARD   [UIStoryboard storyboardWithName:@"ChatTab" bundle:[NSBundle mainBundle]]

#define SC_SCREEN_WIHTH         [UIScreen mainScreen].bounds.size.width

#define MAX_ARTICLE_IMAGE_COUNT 9

// coversation type key
#define CONVERSATION_TYPE_KEY_QUESTION  @"QuestionConversation"
#define CONVERSATION_TYPE_KEY_ANSWER    @"AnswerConversation"
#define CONVERSATION_TYPE_KEY_NORMAL    @"NormalConversation"

#define SAMC_BEGIN
#define SAMC_END

#endif /* AppMacro_h */
