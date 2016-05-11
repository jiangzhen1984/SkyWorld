package com.android.samservice;

import com.android.samservice.info.ContactUser;

public class CommentInfo{
		public ContactUser commenter;
		public long comments_timestamp;
		public String content;

		public CommentInfo(){
			commenter = null;
			comments_timestamp = 0;
			content = null;
		}
};