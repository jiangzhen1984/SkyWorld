package com.android.samservice.info;

import java.io.Serializable;

import com.android.samservice.Constants;

	/*
	id(primary) | username | phonenumber | imagefile |description |area | location | unique_id | easemob_username | lastupdate
	*/
public class ContactUser implements Serializable
{
	public long id;
	public String username;
	public String phonenumber;
	public int usertype;
	public String imagefile;
	public String description;
	public String area;
	public String location;
	public long unique_id;
	public String easemob_username;
	public long lastupdate;

	public ContactUser(){
		this.id = 0;
		this.username = null;
		this.phonenumber = null;
		this.usertype = 0;
		this.imagefile = null;
		this.description = null;
		this.area = null;
		this.location = null;
		this.unique_id = 0;
		this.easemob_username = null;
		this.lastupdate = 0;
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}
	

	public void setusername(String username){
		this.username = username;
	}
	public String getusername(){
		return this.username;
	}


	public String getphonenumber(){
		return this.phonenumber;
	}
	public void setphonenumber(String phonenumber){
		this.phonenumber = phonenumber;
	}

	
	public int getusertype(){
		return this.usertype;
	}
	public void setusertype(int usertype){
		this.usertype = usertype;
	}


	public void setimagefile(String imagefile){
		this.imagefile = imagefile;
	}
	public String getimagefile(){
		return this.imagefile;
	}


	public String getdescription(){
		return this.description;
	}
	public void setdescription(String description){
		this.description = description;
	}

	public String getarea(){
		return this.area;
	}
	public void setarea(String area){
		this.area = area;
	}

	public String getlocation(){
		return this.location;
	}
	public void setlocation(String location){
		this.location = location;
	}

	public long getunique_id(){
		return this.unique_id;
	}
	public void setunique_id(long unique_id){
		this.unique_id = unique_id;
	}


	public String geteasemob_username(){
		if(this.easemob_username == null){
			if(Constants.USERNAME_EQUAL_EASEMOB_ID)
				return this.username;
			else
				return this.phonenumber;
		}
		return this.easemob_username;
	}
	public void seteasemob_username(String easemob_username){
		this.easemob_username = easemob_username;
	}

	public long getlastupdate(){
		return this.lastupdate;
	}
	public void setlastupdate(long lastupdate){
		this.lastupdate = lastupdate;
	}

	
	

	

	

	
}