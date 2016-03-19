package com.android.samservice;

import java.util.List;

public class QueryFGCoreObj extends SamCoreObj{

	public int fetch_count;
	public long start_timestamp;
	
	public QueryFGCoreObj(CBObj cbobj,long start_timestamp, int fetch_count){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.fetch_count= fetch_count;
		this.start_timestamp = start_timestamp;
	}
}