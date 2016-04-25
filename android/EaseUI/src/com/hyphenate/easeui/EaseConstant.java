/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui;

public class EaseConstant {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    
    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
//    /**
//     * app自带的动态表情，直接去resource里获取图片
//     */
//    public static final String MESSAGE_ATTR_BIG_EXPRESSION_ICON = "em_big_expression_icon";
//    /**
//     * 动态下载的表情图片，需要知道表情url
//     */
//    public static final String MESSAGE_ATTR_BIG_EXPRESSION_URL = "em_big_expression_url";
    
    
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
    
	public static final String EXTRA_CHAT_TYPE = "chatType";
	public static final String EXTRA_USER_ID = "userId";

	/*SAMC_BEGIN(chat activity type customized)*/
	public static final String CHAT_ACTIVITY_TYPE="chat_activity_type";
	public static final String CHAT_ACTIVITY_TYPE_VIEW_SERVICE="service";
	public static final String CHAT_ACTIVITY_TYPE_VIEW_CHAT="chat";
	public static final String CHAT_ACTIVITY_TYPE_VIEW_VENDOR="vendor";
	/*SAMC_END(chat activity type customized)*/

	/*SAMC_BEGIN(conversation should be shown in which fragment)*/
	public static final String CONVERSATION_ATTR_VIEW_SERVICE="s";
	public static final String CONVERSATION_ATTR_VIEW_CHAT="c";
	public static final String CONVERSATION_ATTR_VIEW_VENDOR="v";
	/*SAMC_END(conversation should be shown in which fragment)*/

	/*SAMC_BEGIN(which fragment the conversation list in)*/
	public static final int CONVERSATION_LIST_IN_SERIVCE=0;
	public static final int CONVERSATION_LIST_IN_CHAT=1;
	public static final int CONVERSATION_LIST_IN_VENDOR=2;
	/*SAMC_END(which fragment the conversation list in)*/

	/*SAMC_BEGIN(vendor response should be send back with question id)*/
	public static final String CHAT_QUESTIONS_BACK="questions";
	/*SAMC_END(conversation should be shown in which fragment)*/

	/*SAMC_BEGIN(broadcast for vendor chat fragment when received new quest)*/
	public static final String ACTION_NEW_QUEST_FOR_VENDOR_CHAT_FRAG="new_quest_for_vendor_chat_frag";
	public static final String QUESTION_OWNER="question_owner";
	/*SAMC_END(broadcast for vendor chat fragment when received new quest)*/
	
}
