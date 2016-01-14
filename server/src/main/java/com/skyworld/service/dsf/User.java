package com.skyworld.service.dsf;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skyworld.cache.Token;
import com.skyworld.push.ClientTerminal;
import com.skyworld.push.event.SHPEvent;
import com.skyworld.service.po.SWPUser;



public class User extends SWPUser {
	
	private Token token;
	
	
	private ClientTerminal pushTerminal;
	
	private UserType userType;
	
	private Queue<SHPEvent> pengingEvents;

	public User() {
		super();
		userType = UserType.CUSTOMER;
	}
	
	public User(User u) {
		this.setAddress(u.getAddress());
		this.setCellPhone(u.getCellPhone());
		this.setName(u.getName());
		this.setId(u.getId());
		this.setMail(u.getMail());
		this.setPassword(u.getPassword());
		switch(u.getuType()) {
		case 0:
			this.userType = UserType.CUSTOMER;
			break;
		case 1:
			this.userType = UserType.SERVICER;
			break;
		case 2:
			this.userType = UserType.GROUP;
			break;
		}
		
	}

	public User(SWPUser u) {
		this.setAddress(u.getAddress());
		this.setCellPhone(u.getCellPhone());
		this.setName(u.getName());
		this.setId(u.getId());
		this.setMail(u.getMail());
		switch(u.getuType()) {
		case 0:
			this.userType = UserType.CUSTOMER;
			break;
		case 1:
			this.userType = UserType.SERVICER;
			break;
		case 2:
			this.userType = UserType.GROUP;
			break;
		}
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}


	public ClientTerminal getPushTerminal() {
		return pushTerminal;
	}

	public void setPushTerminal(ClientTerminal pushTerminal) {
		this.pushTerminal = pushTerminal;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	
	
	public synchronized void addPendingEvent(SHPEvent event) {
		if (pengingEvents ==  null) {
			pengingEvents = new LinkedBlockingQueue<SHPEvent>();
		}
		
		pengingEvents.offer(event);
	}
	
	public Queue<SHPEvent> getPendingEvents() {
		return pengingEvents;
	}

	@Override
	public String toString() {
		return "User [token=" + token + ", userType=" + userType + ", id=" + id
				+ "]";
	}
	
	
	

}
