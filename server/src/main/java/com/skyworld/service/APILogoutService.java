package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.push.event.ConnectionCloseEvent;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APILogoutService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");

		Token token = checkAuth(header);
		if (token == null) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		User user = CacheManager.getIntance().removeUser(token);
		if (user != null && user.getPushTerminal() != null) {
			//FIXME check token legal or not
			user.getPushTerminal().postEvent(new ConnectionCloseEvent());
		}
		
		return new RTCodeResponse(APICode.SUCCESS);
		
	}

}
