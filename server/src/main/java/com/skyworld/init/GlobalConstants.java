package com.skyworld.init;

public class GlobalConstants {

	public static int MAX_AVATAR_SIZE = 2 * 1024 * 1024;
	
	public static String HOME_HTTPS = "httpS";
	
	public static String HOME_HTTP = "http";
	
	public static String HOME_HOST = "121.42.207.185";
	
	public static String AVATAR_CONTEXT = "avatar/";
	
	public static String AVATAR_HOST = HOME_HTTP +":/"+ HOME_HOST + "/" + AVATAR_CONTEXT;
	
	public static String ARTICLE_CONTEXT = "article/";
	
	public static String ARTICLE_HOST = HOME_HTTP +":/"+ HOME_HOST + "/" + ARTICLE_CONTEXT;
	
	
	public static String SKSERVICER_CONTEXT = "skservicer/";
	
	public static String SKSERVICER_HOST = HOME_HTTP +":/"+ HOME_HOST + "/" + SKSERVICER_CONTEXT;
	
}

