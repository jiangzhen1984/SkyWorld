package com.android.samservice;

import java.util.List;

import com.android.samservice.info.ContactUser;

public class QueryuiCoreObj extends SamCoreObj{
	public List<String> easemob_names;
	public String queryname;
	public List<ContactUser> uiArray;
	public boolean withOutToken;

	public QueryuiCoreObj(CBObj obj, String queryname){
		refCBObj = obj;
		request_status = STATUS_INIT;
		this.queryname = queryname;
		this.easemob_names = null;
		this.uiArray = null;
		this.withOutToken = false;
	}

	public QueryuiCoreObj(CBObj obj, String queryname,boolean withOutToken){
		refCBObj = obj;
		request_status = STATUS_INIT;
		this.queryname = queryname;
		this.easemob_names = null;
		this.uiArray = null;
		this.withOutToken = withOutToken;
	}

	public QueryuiCoreObj(CBObj obj, List<String> easemob_names){
		refCBObj = obj;
		request_status = STATUS_INIT;
		this.queryname = null;
		this.easemob_names = easemob_names;
		this.uiArray = null;
		this.withOutToken = false;
	}
}