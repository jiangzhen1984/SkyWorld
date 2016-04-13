package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.trans.APITransService;


public class APIHotTopicService extends APIBasicJsonApiService{
	
	private APITransService query_topic_list;

	public void setQuery_topic_list(APITransService query_topic_list) {
		this.query_topic_list = query_topic_list;
	}

	@Override
	protected BasicResponse service(JSONObject json) {
 		JSONObject header = json.getJSONObject("header");
		String action = header.getString("action");
		
		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		Token token = TokenFactory.valueOf(tokenId);
		User user = CacheManager.getIntance().getUser(token);
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		
		BasicResponse response = null;
		try {
			response = ((APITransService)(this.getClass().getDeclaredField(action).get(this))).service(json);
		}catch (Exception e) {
 			log.error(e.getMessage(),e);
 			response = new RTCodeResponse(APICode.ACTION_NOT_SUPPORT);;
		}
   		return response;
	}
 
}
