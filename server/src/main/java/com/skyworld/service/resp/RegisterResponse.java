package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.cache.Token;
import com.skyworld.service.APICode;
import com.skyworld.service.dsf.User;

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
		
		userResp.put("name", user.getName());
		userResp.put("cellphone", user.getCellPhone());
		userResp.put("mail", user.getMail());
		userResp.put("username", user.getMail());
		userResp.put("type", user.getUserType().ordinal());
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
