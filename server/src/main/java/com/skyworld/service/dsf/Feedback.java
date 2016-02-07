package com.skyworld.service.dsf;

import com.skyworld.service.po.SWPFeedback;

public class Feedback extends SWPFeedback {
	
	
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (this.user != null) {
			this.userId = user.getId();
		} else {
			this.userId = 0;
		}
	}
	
	
	

}
