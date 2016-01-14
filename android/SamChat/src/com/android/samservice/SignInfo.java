package com.android.samservice;

public class SignInfo{
	public String username;
	public String password;
	public String cellphone;
	public String token;

	public SignInfo(String username ,String password,String cellphone){
		this.username = username;
		this.password = password;
		this.cellphone = cellphone;
		this.token = null;
	}

	public SignInfo(String username ,String password){
		this.username = username;
		this.password = password;
		this.cellphone = null;
		this.token = null;
	}
	
	public SignInfo(){
		this.username = null;
		this.password = null;
		this.cellphone = null;
		this.token = null;
	}
	
};

