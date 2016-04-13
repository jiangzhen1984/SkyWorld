package com.android.samservice.info;

import java.io.Serializable;

import com.android.samservice.Constants;

/*
id(primary) | owner_unique_id | cmplogo | cmpwebsite | cmpname | cmpdesc | cmpphone
*/
public class PublicInfo implements Serializable
{
	public long id;
	public long owner_unique_id;
	public String cmplogo;
	public String cmpwebsite;
	public String cmpname;
	public String cmpdesc;
	public String cmpphone;

	public PublicInfo(){
		this.id = 0;
		this.owner_unique_id = 0;
		this.cmplogo = null;
		this.cmpwebsite = null;
		this.cmpname = null;
		this.cmpdesc = null;
		this.cmpphone = null;
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}
	

	public void setowner_unique_id(long owner_unique_id){
		this.owner_unique_id = owner_unique_id;
	}
	public long getowner_unique_id(){
		return this.owner_unique_id;
	}


	public String getcmplogo(){
		return this.cmplogo;
	}
	public void setcmplogo(String cmplogo){
		this.cmplogo = cmplogo;
	}

	
	public String getcmpwebsite(){
		return this.cmpwebsite;
	}
	public void setcmpwebsite(String cmpwebsite){
		this.cmpwebsite = cmpwebsite;
	}


	public void setcmpname(String cmpname){
		this.cmpname = cmpname;
	}
	public String getcmpname(){
		return this.cmpname;
	}


	public String getcmpdesc(){
		return this.cmpdesc;
	}
	public void setcmpdesc(String cmpdesc){
		this.cmpdesc = cmpdesc;
	}

	public String getcmpphone(){
		return this.cmpphone;
	}
	public void setcmpphone(String cmpphone){
		this.cmpphone = cmpphone;
	}
	
}