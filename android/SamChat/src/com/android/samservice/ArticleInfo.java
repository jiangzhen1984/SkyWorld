package com.android.samservice;

import java.util.ArrayList;

import com.android.samservice.info.ContactUser;

public class ArticleInfo{
	public long timestamp;
	public long article_id;
	public int status;
	public String comment;
	public ContactUser publisher;
	public ArrayList<String> pics;

	public ArrayList<ContactUser> recommander;
	public ArrayList<CommentInfo> comments;
	
	public ArticleInfo(){
		timestamp = 0;
		article_id = 0;
		status = 0;
		comment = null;
		publisher = new ContactUser();
		pics = new ArrayList<String>();
		recommander = new ArrayList<ContactUser>();

		comments = new ArrayList<CommentInfo>();
	}

};


