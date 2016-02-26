package com.android.samservice;

import java.util.List;

public class QueryFGCoreObj extends SamCoreObj{

	public int fetch_count;
	
	public QueryFGCoreObj(CBObj cbobj,int fetch_count){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.fetch_count= fetch_count;
	}
}