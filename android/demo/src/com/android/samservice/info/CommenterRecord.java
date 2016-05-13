package com.android.samservice.info;

import java.io.Serializable;

/*
	id(primary) |commenter_phonenumber | content | fg_id |timestamp
	*/
public class CommenterRecord implements Serializable
{
	public long id;
	public String commenter_phonenumber;
	public String content;
	public long fg_id;
	public long timestamp;

	public CommenterRecord(){
		this.id = 0;
		this.commenter_phonenumber = null;
		this.content = null;
		this.fg_id = 0;
		this.timestamp = 0;
	}

	public CommenterRecord(String commenter_phonenumber,String content,long fg_id,long timestamp){
		this.id = 0;
		this.content = content;
		this.commenter_phonenumber = commenter_phonenumber;
		this.fg_id = fg_id;
		this.timestamp = timestamp;
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}

	public void setcommenter_phonenumber(String commenter_phonenumber){
		this.commenter_phonenumber = commenter_phonenumber;
	}
	public String getcommenter_phonenumber(){
		return this.commenter_phonenumber;
	}

	public void setcontent(String content){
		this.content = content;
	}
	public String getcontent(){
		return this.content;
	}

	public void setfg_id(long fg_id){
		this.fg_id = fg_id;
	}
	public long getfg_id(){
		return this.fg_id;
	}

	public void settimestamp(long timestamp){
		this.timestamp = timestamp;
	}
	public long gettimestamp(){
		return this.timestamp;
	}

}