package com.android.samservice.info;

import java.io.Serializable;
/*
	id(primary) |unique_id | username | owner_unique_id
*/
public class FollowerRecord implements Serializable
{
	public long id;
	public long unique_id;
	public String username;
	public long owner_unique_id;

	public FollowerRecord(){
		this.id = 0;
		this.unique_id = 0;
		this.username = null;
		this.owner_unique_id = 0;

	}

	public FollowerRecord(long unique_id,String username,long owner_unique_id){
		this.id = 0;
		this.unique_id = unique_id;
		this.username = username;
		this.owner_unique_id = owner_unique_id;
	}
	
	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}

	public void setunique_id(long unique_id){
		this.unique_id = unique_id;
	}
	public long getunique_id(){
		return this.unique_id;
	}

	public void setusername(String username){
		this.username = username;
	}
	public String getusername(){
		return this.username;
	}
	

	public void setowner_unique_id(long owner_unique_id){
		this.owner_unique_id = owner_unique_id;
	}
	public long getowner_unique_id(){
		return this.owner_unique_id;
	}

}

