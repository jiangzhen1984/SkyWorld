package com.skyworld.pushimpl;

import org.json.JSONObject;

import com.skyworld.init.GlobalConstants;



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
		
		syservicer.put("id", qm.getServicer().getId());
		syservicer.put("cellphone", qm.getServicer().getCellPhone());
		syservicer.put("username", qm.getServicer().getMail());
		syservicer.put("area", qm.getServicer().getArea());
		syservicer.put("location", qm.getServicer().getLocation());
		syservicer.put("desc", qm.getServicer().getServiceDesc());
		
		JSONObject easemob = new JSONObject();
		syservicer.put("easemob", easemob);
		easemob.put("username", qm.getServicer().getCellPhone());
		if (qm.getServicer().getAvatar() != null) {
			JSONObject avatar = new JSONObject();
			syservicer.put("avatar", avatar);
			avatar.put("origin", GlobalConstants.AVATAR_HOST+qm.getServicer().getAvatarPath());
		}
		
		
		return root;
	}


	
	

}
