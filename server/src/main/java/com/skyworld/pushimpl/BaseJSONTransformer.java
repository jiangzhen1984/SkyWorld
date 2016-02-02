package com.skyworld.pushimpl;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.skyworld.push.HttpPushMessageTransformer;
import com.skyworld.push.msg.HttpPushMessage;

public abstract class BaseJSONTransformer<T extends HttpPushMessage> implements HttpPushMessageTransformer<T> {

	@Override
	public T unserialize(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize(T message) {
		if (message == null) {
			throw new NullPointerException(" message is null") ;
		}
		JSONObject root = new JSONObject();
		transform(root, message);
		return root.toString();
	}
	
	
	protected abstract JSONObject transform(JSONObject root, T t);

	@Override
	public String getContentType() {
		return "application/json";
	}

	
}
