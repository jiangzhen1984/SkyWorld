package com.skyworld.pushimpl;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.skyworld.push.HttpPushMessageTransformer;
import com.skyworld.push.msg.HttpPushMessage;



public class EasemobMessageJSONTransformer implements HttpPushMessageTransformer<HttpPushMessage>  {

	@Override
	public String serialize(HttpPushMessage message) {
		EasemobMessage em = (EasemobMessage)message;
		JSONObject root = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		root.put("header", header);
		root.put("body", body);
		
		header.put("category", "easemob");
		
			
		body.put("id",  em.getUser().getId());
		body.put("cellphone", em.getUser().getCellPhone());
		body.put("easemob_username", em.getUser().getCellPhone());
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
