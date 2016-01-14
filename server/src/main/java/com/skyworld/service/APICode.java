package com.skyworld.service;

public class APICode {
	
	
	public static final int SUCCESS = 0;
	
	
	public static final int REQUEST_PARAMETER_INVALID = -1;
	
	public static final int ACTION_NOT_SUPPORT = -2;
	
	public static final int REQUEST_PARAMETER_NOT_STISFIED = -3;
	
	public static final int TOKEN_FORMAT_ERROR = -4;
	
	public static final int TOKEN_INVALID = -5;
	

	
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
}
