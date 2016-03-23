package com.skyworld.pushimpl;

import org.json.JSONObject;

import com.skyworld.utils.JSONFormat;



public class AnswerMessageJSONTransformer extends BaseJSONTransformer<AnswerMessage> {

	@Override
	protected JSONObject transform(JSONObject root, AnswerMessage message) {
		AnswerMessage qm = message;
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("category", "answer");
		
		JSONObject quest = new JSONObject();
		body.put("quest", quest);
		quest.put("quest", qm.getQuestion().getQuestion());
		quest.put("quest_id", qm.getQuestion().getId());
		
		JSONObject ans = new JSONObject();
		body.put("ans", ans);
		ans.put("answer", qm.getAns().getContent());
		
		JSONObject syservicer = new JSONObject();
		body.put("syservicer", syservicer);
		
		JSONFormat.populateUserData(syservicer, qm.getServicer());
		JSONFormat.populateServicerData(syservicer, qm.getServicer());
		JSONFormat.populateEasemobData(syservicer, qm.getServicer());
		
		return root;
	}


	
	

}
