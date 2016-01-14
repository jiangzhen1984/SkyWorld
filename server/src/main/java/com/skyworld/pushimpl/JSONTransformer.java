package com.skyworld.pushimpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.skyworld.push.HttpPushMessageTransformer;
import com.skyworld.push.msg.HttpPushMessage;

public class JSONTransformer implements HttpPushMessageTransformer<HttpPushMessage> {

	
	private Map<Class<? extends HttpPushMessage>, HttpPushMessageTransformer<HttpPushMessage>> mapping;
	
	
	
	
	public JSONTransformer() {
		super();
		mapping = new HashMap<Class<? extends HttpPushMessage>, HttpPushMessageTransformer<HttpPushMessage>>();
		mapping.put(QuestionMessage.class, new QuestionMessageJSONTransformer());
		mapping.put(AnswerMessage.class, new AnswerMessageJSONTransformer());
		mapping.put(EasemobMessage.class, new EasemobMessageJSONTransformer());
	}

	@Override
	public HttpPushMessage unserialize(InputStream in) throws IOException {
		return null;
	}

	@Override
	public String serialize(HttpPushMessage message) {
		return mapping.get(message.getClass()).serialize(message);
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

}
