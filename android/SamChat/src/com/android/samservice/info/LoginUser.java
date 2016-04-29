package com.android.samservice.info;

import com.android.samservice.Constants;



/*
	id(primary) | status | username | countrycode |phonenumber |password |usertype | image file |description |area | location |login time|logout time | unique_id | easemob_username |easemob_status |lastupdate |sam_serach_conversation_existed
*/

public class LoginUser
{
	public static int INACTIVE = 0;
	public static int ACTIVE = 1;
	
	public static int USER = 0;
	public static int MIDSERVER = 1;

	public static int NOT_EXISTED = 0;
	public static int EXISTED=1;

	public long id;
	public int status;
	public String username;
	public String phonenumber;
	public String countrycode;
	public String password;
	public int usertype;
	public String imagefile;
	public String description;
	public String area;
	public String location;
	public long logintime;
	public long logouttime;
	public long unique_id;
	public String easemob_username;
	public int easemob_status;
	public long lastupdate;

	public int conversation_existed;

	public LoginUser(String countrycode,String cellphone,String username,String password){
		this.id = 0;
		this.status = INACTIVE;
		this.username = username;
		this.countrycode = countrycode;
		this.phonenumber = cellphone;
		this.password = password;
		this.usertype = USER;
		this.imagefile = null;
		this.description = null;
		this.area = null;
		this.location =null;
		this.logintime = System.currentTimeMillis();
		this.logouttime = 0;
		this.unique_id = 0;
		this.easemob_username = null;
		this.easemob_status = INACTIVE;
		this.lastupdate=0;

		this.conversation_existed=NOT_EXISTED;
		
	}
	
	public LoginUser(){
		this.id = 0;
		this.status = INACTIVE;
		this.username = null;
		this.countrycode = null;
		this.phonenumber = null;
		this.password = null;
		this.usertype = USER;
		this.imagefile = null;
		this.description = null;
		this.area = null;
		this.location =null;
		this.logintime = 0;
		this.logouttime = 0;
		this.unique_id = 0;
		this.easemob_username = null;
		this.easemob_status = INACTIVE;
		this.lastupdate=0;

		this.conversation_existed=NOT_EXISTED;
	}


	public int getconversation_existed(){
		return this.conversation_existed;
	}

	public void setconversation_existed(int existed){
		this.conversation_existed = existed;
	}
	
	public int getUserType(){
		return this.usertype;
	}

	public long getid(){
		return this.id;
	}

	public long getunique_id(){
		return this.unique_id;
	}

	public String getusername(){
		return this.username;
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

	public String getarea(){
		return this.area;
	}

	public String getlocation(){
		return this.location;
	}

	public String getdescription(){
		return this.description;
	}

	public String getimagefile(){
		return this.imagefile;
	}
}