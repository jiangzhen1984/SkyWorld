package com.skyworld.pushimpl;

import org.json.JSONObject;

import com.skyworld.utils.JSONFormat;



public class QuestionMessageJSONTransformer extends  BaseJSONTransformer<QuestionMessage> {



	@Override
	protected JSONObject transform(JSONObject root, QuestionMessage t) {
		QuestionMessage qm =  t;
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("category", "question");
		
		body.put("opt", qm.getQuestion().getState().ordinal());
		body.put("quest", qm.getQuestion().getQuestion());
		body.put("quest_id", qm.getQuestion().getId());
		body.put("datetime", System.currentTimeMillis());
		
		JSONObject asker = new JSONObject();
		body.put("asker", asker);
		
		JSONFormat.populateUserData(asker, qm.getQuestion().getAsker());
		JSONFormat.populateEasemobData(asker, qm.getQuestion().getAsker());
		
		return root;
	}


	
	

}
