package com.skyworld.service.resp;

import org.json.JSONObject;

public abstract class JSONBasicResponse extends BasicResponse {

	@Override
	public String getResponse() {
		return getResponseJSON().toString();
	}

	
	
	public abstract JSONObject getResponseJSON();

	
}
