package com.skyworld.pushimpl;

import org.json.JSONObject;

import com.skyworld.utils.JSONFormat;



public class EasemobMessageJSONTransformer extends BaseJSONTransformer<EasemobMessage>  {

	
	@Override
	protected JSONObject transform(JSONObject root, EasemobMessage t) {
		EasemobMessage em = t;
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("category", "easemob");
		
		JSONFormat.populateUserData(body, em.getUser());
		
		JSONFormat.populateEasemobData(body, em.getUser());
		
		return root;
	}


	
	
}
