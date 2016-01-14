package com.skyworld.pushimpl;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.skyworld.push.HttpPushMessageTransformer;
import com.skyworld.push.msg.HttpPushMessage;



public class QuestionMessageJSONTransformer implements HttpPushMessageTransformer<HttpPushMessage>  {

	@Override
	public String serialize(HttpPushMessage message) {
		QuestionMessage qm = (QuestionMessage)message;
		JSONObject root = new JSONObject();
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
		
		return root.toString();
	}

	@Override
	public HttpPushMessage unserialize(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}
