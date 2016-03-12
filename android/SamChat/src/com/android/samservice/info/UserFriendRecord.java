package com.android.samservice.info;

import java.io.Serializable;

/*
	id(primary) |friend
*/
public class UserFriendRecord implements Serializable
{
	public long id;
	public String friend;
	

	public UserFriendRecord(){
		this.id = 0;
		this.friend = null;
	}

	public UserFriendRecord(String friend){
		this.id = 0;
		this.friend = friend;
	}

	public void setid(long id){
		this.id = id;
	}

	public long getid(){
		return this.id;
	}

	public void setfriend(String friend){
		this.friend = friend;
	}
	
	public String getfriend(){
		return this.friend;
	}

}
