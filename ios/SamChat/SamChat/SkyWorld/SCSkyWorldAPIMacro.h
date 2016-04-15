//
//  SCSkyWorldAPIMacro.h
//  SamChat
//
//  Created by HJ on 3/24/16.
//  Copyright © 2016 SkyWorld. All rights reserved.
//

#ifndef SCSkyWorldAPIMacro_h
#define SCSkyWorldAPIMacro_h


//#define SKYWORLD_API_PREFIX             @"http://121.42.207.185/SkyWorld/api/1.0/"
//#define SKYWORLD_API_PUSH               @"http://121.42.207.185/SkyWorld/push"
#define SKYWORLD_API_HOTTOPICAPI        @"http://139.129.57.77/sw/api_1.0_HotTopicAPI.do"
#define SKYWORLD_API_PREFIX             @"http://139.129.57.77/sw/api/1.0/"
#define SKYWORLD_API_PUSH               @"http://139.129.57.77/sw/push"
#define SKYWORLD_API_ADDRESS            @"http://139.129.57.77/"

#define SKYWORLD_APITYPE_USERAPI        @"UserAPI"
#define SKYWORLD_APITYPE_QUESTIONAPI    @"QuestionAPI"
#define SKYWORLD_APITYPE_USERAVATARAPI  @"UserAvatarAPI"
#define SKYWORLD_APITYPE_ARTICLEAPI     @"ArticleApi"

#pragma mark - Json Key Define
#define SKYWORLD_HEADER         @"header"
#define SKYWORLD_BODY           @"body"
#define SKYWORLD_ACTION         @"action"
#define SKYWORLD_TOKEN          @"token"
#define SKYWORLD_USER           @"user"
#define SKYWORLD_CELLPHONE      @"cellphone"
#define SKYWORLD_USERNAME       @"username"

#define SKYWORLD_COUNTRY_CODE   @"country_code"
#define SKYWORLD_PWD            @"pwd"
#define SKYWORLD_CONFIRM_PWD    @"confirm_pwd"

#define SKYWORLD_RET            @"ret"
#define SKYWORLD_ID             @"id"
#define SKYWORLD_MAIL           @"mail"
#define SKYWORLD_TYPE           @"type"
#define SKYWORLD_AVATAR         @"avatar"
#define SKYWORLD_ORIGIN         @"origin"
#define SKYWORLD_AVATAR_ORIGIN  @"avatar.origin"
#define SKYWORLD_LASTUPDATE     @"lastupdate"

#define SKYWORLD_DESC           @"desc"
#define SKYWORLD_AREA           @"area"
#define SKYWORLD_LOCATION       @"location"

#define SKYWORLD_CATEGORY       @"category"

#define SKYWORLD_DATETIME       @"datetime"

#define SKYWORLD_OPT            @"opt"
#define SKYWORLD_QUESTION_ID    @"question_id"
#define SKYWORLD_QUEST_ID       @"quest_id"
#define SKYWORLD_QUEST          @"quest"
#define SKYWORLD_ASKER          @"asker"

#define SKYWORLD_ACT            @"act"

#define SKYWORLD_PARAM          @"param"
#define SKYWORLD_USERNAMES      @"usernames"

#define SKYWORLD_USER_ID        @"user_id"
#define SKYWORLD_FLAG           @"flag"
#define SKYWORLD_BOTH           @"both"

#define SKYWORLD_COMMENT        @"comment"

#define SKYWORLD_SYSERVICER     @"syservicer"

#define SKYWORLD_USERS          @"users"

#define SKYWORLD_ARTICLE_ID         @"article_id"
#define SKYWORLD_TIMESTAMP_START    @"timestamp_start"
#define SKYWORLD_TIMESTAMP_END      @"timestamp_end"
#define SKYWORLD_FETCH_COUNT        @"fetch_count"
#define SKYWORLD_QT                 @"qt"

#define SKYWORLD_OPT_TYPE           @"opt_type"
#define SKYWORLD_TOPIC_TYPE         @"topic_type"
#define SKYWORLD_CUR_COUNT          @"cur_count"
#define SKYWORLD_UPDATE_TIME_PRE    @"update_time_pre"

#define SKYWORLD_TOPICS             @"topics"
#define SKYWORLD_QUERY_TIME         @"query_time"

#define SKYWORLD_NAME               @"name"

#define SKYWORLD_ARTICLE            @"article"
#define SKYWORLD_ARTICLES           @"articles"
#define SKYWORLD_TIMESTAMP          @"timestamp"
#define SKYWORLD_STATUS             @"status"
#define SKYWORLD_COMMENT            @"comment"
#define SKYWORLD_PUBLISHER          @"publisher"
#define SKYWORLD_PICS               @"pics"
#define SKYWORLD_URL                @"url"
#define SKYWORLD_RECOMMENDS         @"recommends"
#define SKYWORLD_CONTENT            @"content"
#define SKYWORLD_COMMENTS           @"comments"

#pragma mark - Json Key Path
#define SKYWORLD_USER_USERNAME      @"user.username"
#define SKYWORLD_EASEMOB_USERNAME   @"easemob.username"
#define SKYWORLD_HEADER_CATEGORY    @"header.category"
#define SKYWORLD_QUEST_QUEST_ID     @"quest.quest_id"
#define SKYWORLD_ANS_ANSWER         @"ans.answer"
#define SKYWORLD_ASKER_CELLPHONE    @"asker.cellphone"
#define SKYWORLD_USER_AVATAR_ORIGIN @"user.avatar.origin"
#define SKYWORLD_ARTICLE_PICS       @"article.pics"
#define SKYWORLD_USER_CELLPHONE     @"user.cellphone"

#pragma mark - Json Value Define
#define SKYWORLD_REGISTER           @"register"
#define SKYWORLD_LOGIN              @"login"
#define SKYWORLD_LOGOUT             @"logout"
#define SKYWORLD_UPGRADE            @"upgrade"
#define SKYWORLD_QUERY              @"query"
#define SKYWORLD_RELATION           @"relation"
#define SKYWORLD_QUESTION           @"question"
#define SKYWORLD_ANSWER             @"answer"
#define SKYWORLD_FOLLOW             @"follow"
#define SKYWORLD_FEEDBACK           @"feedback"
#define SKYWORLD_UPDATE_AVATAR      @"update-avatar"
#define SKYWORLD_ARTICLE_PUBLISH    @"article-publish"
#define SKYWORLD_ARTICLE_RECOMMEND  @"article-recommend"
#define SKYWORLD_ARTICLE_COMMENT    @"article-comment"
#define SKYWORLD_ARTICLE_QUERY      @"article-query"
#define SKYWORLD_EASEMOB            @"easemob"
#define SKYWORLD_QUERY_TOPIC_LIST   @"query_topic_list"


#endif /* SCSkyWorldAPIMacro_h */
