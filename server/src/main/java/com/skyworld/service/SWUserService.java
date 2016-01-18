package com.skyworld.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.service.po.SWPUser;
import com.skyworld.service.po.SWPUserAvatar;


public class SWUserService extends BaseService {

	
	public User selectUser(String userName, String mail, String password) {
		Session session = openSession();
		Query query = session.createQuery(" from SWPUser where (cellPhone=? or mail =?) and password=?");
		query.setString(0, userName);
		query.setString(1, mail);
		query.setString(2, password);
		List<User> list = (List<User>)query.list();
		User user = null;
		if (list.size() > 0) {
			user = new User((SWPUser)list.get(0));
			user.setAvatar(queryAvatar(user, session));
		}
		session.close();
		return user;
	}
	
	
	public User selectUser(String phone, String mail) {
		Session session = openSession();
		Query query = session.createQuery(" from SWPUser where (cellPhone=? or mail =?) ");
		query.setString(0, phone);
		query.setString(1, mail);
		List<User> list = (List<User>)query.list();
		User user = null;
		if (list.size() > 0) {
			user = new User((SWPUser)list.get(0));
		}
		session.close();
		return user;
	}
	
	public int addUser(User user) {
		User u = selectUser(user.getCellPhone(), user.getMail());
		if (u != null) {
			return -1;
		}
		Session session = openSession();
		Transaction t = session.beginTransaction();
		SWPUser esu = new SWPUser();
		esu.setCellPhone(user.getCellPhone());
		esu.setMail(user.getMail());
		esu.setName(user.getName());
		esu.setPassword(user.getPassword());
		esu.setuType(user.getUserType().ordinal());
	
		
		if (user.getAvatar() != null) {
			SWPUserAvatar swp = new SWPUserAvatar();
			swp.setUser(esu);
			swp.setOriginPath(user.getAvatarPath());
			session.save(swp);
			esu.setAvatar(swp);
			user.getAvatar().setId(esu.getId());
		}
		
		session.save(esu);
		user.setId(esu.getId());
		t.commit();
		session.close();
		return 0;
	}
	
	
	public SWPUserAvatar queryAvatar(User user, Session sess) {
		Session session = null;
		if (sess == null) {
			session = openSession();
		} else {
			session = sess;
		}
		SWPUserAvatar tmp = null;
		if (user.getAvatarId() >0) {
			SWPUserAvatar av = (SWPUserAvatar)session.get(SWPUserAvatar.class, user.getAvatarId());
			if (av != null) {
				tmp = new SWPUserAvatar(av);
			}
		}
		if (sess == null) {
			session.close();
		}
		return tmp;
	}
	

	
	
	public boolean updradeUserToSKServicer(SKServicer servicer) {
		Session session = openSession();
		Query query = session.createQuery(" from SWPUser where id = ?");
		query.setLong(0, servicer.getId());
		List<User> list = (List<User>)query.list();
		if (list.size() > 0) {
			SWPUser cache = (SWPUser)list.get(0);
			cache.setuType(UserType.SERVICER.ordinal());
			Transaction t = session.beginTransaction();
			session.update(cache);
			t.commit();
		}
		session.close();
		
		return true;
	}
	
	
	
	public SWPUserAvatar updateUserAvatar(User user) {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		if (user.getAvatarId() > 0) {
			SWPUserAvatar avatar  = (SWPUserAvatar)session.load(SWPUserAvatar.class, user.getAvatarId());
			avatar.setOriginPath(user.getAvatarPath());
			session.update(avatar);
		} else {
			session.save(user.getAvatar());
			SWPUser u  = (SWPUser)session.load(SWPUser.class, user.getId());
			u.setAvatarId(user.getAvatar().getId());
			session.update(u);
		}
		t.commit();
		session.close();
		return user.getAvatar();
	}


}
