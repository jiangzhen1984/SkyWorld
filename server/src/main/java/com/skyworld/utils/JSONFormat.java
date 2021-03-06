package com.skyworld.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.skyworld.init.GlobalConstants;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.SKServicer.SKServicerCMPItem;

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
	
	
	
	public static void populateServicerCmpData(JSONObject jsobj, SKServicer servicer) {
		JSONArray cmpJ = new JSONArray();
		jsobj.put("cmp", cmpJ);
		
		JSONObject a = new JSONObject();
		a.put("cmplogo", servicer.getLogoURL());
		a.put("cmpwebsite", servicer.getWebsite());
		a.put("cmpname", servicer.getCmpName());
		a.put("cmpdesc", servicer.getCmpDesc());
		a.put("cmpphone", servicer.getCmpPhone());
		cmpJ.put(a);
	}
	
	
	public static void populateServicerCmpItemData(JSONObject jsobj, List<SKServicerCMPItem> items) {
		JSONArray cmpJ = new JSONArray();
		jsobj.put("cmpitem", cmpJ);
		
		for (SKServicerCMPItem it : items) {
			JSONObject a = new JSONObject();
			a.put("id", it.id);
			a.put("title", it.title);
			a.put("con", it.content);
			a.put("pic", it.pic);
			a.put("item-url", GlobalConstants.HOME_HTTP+"/" +GlobalConstants.HOME_HOST+"/skservicer/setting/cmplist/view/"+it.id);
			cmpJ.put(a);
		}
	}
	
	public static void populateEasemobData(JSONObject parent, JSONObject jsobj, User user) {
		parent.put("easemob", jsobj);
		jsobj.put("username", user.getMail());
	}
	
	public static void populateEasemobData(JSONObject parent,  User user) {
		populateEasemobData(parent, new JSONObject() , user);
	}

}
