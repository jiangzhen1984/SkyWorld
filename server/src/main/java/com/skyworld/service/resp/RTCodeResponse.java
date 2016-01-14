package com.skyworld.service.resp;

import org.json.JSONObject;

public class RTCodeResponse extends JSONBasicResponse {
	
	

	public RTCodeResponse(int code) {
		super();
		super.retCode = code;
	}

	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		resp.put("ret", retCode);
		return resp;
	}

}
