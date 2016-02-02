package com.skyworld.pushimpl;

import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.service.dsf.User;

public class EasemobMessage extends HttpPushMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3851051980097665257L;


	public static final int EM_TYPE = 2;

	
	private User user;

	public EasemobMessage(User user) {
		super();
		this.user = user;
		this.type = EM_TYPE;
	}

	public User getUser() {
		return user;
	}
	
	

	
	
}
