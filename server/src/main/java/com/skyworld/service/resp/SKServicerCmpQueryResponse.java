package com.skyworld.service.resp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.SKServicer.SKServicerCMPItem;
import com.skyworld.utils.JSONFormat;

public class SKServicerCmpQueryResponse extends JSONBasicResponse {
	
	
	private SKServicer servicer;
	private List<SKServicerCMPItem> items;
	
	

	public SKServicerCmpQueryResponse(SKServicer servicer) {
		super();
		this.servicer = servicer;
	}

	
	public SKServicerCmpQueryResponse(SKServicer servicer, List<SKServicerCMPItem> items) {
		super();
		this.servicer = servicer;
		this.items = items;
	}
	
	public SKServicerCmpQueryResponse(SKServicer servicer, SKServicerCMPItem item) {
		super();
		this.servicer = servicer;
		if (item != null) {
			this.items = new ArrayList<SKServicerCMPItem>(1);
		}
		this.items.add(item);
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
		if (items != null) {
			JSONFormat.populateServicerCmpItemData(serJSON, items);
		}
		
		return resp;
	}

}
