package com.skyworld.pushimpl;

import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.service.dsf.User;

public class EasemobMessage extends HttpPushMessage {

	
	private User user;

	public EasemobMessage(User user) {
		super();
		this.user = user;
	}

	public User getUser() {
		return user;
	}
	
	

	
	
}
