package com.skyworld.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyworld.cache.Token;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.JSONBasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.UserQueryResponse;

public class APIQueryService extends APIBasicJsonApiService {

	private static final int OPT_QUERY_USER = 1;
	
	private static final int OPT_QUERY_USER_LIST = 2;
	
	private static final int OPT_QUERY_USER_EXIST = 3;

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		
		Token token = checkAuth(header);


		if (!body.has("opt") || !body.has("param")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		int opt = body.getInt("opt");
		JSONObject param = body.getJSONObject("param");
		switch (opt) {
		case OPT_QUERY_USER:
			if (token == null) {
				return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
			}
			return queryUser(param);
		case OPT_QUERY_USER_LIST:
			if (token == null) {
				return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
			}
			return queryUserList(param);
		case OPT_QUERY_USER_EXIST:
			return queryUser(param);
		default:
			return new RTCodeResponse(APICode.QUERY_ERROR_OPT_NOT_SUPPORT);
		}
	}

	private JSONBasicResponse queryUser(JSONObject param) {
		if (!param.has("username")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		String uname = param.getString("username");
		User user = ServiceFactory.getESUserService().selectUser(uname, uname, true);
		if (user.getUserType() == UserType.SERVICER) {
			user = new SKServicer(user);
			ServiceFactory.getESUserService().populateServicer((SKServicer)user);
		}
		List<User> l = null;
		if (user != null) {
			l = new ArrayList<User>();
			l.add(user);
		}
		
		return new UserQueryResponse(l);
	}
	
	
	
	private JSONBasicResponse queryUserList(JSONObject param) {
		if (!param.has("usernames")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		JSONArray unames = param.getJSONArray("usernames");
		String[] phones = new String[unames.length()];
		for(int i = 0; i < phones.length; i++) {
			phones[i] = unames.getString(i);
		}
		List<User>  l= ServiceFactory.getESUserService().selectUserList(phones, phones);
		
		return new UserQueryResponse(l);
	}
}
