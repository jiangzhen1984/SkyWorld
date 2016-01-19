package com.skyworld.service.resp;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyworld.init.GlobalConstants;
import com.skyworld.service.APICode;
import com.skyworld.service.dsf.User;

public class UserQueryResponse extends JSONBasicResponse {
	
	private List<User> userList;
	
	

	public UserQueryResponse(List<User> userList) {
		super();
		this.userList = userList;
	}



	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		if (userList == null || userList.size() <= 0) {
			resp.put("ret", APICode.QUERY_ERROR_NO_ELEMENTS);
		} else {
			resp.put("ret", APICode.SUCCESS);
			JSONArray users = new JSONArray();
			for (User u : userList) {
				JSONObject jsonUser = new JSONObject();
				users.put(jsonUser);
				jsonUser.put("name", u.getName());
				jsonUser.put("cellphone", u.getCellPhone());
				jsonUser.put("mail", u.getMail());
				jsonUser.put("username", u.getMail());
				jsonUser.put("type", u.getUserType().ordinal());
				
				if (u.getAvatar() != null) {
					JSONObject avatar = new JSONObject();
					jsonUser.put("avatar", avatar);
					avatar.put("origin",  GlobalConstants.AVATAR_HOST+u.getAvatarPath());
				}
				
				
			}
			resp.put("users", users);
		}
		
		
		
		return resp;
	}

}
