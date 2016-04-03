package com.skyworld.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.UserQueryResponse;

/**
 * @FIXME do not support cluster
 * @author 28851274
 *
 */
public class APIUserRelationQueryService extends APIBasicJsonApiService {
	
	
	public static final int TYPE_FANS = 1;
	public static final int TYPE_FOLLOW = 2;
	public static final int TYPE_TEST_RELATIONS = 3;

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
			if (!user.isRelationQueryFlag()) {
				ServiceFactory.getESUserService().queryUserRelation(user);
			}
			relation = user.getRelationCopy();
			break;
		case TYPE_TEST_RELATIONS:
			if (!body.has("userid2")) {
				return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
			}
			long userId2 = body.getLong("userid2");
			User user2 = ServiceFactory.getESUserService().selectUser(userId2);
			if (user2 == null) {
				 return new UserQueryResponse(null);
			}
			boolean both = false;
			if (body.has("both")) {
				both = body.getBoolean("both");
			}
			boolean ret = ServiceFactory.getESUserService().queryRelation(user, user2, both);
			if (ret) {
				relation = new ArrayList<User>();
				relation.add(user2);
			}
			break;
		default:
			break;
		}
		
		
		return new UserQueryResponse(relation);
	}
	
	

}
