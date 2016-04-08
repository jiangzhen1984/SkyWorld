package com.skyworld.service.skservicer;

import java.util.List;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.service.APIBasicJsonApiService;
import com.skyworld.service.APICode;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.service.dsf.SKServicer.SKServicerCMPItem;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.SKServicerCmpQueryResponse;

public class APICmpQueryService extends APIBasicJsonApiService {

	public static final int OPT_ID = 1;
	
	public static final int OPT_CMP_ITEM_LIST = 2;
	
	public static final int PAGE_COUNT = 20;

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		Token token = checkAuth(header);
		if (token == null) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		User user = CacheManager.getIntance().getUser(token);
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}

		int opt = OPT_ID;
		if (body.has("opt")) {
			opt = body.getInt("opt");
		}

		switch (opt) {
		case OPT_ID:
			return queryCmp(body);
		case OPT_CMP_ITEM_LIST:
			return queryCmpItemList(body);
		
		default:
			return new RTCodeResponse(
					APICode.SKSERVICER_QUERY_ERROR_TYPE_NOT_SUPPORT_OPT);
		}

	}
	
	private BasicResponse queryCmp(JSONObject body) {
		long uid = -1;
		if (!body.has("uid")) {
			return new RTCodeResponse(
					APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		uid = body.getLong("uid");
		User u = ServiceFactory.getESUserService().getUser(uid);
		if (u.getUserType() != UserType.SERVICER) {
			return new RTCodeResponse(
					APICode.SKSERVICER_QUERY_ERROR_TYPE_NOT_SERVICER);
		}
		ServiceFactory.getESKServicerService().populateCompanyInfor(
				(SKServicer) u);
		return new SKServicerCmpQueryResponse((SKServicer) u);
	}
	
	
	private BasicResponse queryCmpItemList(JSONObject body) {
		long uid = -1;
		if (!body.has("uid")) {
			return new RTCodeResponse(
					APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		uid = body.getLong("uid");
		User u = ServiceFactory.getESUserService().getUser(uid);
		if (u.getUserType() != UserType.SERVICER) {
			return new RTCodeResponse(
					APICode.SKSERVICER_QUERY_ERROR_TYPE_NOT_SERVICER);
		}
		
		int page = 1;
		if (body.has("page")) {
			page = body.getInt("page");
		}
		
		List<SKServicerCMPItem>  itemList = ServiceFactory.getESKServicerService().querySKServicerCMPItemList((SKServicer)u, page, PAGE_COUNT+1);
		return new SKServicerCmpQueryResponse((SKServicer) u, itemList);
	}
	
	
}
