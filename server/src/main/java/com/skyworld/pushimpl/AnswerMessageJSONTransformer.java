package com.skyworld.pushimpl;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.skyworld.push.HttpPushMessageTransformer;
import com.skyworld.push.msg.HttpPushMessage;



public class AnswerMessageJSONTransformer implements HttpPushMessageTransformer<HttpPushMessage> {

	@Override
	public String serialize(HttpPushMessage message) {
		AnswerMessage qm = (AnswerMessage)message;
		JSONObject root = new JSONObject();
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
		JSONObject easemob = new JSONObject();
		syservicer.put("easemob", easemob);
		easemob.put("username", qm.getServicer().getCellPhone());
		
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
