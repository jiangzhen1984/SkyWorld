package com.skyworld.pushimpl;

import org.json.JSONObject;

import com.skyworld.init.GlobalConstants;



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
		
		asker.put("id", qm.getQuestion().getAsker().getId());
		asker.put("cellphone", qm.getQuestion().getAsker().getCellPhone());
		asker.put("username", qm.getQuestion().getAsker().getMail());
		
		JSONObject easemob = new JSONObject();
		asker.put("easemob", easemob);
		easemob.put("username", qm.getQuestion().getAsker().getCellPhone());
		
		if (qm.getQuestion().getAsker().getAvatar() != null) {
			JSONObject avatar = new JSONObject();
			asker.put("avatar", avatar);
			avatar.put("origin", GlobalConstants.AVATAR_HOST+qm.getQuestion().getAsker().getAvatarPath());
		}
		
		return root;
	}


	
	

}
