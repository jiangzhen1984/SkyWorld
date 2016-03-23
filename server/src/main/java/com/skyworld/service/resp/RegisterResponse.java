package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.cache.Token;
import com.skyworld.service.APICode;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.utils.JSONFormat;

public class RegisterResponse extends JSONBasicResponse {
	
	private User user;
	
	private Token token;
	

	


	public RegisterResponse(User user, Token token) {
		super();
		this.user = user;
		this.token = token;
	}





	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		resp.put("ret", APICode.SUCCESS);
		resp.put("token", token);
		
		JSONObject userResp = new JSONObject();
		resp.put("user", userResp);
		
		
		JSONFormat.populateUserData(userResp, user);
		if (user.getUserType() == UserType.SERVICER) {
			JSONFormat.populateServicerData(userResp, (SKServicer)user);
		}

		userResp.put("lastupdate", user.getLastUpdate());
		return resp;
	}





	public User getUser() {
		return user;
	}





	public void setUser(User user) {
		this.user = user;
	}





	public Token getToken() {
		return token;
	}





	public void setToken(Token token) {
		this.token = token;
	}

	
	
}
