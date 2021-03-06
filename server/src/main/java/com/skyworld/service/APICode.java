package com.skyworld.service;

public class APICode {
	
	
	public static final int SUCCESS = 0;
	
	
	public static final int REQUEST_PARAMETER_INVALID = -1;
	
	public static final int ACTION_NOT_SUPPORT = -2;
	
	public static final int REQUEST_PARAMETER_NOT_STISFIED = -3;
	
	public static final int TOKEN_FORMAT_ERROR = -4;
	
	public static final int TOKEN_INVALID = -5;
	
	public static final int HANDLER_STREAM_FAILED = -6;
	

	
	public static final int REGISTER_ERROR_USER_EXIST = -101;
	public static final int REGISTER_ERROR_USER_PWD_MISMATCH = -102;
	public static final int REGISTER_ERROR_INTERNAL = -103;

	
	
	public static final int LOGIN_ERROR_INCORRECT_USER_NAME_OR_PWD = -201;
	
	
	public static final int INQUIRE_ERROR_OPT_UNSUPPORTED = -301;
	public static final int INQUIRE_ERROR_INTERNAL_ERROR = -302;
	public static final int INQUIRE_ERROR_SUCH_QUESTION = -303;
	
	
	public static final int LOGOUT_ERROR_INVALID_TOKEN = -401;
	
	
	public static final int USER_UPGRADE_ERROR_INTERNAL = -501;
	
	public static final int USER_UPGRADE_ERROR_USER_TOKE = -502;
	public static final int USER_UPGRADE_ERROR_ALREADY = -503;
	
	public static final int ANSWER_ERROR_NO_SUCH_QUESTION = -601;
	public static final int ANSWER_ERROR_NOT_SERVICER = -602;
	
	
	public static final int QUERY_ERROR_OPT_NOT_SUPPORT = -701;
	
	
	public static final int UPDATE_AVATAR_ERROR = -801;
	public static final int UPDATE_AVATAR_TYPE_NOT_SUPPORT = -802;
	public static final int UPDATE_AVATAR_SIZE_EXCEED_MAX_LIMITION = -803;
	
	
	
	public static final int ARTICLE_ERROR_ARTICLE_NOT_EXIST = -901;
	
	
	public static final int FOLLOW_ERROR_USER_NOT_EXIST = -1001;
	public static final int FOLLOW_ERROR_UN_SUPPORT_FLAG = -1002;
	
	
	public static final int ARTICLE_QUERY_ERROR_TYPE_NOT_SUPPORT = -1101;
	
	
	public static final int SKSERVICER_QUERY_ERROR_TYPE_NOT_SUPPORT_OPT = -1201;
	public static final int SKSERVICER_QUERY_ERROR_TYPE_NOT_SERVICER = -1202;
	
	
	public static final int SKSERVICER_UPDATE_ERROR_NOT_SUPPORT_OPT = -1301;
	
	
	public static final int VVERSION_QUERY_ERROR_NOT_SUPPORT_OPT = -1401;
	
}


