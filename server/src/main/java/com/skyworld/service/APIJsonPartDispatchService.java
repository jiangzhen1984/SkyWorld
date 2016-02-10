package com.skyworld.service;

import java.util.Collection;

import javax.servlet.http.Part;

import org.json.JSONObject;

import com.skyworld.service.resp.BasicResponse;

public class APIJsonPartDispatchService extends APIBasicJsonPartApiService {

	@Override
	protected BasicResponse service(JSONObject json, Collection<Part> parts) {
		return null;
	}


	
	

}
