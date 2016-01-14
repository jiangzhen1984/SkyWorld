package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
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

		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		// FIXME check token legal

		SKServicer cache = CacheManager.getIntance().getSKServicer(TokenFactory.valueOf(tokenId));
		if (cache != null) {
			return new RTCodeResponse(APICode.USER_UPGRADE_ERROR_ALREADY);
		}
		
		
		Customer cus = CacheManager.getIntance().getCustomer(
				TokenFactory.valueOf(tokenId));
		if (cus == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}

		SKServicer servicer = new SKServicer((User) cus);
		boolean ret = ServiceFactory.getESUserService()
				.updradeUserToSKServicer(servicer);
		if (!ret) {
			return new RTCodeResponse(APICode.USER_UPGRADE_ERROR_INTERNAL);

		}

		Token newToken = CacheManager.getIntance().saveUser(servicer);
		return new RegisterResponse(servicer, newToken);

	}

}
