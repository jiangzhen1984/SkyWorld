package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.push.event.ConnectionCloseEvent;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APILogoutService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");

		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}


		
		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		
		Token token = TokenFactory.valueOf(tokenId);
		User user = CacheManager.getIntance().removeUser(token);
		if (user != null && user.getPushTerminal() != null) {
			//FIXME check token legal or not
			user.getPushTerminal().postEvent(new ConnectionCloseEvent());
		}
		
		return new RTCodeResponse(APICode.SUCCESS);
		
	}

}
