package com.skyworld.service;

import java.util.List;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.UserQueryResponse;

public class APIUserRelationQueryService extends APIBasicJsonApiService {
	
	
	public static final int TYPE_FANS = 1;
	public static final int TYPE_FOLLOW = 2;

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		
		if (!body.has("type")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		Token token = checkAuth(header);
		if (token == null) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		User user = CacheManager.getIntance().getUser(token);
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		
		int type = body.getInt("type");
		List<User> relation = null;
		switch (type) {
		case TYPE_FANS:
			relation = ServiceFactory.getESUserService().queryUserRelationReverse(user);
			break;
		case TYPE_FOLLOW:
			ServiceFactory.getESUserService().queryUserRelation(user);
			relation = user.getRelationCopy();
			break;
		default:
			break;
		}
		
		
		return new UserQueryResponse(relation);
	}
	
	

}
