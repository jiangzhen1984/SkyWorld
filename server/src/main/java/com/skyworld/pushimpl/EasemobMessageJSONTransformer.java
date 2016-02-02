package com.skyworld.pushimpl;

import org.json.JSONObject;



public class EasemobMessageJSONTransformer extends BaseJSONTransformer<EasemobMessage>  {

	
	@Override
	protected JSONObject transform(JSONObject root, EasemobMessage t) {
		EasemobMessage em = t;
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("category", "easemob");
		
			
		body.put("id",  em.getUser().getId());
		body.put("cellphone", em.getUser().getCellPhone());
		body.put("easemob_username", em.getUser().getCellPhone());
		return root;
	}


	
	
}
