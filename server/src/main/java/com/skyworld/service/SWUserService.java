package com.skyworld.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.cache.CacheManager;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.service.po.SWPUser;
import com.skyworld.service.po.SWPUserAvatar;


public class SWUserService extends BaseService {
	
	
	public User getUser(long uid) {
		User user = CacheManager.getIntance().getUser(uid);
		if (user == null) {
			user = selectUser(uid);
			CacheManager.getIntance().putUser(uid, user);
		}
		return user;
	}

	
	public User selectUser(long uid) {
		Session session = openSession();
		User u = (User)session.load(User.class, uid);
		User user = new User(u);
		user.setAvatar(queryAvatar(user, session));
		session.close();
		return user;
	}
	
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
	
	
	public User selectUser(String phone, String mail, boolean queryAvatar) {
		Session session = openSession();
		Query query = session.createQuery(" from SWPUser where (cellPhone=? or mail =?) ");
		query.setString(0, phone);
		query.setString(1, mail);
		List<User> list = (List<User>)query.list();
		User user = null;
		if (list.size() > 0) {
			user = new User((SWPUser)list.get(0));
			if (queryAvatar) {
				user.setAvatar(queryAvatar(user, session));
			}
		}
		session.close();
		return user;
	}
	
	
	public List<User> selectUserList(String[] phones, String[] mails) {
		if (phones == null || mails == null || phones.length <= 0 || mails.length ==0) {
			return null;
		}
		if (phones.length != mails.length) {
			throw new IllegalArgumentException("phone lenght : "+ phones.length +"  doesn't match with mails length" + mails.length);
		}
		StringBuffer condition = new StringBuffer();
		condition.append("(");
		condition.append(" u.CELL_PHONE=?");
		condition.append(" or u.MAIL=? ");
		
		for (int i = 1; i < phones.length; i++) {
			condition.append(" or u.CELL_PHONE=?");
			condition.append(" or u.MAIL=? ");
		}
		condition.append(")");
		
		Session session = openSession();
		Query query = session.createSQLQuery(" select  u.id as id , CELL_PHONE , USER_PWD , NAME,ADDRESS  , MAIL,  U_TYPE, a.id as aid , LAST_UPDATE , origin_path from SW_USER as u   left join SW_USER_AVATAR as a on u.avatar_id = a.id  where  " + condition.toString() );
		for (int i = 0; i < phones.length; i++) {
			query.setString(2 * i , phones[i]);
			query.setString(2 * i +1 , mails[i]);
		}
		
		List<Object[]> queryList = query.list();
		List<User> list = new ArrayList<User>(queryList.size());
		
		Iterator<Object[]> it = queryList.iterator();
		while(it.hasNext()) {
			Object[] obj = it.next();
			
			User user = new User();
			
			
//			index 0 long id;
//			index 1 String cellPhone;
//			index 2 String password;
//			index 3 String name;
//			index 4 String address;
//			index 5 String mail;
//			index 6 int uType;
//			index 7 long avatarId;
//			index 8 long lastUpdate;
//			index 9 long avatarId;
//			index 10 String originPath;
			
			
			user.setId(((BigInteger)obj[0]).longValue());
			user.setCellPhone((String)obj[1]);
			user.setName((String)obj[2]);
			user.setAddress((String)obj[4]);
			user.setMail((String)obj[5]);
			user.setAvatarId(((BigInteger)obj[7]).longValue());
			user.setLastUpdate(((BigDecimal)obj[8]).longValue());
			int type = ((BigDecimal)obj[6]).intValue();
			switch(type) {
			case 0:
				user.setUserType(UserType.CUSTOMER);
				break;
			case 1:
				user.setUserType(UserType.SERVICER);
				break;
			case 2:
				user.setUserType(UserType.GROUP);
				break;
			}
			
			if (user.getAvatarId() > 0) {
				SWPUserAvatar sa = new SWPUserAvatar();
				sa.setId(user.getAvatarId());
				sa.setOriginPath((String)obj[9]);
				user.setAvatar(sa);
			}
			
			list.add(user);
		}
		
		session.close();
		return list;
	}
	
	
	public int addUser(User user) {
		User u = selectUser(user.getCellPhone(), user.getMail(), false);
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
		esu.setLastUpdate(System.currentTimeMillis());
	
		
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
		user.setLastUpdate(esu.getLastUpdate());
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
			cache.setLastUpdate(System.currentTimeMillis());
			Transaction t = session.beginTransaction();
			session.update(cache);
			t.commit();
			servicer.setLastUpdate(cache.getLastUpdate());
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
