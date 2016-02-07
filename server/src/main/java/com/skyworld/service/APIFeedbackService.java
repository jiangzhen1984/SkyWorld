package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.Feedback;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIFeedbackService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		
		if (!body.has("comment") ) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		
		Feedback feedback = new Feedback();
		feedback.setComments(body.getString("comment"));
		feedback.setFeedbackTimestamp();
		//TODO check token?
		if (header.has("token")) {
			User user = CacheManager.getIntance().getUser(TokenFactory.valueOf(header.getString("token")));
			feedback.setUser(user);
		}
		
		ServiceFactory.getSystemBasicService().createFeedback(feedback);
		
		return new RTCodeResponse(APICode.SUCCESS);
	}
	
	

}
