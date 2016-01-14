package com.android.samservice.info;

	/*
	id(primary) | username | phone number |image file | user type | description |
	*/
public class ContactUser
{
	public long id;
	public String username;
	public String phonenumber;
	public int usertype;
	public String imagefile;
	public String description;
	public long unique_id;
	public String easemob_username;

	public ContactUser(){
		this.id = 0;
		this.username = null;
		this.phonenumber = null;
		this.usertype = 0;
		this.imagefile = null;
		this.description = null;
		this.unique_id = 0;
		this.easemob_username = null;
	}

	public long get_id(){
		return this.id;
	}

	public String get_username(){
		return this.username;
	}

	public void set_username(String username){
		this.username = username;
	}

	public long get_unique_id(){
		return this.unique_id;
	}

	public void set_unique_id(long unique_id){
		this.unique_id = unique_id;
	}

	public String get_easemob_username(){
		return this.easemob_username;
	}

	public void set_easemob_username(String easemob_username){
		this.easemob_username = easemob_username;
	}

	public String get_phonenumber(){
		return this.phonenumber;
	}

	public void set_phonenumber(String phonenumber){
		this.phonenumber = phonenumber;
	}

	public String get_imagefile(){
		return this.imagefile;
	}

	public void set_imagefile(String imagefile){
		this.imagefile = imagefile;
	}

	public int get_usertype(){
		return this.usertype;
	}

	public void set_usertype(int usertype){
		this.usertype = usertype;
	}

	public String get_description(){
		return this.description;
	}

	public void set_description(String description){
		this.description = description;
	}
}