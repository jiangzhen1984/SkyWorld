package com.android.samservice;

import java.util.List;

import com.android.samservice.info.ContactUser;

public class QueryuiCoreObj extends SamCoreObj{
	public List<String> easemob_names;
	public String phonenumber;
	public List<ContactUser> uiArray;

	public QueryuiCoreObj(CBObj obj, String phonenumber){
		refCBObj = obj;
		request_status = STATUS_INIT;
		this.phonenumber = phonenumber;
		this.easemob_names = null;
		this.uiArray = null;
	}

	public QueryuiCoreObj(CBObj obj, List<String> easemob_names){
		refCBObj = obj;
		request_status = STATUS_INIT;
		this.phonenumber = null;
		this.easemob_names = easemob_names;
		this.uiArray = null;
	}
}