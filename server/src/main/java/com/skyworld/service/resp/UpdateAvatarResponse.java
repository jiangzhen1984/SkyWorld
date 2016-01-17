package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.cache.Token;
import com.skyworld.service.APICode;
import com.skyworld.service.dsf.User;

public class UpdateAvatarResponse extends JSONBasicResponse {
	
	private User user;
	
	


	public UpdateAvatarResponse(User user) {
		super();
		this.user = user;
	}





	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		resp.put("ret", APICode.SUCCESS);
		
		JSONObject userResp = new JSONObject();
		resp.put("user", userResp);
		
		userResp.put("name", user.getName());
		userResp.put("cellphone", user.getCellPhone());
		userResp.put("mail", user.getMail());
		userResp.put("username", user.getMail());
		userResp.put("type", user.getUserType().ordinal());
		if (user.getAvatar() != null) {
			JSONObject avatar = new JSONObject();
			userResp.put("avatar", avatar);
			avatar.put("origin", user.getAvatarPath());
		}
		return resp;
	}





	public User getUser() {
		return user;
	}





	public void setUser(User user) {
		this.user = user;
	}


	
	
}
