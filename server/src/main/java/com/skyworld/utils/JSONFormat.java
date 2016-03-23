package com.skyworld.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.init.GlobalConstants;
import com.skyworld.service.dsf.SKServicer;
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
		jsobj.put("username", user.getMail());
		jsobj.put("type", user.getUserType().ordinal());
		jsobj.put("lastupdate", user.getLastUpdate());
		jsobj.put("country_code", user.getCountyCode());

		if (user.getAvatar() != null) {
			JSONObject avatar = new JSONObject();
			jsobj.put("avatar", avatar);
			avatar.put("origin", GlobalConstants.AVATAR_HOST+user.getAvatarPath());
		}
	}
	
	
	public static void populateServicerData(JSONObject jsobj, SKServicer servicer) {
		jsobj.put("area", servicer.getArea());
		jsobj.put("location", servicer.getLocation());
		jsobj.put("desc", servicer.getServiceDesc());
	}
	
	
	public static void populateEasemobData(JSONObject parent, JSONObject jsobj, User user) {
		parent.put("easemob", jsobj);
		jsobj.put("username", user.getMail());
	}
	
	public static void populateEasemobData(JSONObject parent,  User user) {
		populateEasemobData(parent, new JSONObject() , user);
	}

}
