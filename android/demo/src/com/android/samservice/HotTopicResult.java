package com.android.samservice;

import java.util.List;

public class HotTopicResult {

	public long query_time;
	public List<String> topics;
	
	public HotTopicResult(long query_time, List<String> topics){
		this.query_time = query_time;
		this.topics = topics;
	}
}
