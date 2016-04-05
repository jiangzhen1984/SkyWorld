package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.utils.JSONFormat;

public class SkSkervicerCmpQueryResponse extends JSONBasicResponse {
	
	
	private SKServicer servicer;
	
	
	

	public SkSkervicerCmpQueryResponse(SKServicer servicer) {
		super();
		this.servicer = servicer;
	}




	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		resp.put("ret", APICode.SUCCESS);
		JSONObject serJSON = new JSONObject();
		resp.put("servicer", serJSON);
		JSONFormat.populateUserData(serJSON, servicer);
		JSONFormat.populateServicerData(serJSON, servicer);
		JSONFormat.populateServicerCmpData(serJSON, servicer);
		JSONFormat.populateEasemobData(serJSON, servicer);
		
		return resp;
	}

}
