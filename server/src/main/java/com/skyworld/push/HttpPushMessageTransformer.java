package com.skyworld.push;

import java.io.IOException;
import java.io.InputStream;

import com.skyworld.push.msg.HttpPushMessage;

public interface HttpPushMessageTransformer<T extends HttpPushMessage> {
	
	
	
	public HttpPushMessage unserialize(InputStream in) throws IOException;
	
	
	
	public String serialize(T message);
	
	public String getContentType();
	

}
