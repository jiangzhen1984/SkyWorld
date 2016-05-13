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
import com.skyworld.service.po.SWPRelationship;
import com.skyworld.service.po.SWPServicerDesc;
import com.skyworld.service.po.SWPUser;
import com.skyworld.service.po.SWPUserAvatar;


public class SWUserService extends BaseService {
	
	
	public User getUser(long uid) {
		return getUser(uid, null);
	}
	
	
	public User getUser(long uid, Session sess) {
		User user = CacheManager.getIntance().getUser(uid);
		if (user == null) {
			user = selectUser(uid, sess);
			if (user == null) {
				return null;
			}
			CacheManager.getIntance().putUser(uid, user);
		}
		return user;
	}

	
	public User selectUser(long uid) {
		Session session = openSession();
		User user = selectUser(uid, session);
		session.close();
		return user;
	}
	
	
	public User selectUser(long uid, Session sess) {
		SWPUser u = (SWPUser)sess.get(SWPUser.class, uid);
		if (u == null) {
			return null;
		}
		User user = null;
		//servicer
		if (u.getuType() == 1) {
			user = new SKServicer(u);
			this.populateServicer((SKServicer)user);
		} else {
			user = new User(u);
		}
		user.setAvatar(queryAvatar(user, sess));
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
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append(" select  u.id as id , CELL_PHONE , USER_PWD , NAME,ADDRESS  , MAIL,  U_TYPE, a.id as aid , LAST_UPDATE , a.origin_path, ");
		sqlBuf.append(" sd.SER_DESC, sd.area, sd.location  ");
		sqlBuf.append(" from SW_USER as u  " );
		sqlBuf.append(" left join SW_USER_AVATAR as a on u.avatar_id = a.id  ");
		sqlBuf.append(" left join SW_SERVICER_DESC as sd on sd.SER_ID = a.id  ");
		sqlBuf.append("where  " + condition.toString() );
		
		Query query = session.createSQLQuery(sqlBuf.toString() );
		for (int i = 0; i < phones.length; i++) {
			query.setString(2 * i , phones[i]);
			query.setString(2 * i +1 , mails[i]);
		}
		
		List<Object[]> queryList = query.list();
		List<User> list = new ArrayList<User>(queryList.size());
		
		Iterator<Object[]> it = queryList.iterator();
		while(it.hasNext()) {
			Object[] obj = it.next();
			
			User user = null;
			int type = ((BigDecimal)obj[6]).intValue();
			switch(type) {
			case 0:
				user = new User();
				user.setUserType(UserType.CUSTOMER);
				break;
			case 1:
				user = new SKServicer();
				user.setUserType(UserType.SERVICER);
				break;
			case 2:
				user = new User();
				user.setUserType(UserType.GROUP);
				break;
			}
			
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
//			index 11 String servicer desc;
//			index 12 String area;
//			index 13 String location;
			
			
			user.setId(((BigInteger)obj[0]).longValue());
			user.setCellPhone((String)obj[1]);
			user.setName((String)obj[3]);
			user.setAddress((String)obj[4]);
			user.setMail((String)obj[5]);
			if (obj[7] != null) {
				user.setAvatarId(((BigInteger)obj[7]).longValue());
			}
			user.setLastUpdate(((BigDecimal)obj[8]).longValue());
			
			
			if (user.getAvatarId() > 0) {
				SWPUserAvatar sa = new SWPUserAvatar();
				sa.setId(user.getAvatarId());
				sa.setOriginPath((String)obj[9]);
				user.setAvatar(sa);
			}
			
			
			if (user.getUserType() == UserType.SERVICER) {
				SKServicer sks = (SKServicer)user;
				sks.setServiceDesc((String)obj[10]);
				sks.setArea((String)obj[11]);
				sks.setLocation((String)obj[12]);
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
		//put to cache
		CacheManager.getIntance().putUser(user.getId(), user);
		user.setLastUpdate(esu.getLastUpdate());
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
	

	public boolean populateServicer(SKServicer servicer) {
		if (servicer == null) {
			return false;
		}
		Session session = openSession();
		Query query = session.createQuery(" from SWPServicerDesc where servicer.id = ?");
		query.setLong(0, servicer.getId());
		List<SWPServicerDesc> list = (List<SWPServicerDesc>)query.list();
		//--xcl 添加
		session.close();
		//--
		if (list.size() > 0) {
			SWPServicerDesc desc = list.get(0);
			servicer.setArea(desc.getArea());
			servicer.setLocation(desc.getLocation());
			servicer.setServiceDesc(desc.getDesc());
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean updradeUserToSKServicer(SKServicer servicer) {
		if (servicer == null) {
			throw new NullPointerException("user is null");
		}
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
			
			
			SWPServicerDesc swsd = new SWPServicerDesc();
			swsd.setArea(servicer.getArea());
			swsd.setDesc(servicer.getServiceDesc());
			swsd.setLocation(servicer.getLocation());
			swsd.setServicer(cache);
			session.save(swsd);
			
			t.commit();
			servicer.setLastUpdate(cache.getLastUpdate());
		}
		
		if (servicer.getAvatar() == null) {
			servicer.setAvatar(queryAvatar(servicer, session));
		}
		session.close();
		
		CacheManager.getIntance().putUser(servicer.getId(), servicer);
		return true;
	}
	
	
	
	public SWPUserAvatar updateUserAvatar(User user) {
		if (user == null) {
			throw new NullPointerException("user is null");
		}
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
	
	
	
	public boolean queryRelation(User user1, User user2, boolean both) {
		if (user1 == null || user2 == null) {
			return false;
		}
		boolean ret = false;
		Session sess = openSession();
		StringBuffer buf = new StringBuffer();
		buf.append("select count(*) from SWPRelationship where (userId1 = ? and userId2 =?)  ");
		if (both) {
			buf.append("  or (userId2 = ? and userId1 =?) " );
		}
		Query q = sess.createQuery(buf.toString());
		q.setLong(0, user1.getId());
		q.setLong(1, user2.getId());
		if (both) {
			q.setLong(2, user1.getId());
			q.setLong(3, user2.getId());
		}
		int count = q.list().size();
		if (count == 0) {
			ret = false;
		} else {
			count = ((Long)q.list().iterator().next()).intValue();
			if (both) {
				ret = count == 2;
			} else {
				ret = count == 1;
			}
		}
		sess.close();
		return ret;
	}
	
	
	public void makeRelation(User user1, User user2, boolean both) {
		if (user1 == null || user2 == null) {
			throw new NullPointerException("user is null");
		}
		//FIXME should check exist first
		SWPRelationship ship1 = new SWPRelationship();
		ship1.setUserId1(user1.getId());
		ship1.setUserId2(user2.getId());
		
		Session session = openSession();
		Transaction t = session.beginTransaction();
		session.save(ship1);
		if (both) {
			SWPRelationship ship2 = new SWPRelationship();
			ship2.setUserId1(user2.getId());
			ship2.setUserId2(user1.getId());
			session.save(ship2);
		}
		t.commit();
		session.close();
		
		user1.addFriend(user2);
		if (both) {
			user2.addFriend(user1);
		}
	}
	
	
	
	public void removeRelation(User user1, User user2, boolean both) {
		if (user1 == null || user2 == null) {
			throw new NullPointerException("user is null");
		}

		Session session = openSession();
		Transaction t = session.beginTransaction();
		Query query = session.createQuery(" delete from SWPRelationship where (userId1 = ? and userId2 =? )"+(both ? " or (userId1 = ? and userId2 =?)  " : ""));
		query.setLong(0, user1.getId());
		query.setLong(1, user2.getId());
		if (both) {
			query.setLong(2, user2.getId());
			query.setLong(3, user1.getId());
		}
		query.executeUpdate();
		t.commit();
		session.close();
		
		user1.removeFriend(user2);
		if (both) {
			user2.removeFriend(user1);
		}
	}
	
	
	public void queryUserRelation(User user) {
		if (user == null) {
			throw new NullPointerException("user is null");
		}
		Session session = openSession();
		Query query = session.createQuery(" from SWPRelationship r  where userId1 = ?");
		query.setLong(0, user.getId());
		List<SWPRelationship> list = query.list();
		Iterator<SWPRelationship> it = list.iterator();
		while(it.hasNext()) {
			user.addFriend(getUser(it.next().getUserId2(), session));
		}
		session.close();
		user.setRelationQueryFlag(true);
	}
	
	
	public List<User> queryUserRelationReverse(User user) {
		if (user == null) {
			throw new NullPointerException("user is null");
		}
		Session session = openSession();
		Query query = session.createQuery(" from SWPRelationship r  where userId2 = ?");
		query.setLong(0, user.getId());
		List<SWPRelationship> list = query.list();
		List<User> fans = new ArrayList<User>(list.size());
		Iterator<SWPRelationship> it = list.iterator();
		while(it.hasNext()) {
			fans.add(getUser(it.next().getUserId1(), session));
		}
		session.close();
		return fans;
	}


}
