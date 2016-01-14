package com.skyworld.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONTokener;

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

}
