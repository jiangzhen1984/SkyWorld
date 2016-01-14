package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.push.event.MessageEvent;
import com.skyworld.pushimpl.EasemobMessage;
import com.skyworld.service.dsf.Customer;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.service.resp.RegisterResponse;

public class APILoginService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject body = json.getJSONObject("body");

		if (!body.has("username") || !body.has("pwd")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		String uname = body.getString("username");
		String pwd = (String) body.get("pwd");

		User user = ServiceFactory.getESUserService().selectUser(uname, uname,
				pwd);
		if (user == null) {
			return new RTCodeResponse(
					APICode.LOGIN_ERROR_INCORRECT_USER_NAME_OR_PWD);
		}

		if (user.getUserType() == UserType.CUSTOMER) {
			user = new Customer(user);
		} else if (user.getUserType() == UserType.SERVICER) {
			user = new SKServicer(user);
		}
		Token token = CacheManager.getIntance().saveUser(user);
		//Add pending event of Easemob message
		user.addPendingEvent(new MessageEvent(new EasemobMessage(user)));
		return new RegisterResponse(user, token);
	}

}
