package com.android.samservice.info;

import java.io.Serializable;
/*
	id(primary) |timestamp | fg_id |status |comment | publisher_phonenumber |
*/
public class FGRecord implements Serializable
{
	public long id;
	public long timestamp;
	public long fg_id;
	public int status;
	public String comment;
	public String publisher_phonenumber;
	public String owner_phonenumber;

	public FGRecord(){
		this.id = 0;
		this.timestamp = 0;
		this.fg_id = 0;
		this.status = 0;
		this.comment = null;
		this.publisher_phonenumber = null;
		this.owner_phonenumber = null;

	}

	public FGRecord(long timestamp,long fg_id,int status,String comment,String publisher_phonenumber,String owner_phonenumber){
		this.id = 0;
		this.timestamp = timestamp;
		this.fg_id = fg_id;
		this.status = status;
		this.comment = comment;
		this.publisher_phonenumber = publisher_phonenumber;
		this.owner_phonenumber = owner_phonenumber;
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}

	public void settimestamp(long timestamp){
		this.timestamp = timestamp;
	}
	public long gettimestamp(){
		return this.timestamp;
	}

	public void setfg_id(long fg_id){
		this.fg_id = fg_id;
	}
	public long getfg_id(){
		return this.fg_id;
	}

	public void setstatus(int status){
		this.status = status;
	}
	public int getstatus(){
		return this.status;
	}

	public void setcomment(String comment){
		this.comment = comment;
	}
	public String getcomment(){
		return this.comment;
	}

	public void setpublisher_phonenumber(String publisher_phonenumber){
		this.publisher_phonenumber = publisher_phonenumber;
	}	
	public String getpublisher_phonenumber(){
		return this.publisher_phonenumber;
	}

	public void setowner_phonenumber(String owner_phonenumber){
		this.owner_phonenumber = owner_phonenumber;
	}	
	public String getowner_phonenumber(){
		return this.owner_phonenumber;
	}

}
