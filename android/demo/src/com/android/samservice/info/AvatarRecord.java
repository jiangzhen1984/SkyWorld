package com.android.samservice.info;

import java.io.Serializable;

/*
	id(primary) |phonenumber | avatarname | nickname
*/
public class AvatarRecord implements Serializable
{
	public long id;
	public String phonenumber;
	public String avatarname;
	public String nickname;

	public AvatarRecord(){
		this.id = 0;
		this.phonenumber = null;
		this.avatarname = null;
		this.nickname = null;
	}

	public AvatarRecord(String phonenumber,String avatarname,String nickname){
		this.id = 0;
		this.phonenumber = phonenumber;
		this.avatarname = avatarname;
		this.nickname = nickname;
	}

	public void setid(long id){
		this.id = id;
	}

	public long getid(){
		return this.id;
	}

	public void setphonenumber(String phonenumber){
		this.phonenumber = phonenumber;
	}
	
	public String getphonenumber(){
		return this.phonenumber;
	}

	public void setavatarname(String avatarname){
		this.avatarname = avatarname;
	}
	
	public String getavatarname(){
		return this.avatarname;
	}

	public void setnickname(String nickname){
		this.nickname = nickname;
	}
	
	public String getnickname(){
		return this.nickname;
	}

}