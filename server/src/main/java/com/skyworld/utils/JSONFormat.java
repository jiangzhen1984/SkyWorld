package com.skyworld.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.init.GlobalConstants;
import com.skyworld.service.dsf.User;

public class JSONFormat {

	public static Map<String, JSONObject> parse(String data) {
		Map<String, JSONObject> map = new HashMap<String, JSONObject>();
		try {
			JSONTokener jsonParser = new JSONTokener(data);
			JSONObject request = (JSONObject) jsonParser.nextValue();
			JSONObject header = request.getJSONObject("header");
			JSONObject body = request.getJSONObject("body");
			if (header == null || body == null) {
				return null;
			}
			
			map.put("header", header);
			map.put("body", body);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	public static void populateUserData(JSONObject jsobj, User user) {
		if (jsobj == null || user == null) {
			throw new NullPointerException(" jsobj or user is null");
		}
		
		jsobj.put("id", user.getId());
		jsobj.put("name", user.getName());
		jsobj.put("cellphone", user.getCellPhone());
		jsobj.put("mail", user.getMail());
		if (user.getAvatar() != null) {
			JSONObject avatar = new JSONObject();
			jsobj.put("avatar", avatar);
			avatar.put("origin", GlobalConstants.AVATAR_HOST+user.getAvatarPath());
		}
	}

}
