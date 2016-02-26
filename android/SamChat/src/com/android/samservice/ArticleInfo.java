package com.android.samservice;

import java.util.ArrayList;

import com.android.samservice.info.ContactUser;

public class ArticleInfo{
	public long timestamp;
	public long article_id;
	public ArrayList<String> recommands;
	public int status;
	public String comment;
	public ArrayList<String> comments;

	public ArrayList<ContactUser> recommander;
	public ArrayList<ContactUser> commenter;
	public ContactUser publisher;

	public ArrayList<String> pics;

	public ArticleInfo(){
		timestamp = 0;
		article_id = 0;
		recommands = new ArrayList<String>();
		status = 0;
		comment = null;
		comments = new ArrayList<String>();

		recommander = new ArrayList<ContactUser>();
		commenter = new ArrayList<ContactUser>();
		publisher = new ContactUser();

		pics = new ArrayList<String>();
		
	}

};


