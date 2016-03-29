package com.skyworld.service.resp;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.utils.JSONFormat;

public class UserQueryResponse extends JSONBasicResponse {
	
	private List<User> userList;
	
	

	public UserQueryResponse(List<User> userList) {
		super();
		this.userList = userList;
	}



	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();

		resp.put("ret", APICode.SUCCESS);
		resp.put("count", userList.size());
		JSONArray users = new JSONArray();
		if (userList != null && userList.size() > 0) {
			for (User u : userList) {
				JSONObject jsonUser = new JSONObject();
				users.put(jsonUser);
				JSONFormat.populateUserData(jsonUser, u);
				if (u.getUserType() == UserType.SERVICER) {
					JSONFormat.populateServicerData(jsonUser, (SKServicer)u);
				}
				JSONFormat.populateEasemobData(jsonUser, u);
			}
		}
		resp.put("users", users);
		
		
		
		return resp;
	}

}
