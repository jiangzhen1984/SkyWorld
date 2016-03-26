package com.android.samservice;

public class HttpPushInfo{
	public static int QUESTION = 0;
	public static int ANSWER = 1;
	public static int EASEMOBINFO = 2;
	
	public int category;
	public long unique_id;
	public String cellphone;
	public String easemob_username;
	public String avatar;
	public String area;
	public String location;
	public String desc;

	public String quest_id;
	public String quest;
	public long datetime;
	public int opt;
	
	public String username;

	public String answer;

	public long lastupdate;

	
	

	public void HttpPushInfo(){
		category = 0;
		datetime = 0;
		quest_id = null;
		quest = null;
		opt = 0;
		
		unique_id =0;
		username = null;
		cellphone = null;

		easemob_username = null;
		avatar = null;

		area = null;
		location = null;
		desc = null;

		lastupdate = 0;
		
		answer = null;
		
	}

};

