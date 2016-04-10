//
//  SCCoreDataMacro.h
//  SamChat
//
//  Created by HJ on 4/4/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#ifndef SCCoreDataMacro_h
#define SCCoreDataMacro_h

#define ENTITY_CONTACT_USER             @"ContactUser"
#define CONTACT_USER_UNIQUE_ID          @"unique_id"
#define CONTACT_USER_USERNAME           @"username"

#define ENTITY_LOGIN_USER_INFORMATION           @"LoginUserInformation"
#define LOGIN_USER_INFORMATION_USERNAME         @"username"
#define LOGIN_USER_INFORMATION_EASEMOB_STATUS   @"easemob_status"

#define LOGIN_USER_TYPE_NORMAL              @0
#define LOGIN_USER_TYPE_SAMVENDOR           @1

#define ENTITY_SEND_QUESTION                @"SendQuestion"
#define SEND_QUESTION_QUESTION_ID           @"question_id"
#define SEND_QUESTION_SENDUSERID            @"senduserid"
#define SEND_QUESTION_QUESTION              @"question"
#define SEND_QUESTION_STATUS                @"status"
#define SEND_QUESTION_SENDTIME              @"sendtime"
#define SEND_QUESTION_CANCELTIME            @"canceltime"
#define SEND_QUESTION_SENDERCELLPHONE       @"sendercellphone"
#define SEND_QUESTION_SENDERUSERNAME        @"senderusername"

#define ENTITY_RECEIVED_ANSWER          @"ReceivedAnswer"

#define ENTITY_RECEIVED_QUESTION            @"ReceivedQuestion"
#define RECEIVED_QUESTION_QUESTION_ID       @"question_id"
#define RECEIVED_QUESTION_VALID             @1
#define RECEIVED_QUESTION_INVALID           @0
#define RECEIVED_QUESTION_RECEIVEDTIME      @"receivedtime"
#define RECEIVED_QUESTION_RECEIVERUSERNAME  @"receiverusername"

#define RECEIVED_QUESTION_RESPONSED         @1
#define RECEIVED_QUESTION_NOTRESPONSED      @0

#define ENTITY_SEND_ANSWER                  @"SendAnswer"

#define SEND_ANSWER_QUESTION_ID             @"question_id"
#define SEND_ANSWER_ANSWER                  @"answer"
#define SEND_ANSWER_STATUS                  @"status"
#define SEND_ANSWER_SENDTIME                @"sendtime"
#define SEND_ANSWER_WHOSEND_USERNAM         @"whoSend.username"

#define SEND_ANSWER_SENDING                 @0
#define SEND_ANSWER_SENDSUCCEED             @1
#define SEND_ANSWER_SENDFAILED              @2


#endif /* SCCoreDataMacro_h */
