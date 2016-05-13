package com.android.samservice;

public class SignUpCoreObj extends SamCoreObj{

	public String username;
	public String password;
	public String cellphone;
	public String country_code;
	
	public SignUpCoreObj(CBObj cbobj,String un,String pwd,String phonenumber,String country_code){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.username = un;
		this.password = pwd;
		this.cellphone = phonenumber;
		this.country_code = country_code;
	}
}