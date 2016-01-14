package com.skyworld.push.event;

import com.skyworld.push.msg.HttpPushMessage;

public class MessageEvent extends SHPEvent{

	protected HttpPushMessage message;
	public MessageEvent(HttpPushMessage message) {
		super(Type.POST_MESSAGE);
		this.message = message;
	}
	
	
	public HttpPushMessage getMessage() {
		return this.message;
	}
}
