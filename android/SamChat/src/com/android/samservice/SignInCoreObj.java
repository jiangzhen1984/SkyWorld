package com.android.samservice;

public class SignInCoreObj extends SamCoreObj{

	public String countrycode;
	public String username;
	public String password;
	
	
	public SignInCoreObj(CBObj cbobj,String un,String pwd){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.username = un;
		this.password = pwd;
		this.countrycode=null;
	}

	public SignInCoreObj(CBObj cbobj,String countrycode,String un,String pwd){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.username = un;
		this.password = pwd;
		this.countrycode = countrycode;
	}
}