package com.skyworld.pushimpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.skyworld.push.HttpPushMessageTransformer;
import com.skyworld.push.msg.HttpPushMessage;

public class JSONTransformer implements
		HttpPushMessageTransformer<HttpPushMessage> {

	private Map<Class<? extends HttpPushMessage>, HttpPushMessageTransformer<? extends HttpPushMessage>> mapping;

	public JSONTransformer() {
		super();
		mapping = new HashMap<Class<? extends HttpPushMessage>, HttpPushMessageTransformer<? extends HttpPushMessage>>();
		mapping.put(QuestionMessage.class, new QuestionMessageJSONTransformer());
		mapping.put(AnswerMessage.class, new AnswerMessageJSONTransformer());
		mapping.put(EasemobMessage.class, new EasemobMessageJSONTransformer());
	}

	@Override
	public HttpPushMessage unserialize(InputStream in) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize(HttpPushMessage message) {
		String ret;
		int type = message.getType();
		switch (type) {
		case AnswerMessage.AM_TYPE:
			ret = ((HttpPushMessageTransformer<AnswerMessage>) mapping
					.get(message.getClass()))
					.serialize((AnswerMessage) message);
			break;
		case EasemobMessage.EM_TYPE:
			ret = ((HttpPushMessageTransformer<EasemobMessage>) mapping
					.get(message.getClass()))
					.serialize((EasemobMessage) message);
			break;
		case QuestionMessage.QM_TYPE:
			ret = ((HttpPushMessageTransformer<QuestionMessage>) mapping
					.get(message.getClass()))
					.serialize((QuestionMessage) message);
			break;
		default:
			ret = "{ret : -1}";
		}
		return ret;
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

}
