package com.android.samservice;

import java.util.List;

public class QueryFollowerCoreObj extends SamCoreObj{
	
	public QueryFollowerCoreObj(CBObj cbobj){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
	}
}

