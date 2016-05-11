package com.android.samservice;

import java.util.List;

public class QueryPublicInfoCoreObj extends SamCoreObj{
	
	public long uid;
	
	public QueryPublicInfoCoreObj(long uid,CBObj cbobj){
		this.uid = uid;
		refCBObj = cbobj;
		request_status = STATUS_INIT;
	}
}
