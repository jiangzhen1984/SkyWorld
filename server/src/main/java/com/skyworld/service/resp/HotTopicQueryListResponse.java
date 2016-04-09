package com.skyworld.service.resp;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.po.SWPHotTopic;

public class HotTopicQueryListResponse extends JSONBasicResponse{
	
	private List<SWPHotTopic> hotTopic ;
	
	private  Timestamp queryTime ;
	
	public HotTopicQueryListResponse(List<SWPHotTopic> hotTopic, Timestamp queryTime){
		this.hotTopic = hotTopic;
		this.queryTime = queryTime;
	}
	
	@Override
	public JSONObject getResponseJSON(){
		
		JSONObject resp = new JSONObject();
		resp.put("ret", APICode.SUCCESS);
		resp.put("query_time", queryTime.getTime());
		
		JSONArray tpcDtlArr = new JSONArray();
		resp.put("topics", tpcDtlArr);
		
		for(SWPHotTopic topic : hotTopic){
			JSONObject tpcDtl = new JSONObject();
			tpcDtl.put("name", topic.getName());
			tpcDtl.put("topic_type", topic.getType());
			tpcDtlArr.put(tpcDtl);
		}
		return resp;
 	}
  }
