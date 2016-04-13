package com.skyworld.service.trans;

import org.json.JSONObject;

import com.skyworld.service.resp.BasicResponse;

public interface APITransService {
	public BasicResponse service(JSONObject json);
}
