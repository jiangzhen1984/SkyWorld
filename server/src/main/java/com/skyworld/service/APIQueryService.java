package com.skyworld.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.JSONBasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.UserQueryResponse;

public class APIQueryService extends APIBasicJsonApiService {

	private static final int OPT_QUERY_USER = 1;

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		

		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}


		if (!body.has("opt") || !body.has("param")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		int opt = body.getInt("opt");
		JSONObject param = body.getJSONObject("param");
		switch (opt) {
		case OPT_QUERY_USER:
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
		User user = ServiceFactory.getESUserService().selectUser(uname, uname);
		List<User> l = null;
		if (user != null) {
			l = new ArrayList<User>();
			l.add(user);
		}
		
		return new UserQueryResponse(l);
	}
}
