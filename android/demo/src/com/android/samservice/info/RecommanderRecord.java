package com.android.samservice.info;

import java.io.Serializable;

/*
	id(primary) |recommander_phonenumber | fg_id |timestamp
	*/
public class RecommanderRecord implements Serializable
{
	public long id;
	public String recommander_phonenumber;
	public long fg_id;
	public long timestamp;

	public RecommanderRecord(){
		this.id = 0;
		this.recommander_phonenumber = null;
		this.fg_id = 0;
		this.timestamp = 0;
	}

	public RecommanderRecord(String recommander_phonenumber,long fg_id,long timestamp){
		this.id = 0;
		this.recommander_phonenumber = recommander_phonenumber;
		this.fg_id = fg_id;
		this.timestamp = timestamp;
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}

	public void setrecommander_phonenumber(String recommander_phonenumber){
		this.recommander_phonenumber = recommander_phonenumber;
	}
	public String getrecommander_phonenumber(){
		return this.recommander_phonenumber;
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