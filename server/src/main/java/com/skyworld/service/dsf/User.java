package com.skyworld.service.dsf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.skyworld.cache.Token;
import com.skyworld.push.ClientTerminal;
import com.skyworld.push.event.SHPEvent;
import com.skyworld.service.po.SWPUser;
import com.skyworld.service.po.SWPUserAvatar;



public class User extends SWPUser {
	
	private Token token;
	
	
	private ClientTerminal pushTerminal;
	
	private UserType userType;
	
	private Queue<SHPEvent> pengingEvents;
	
	private Set<User> relationUser;
	
	private List<User> fansUser;
	
	private boolean relationQueryFlag;

	public User() {
		super();
		userType = UserType.CUSTOMER;
		relationUser = new HashSet<User>();
		fansUser = new ArrayList<User>();
	}
	
	public User(User u) {
		this.setAddress(u.getAddress());
		this.setCellPhone(u.getCellPhone());
		this.setName(u.getName());
		this.setId(u.getId());
		this.setMail(u.getMail());
		this.setPassword(u.getPassword());
		this.setAvatarId(u.getAvatarId());
		this.setAvatar(u.getAvatar());
		this.setCountyCode(u.getCountyCode());
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
		relationUser = new HashSet<User>();
		fansUser = new ArrayList<User>();
		
	}

	public User(SWPUser u) {
		this.setAddress(u.getAddress());
		this.setCellPhone(u.getCellPhone());
		this.setName(u.getName());
		this.setId(u.getId());
		this.setMail(u.getMail());
		this.setAvatarId(u.getAvatarId());
		this.setAvatar(u.getAvatar());
		this.setLastUpdate(u.getLastUpdate());
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
		relationUser = new HashSet<User>();
		fansUser = new ArrayList<User>();
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
	
	public String getAvatarPath() {
		if (this.getAvatar() == null) {
			return null;
		}
		return this.getAvatar().getOriginPath();
	}
	
	
	public void setAvatarPath(String path) {
		if (this.getAvatar() == null) {
			this.setAvatar(new SWPUserAvatar());
		}
		this.getAvatar().setOriginPath(path);
		this.getAvatar().setUser(this);
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
	
	
	public void addFriend(User u) {
		this.relationUser.add(u);
	}
	
	public void removeFriend(User u) {
		this.relationUser.remove(u);
	}
	
	
	public boolean isInRelation(User u) {
		return this.relationUser.contains(u);
	}
	
	public int getRelationUserCount() {
		return relationUser.size();
	}
	
	public Iterator<User> iteratorRelationUser() {
		if (relationUser != null) {
			return relationUser.iterator();
		} else {
			return null;
		}
	}
	
	
	public List<User> getRelationCopy() {
		return new ArrayList<User>(relationUser);
	}

	public boolean isRelationQueryFlag() {
		return relationQueryFlag;
	}

	public void setRelationQueryFlag(boolean relationQueryFlag) {
		this.relationQueryFlag = relationQueryFlag;
	}

	@Override
	public String toString() {
		return "User [token=" + token + ", userType=" + userType + ", id=" + id
				+ "]";
	}

	

}
