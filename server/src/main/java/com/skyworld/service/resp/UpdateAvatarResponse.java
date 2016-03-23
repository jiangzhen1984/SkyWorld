package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.dsf.User;
import com.skyworld.utils.JSONFormat;

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
		JSONFormat.populateUserData(userResp, user);
		JSONFormat.populateEasemobData(userResp, user);
		return resp;
	}





	public User getUser() {
		return user;
	}





	public void setUser(User user) {
		this.user = user;
	}


	
	
}
