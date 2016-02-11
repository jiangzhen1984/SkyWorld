package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIFollowService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		
		if (!header.has("token") || !body.has("user_id") || !body.has("flag") || !body.has("both")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		User user = CacheManager.getIntance().getUser(TokenFactory.valueOf(header.getString("token")));
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		long userId2 = body.getLong("user_id");
		User user2 = ServiceFactory.getESUserService().getUser(userId2);
		if (user2 == null) {
			new RTCodeResponse(APICode.FOLLOW_ERROR_USER_NOT_EXIST);
		}
		
		boolean both = body.getBoolean("both");
		int flag = body.getInt("flag");
		switch (flag) {
		case 1:
			ServiceFactory.getESUserService().makeRelation(user, user2,both);
			break;
		case 2:
			ServiceFactory.getESUserService().removeRelation(user, user2, both);
		default:
			new RTCodeResponse(APICode.FOLLOW_ERROR_UN_SUPPORT_FLAG);
		}
		return new RTCodeResponse(APICode.SUCCESS);
	}

}
