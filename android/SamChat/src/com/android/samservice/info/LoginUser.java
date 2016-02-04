package com.android.samservice.info;



/*
	id(primary) | status | username | phonenumber |password |usertype | image file |description |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate
*/

public class LoginUser
{
	public static int INACTIVE = 0;
	public static int ACTIVE = 1;
	
	public static int USER = 0;
	public static int MIDSERVER = 1;
	public static int TOPSERVER = 2;


	public long id;
	public int status;
	public String username;
	public String phonenumber;
	public String password;
	public int usertype;
	public String imagefile;
	public String description;
	public long logintime;
	public long logouttime;
	public long unique_id;
	public String easemob_username;
	public int easemob_status;
	public long lastupdate;

	public LoginUser(String cellphone,String username,String password){
		this.id = 0;
		this.status = INACTIVE;
		this.username = username;
		this.phonenumber = cellphone;
		this.password = password;
		this.usertype = USER;
		this.imagefile = null;
		this.description = null;
		this.logintime = System.currentTimeMillis();
		this.logouttime = 0;
		this.unique_id = 0;
		this.easemob_username = null;
		this.easemob_status = INACTIVE;
		this.lastupdate=0;
		
	}
	
	public LoginUser(){
		this.id = 0;
		this.status = INACTIVE;
		this.username = null;
		this.phonenumber = null;
		this.password = null;
		this.usertype = USER;
		this.imagefile = null;
		this.description = null;
		this.logintime = 0;
		this.logouttime = 0;
		this.unique_id = 0;
		this.easemob_username = null;
		this.easemob_status = INACTIVE;
		this.lastupdate=0;
	}
	
	public int getUserType(){
		return this.usertype;
	}

	public long getid(){
		return this.id;
	}

	public String getusername(){
		return this.username;
	}

	public String geteasemob_username(){
		return this.easemob_username;
	}

	public String getpassword(){
		return this.password;
	}

	public String getphonenumber(){
		return this.phonenumber;
	}

	public long getlastupdate(){
		return this.lastupdate;
	}

	public void seteasemob_status(int easemob_status){
		this.easemob_status  = easemob_status;
	}
}