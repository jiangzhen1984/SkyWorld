package com.skyworld.service.skservicer;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.service.APIBasicJsonApiService;
import com.skyworld.service.APICode;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.Customer;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.RegisterResponse;

public class APIUpgradeService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		Token token = checkAuth(header);
		if (token == null) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		if (!body.has("area") || !body.has("location") || !body.has("desc")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		String area = body.getString("area");
		String location = body.getString("location");
		String desc = body.getString("desc");
		
		SKServicer cache = CacheManager.getIntance().getSKServicer(token);
		if (cache != null) {
			return new RTCodeResponse(APICode.USER_UPGRADE_ERROR_ALREADY);
		}
		
		
		Customer cus = CacheManager.getIntance().getCustomer(token);
		if (cus == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		

		SKServicer servicer = new SKServicer((User) cus);
		servicer.setArea(area);
		servicer.setLocation(location);
		servicer.setServiceDesc(desc);
		
		boolean ret = ServiceFactory.getESUserService()
				.updradeUserToSKServicer(servicer);
		if (!ret) {
			return new RTCodeResponse(APICode.USER_UPGRADE_ERROR_INTERNAL);
		}

		Token newToken = CacheManager.getIntance().saveUser(servicer);
		return new RegisterResponse(servicer, newToken);

	}

}
