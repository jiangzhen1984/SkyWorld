package com.android.samservice;

public class SignInfo{
	public String username;
	public String password;
	public String cellphone;
	public String country_code;
	public String token;

	public SignInfo(String username ,String password,String cellphone,String country_code){
		this.username = username;
		this.password = password;
		this.cellphone = cellphone;
		this.country_code = country_code;
		this.token = null;
	}

	public SignInfo(String username ,String password){
		this.username = username;
		this.password = password;
		this.cellphone = null;
		this.country_code = null;
		this.token = null;
	}

	public SignInfo(String countrycode,String cellphone ,String password){
		this.username = null;
		this.password = password;
		this.cellphone = cellphone;
		this.country_code = countrycode;
		this.token = null;
	}
	
	public SignInfo(){
		this.username = null;
		this.password = null;
		this.cellphone = null;
		this.country_code = null;
		this.token = null;
	}
	
};

