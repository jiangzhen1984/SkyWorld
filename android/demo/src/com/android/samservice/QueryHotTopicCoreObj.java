package com.android.samservice;

import java.util.List;

public class QueryHotTopicCoreObj extends SamCoreObj{
	
	public long cur_count;
	public long update_time_pre;
	
	public QueryHotTopicCoreObj(long cur_count,long update_time_pre,CBObj cbobj){
		this.cur_count= cur_count;
		this.update_time_pre = update_time_pre;
		refCBObj = cbobj;
		request_status = STATUS_INIT;
	}
}

