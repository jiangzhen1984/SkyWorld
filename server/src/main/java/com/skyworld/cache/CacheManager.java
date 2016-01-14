package com.skyworld.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.skyworld.service.dsf.Customer;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;

public class CacheManager {
	
	private ConcurrentHashMap<Token, User> mUserCache;
	private ConcurrentHashMap<Long, Token> mUserIdCache;
	
	private ConcurrentHashMap<Token, User> mServicerCache;
	private ConcurrentHashMap<Long, Token> mServicerIDCache;
	
	
	private ConcurrentHashMap<Token, User> mCustomerCache;
	private ConcurrentHashMap<Long, Token> mCustomerIDCache;
	
	private ConcurrentHashMap<Long, Question> pendingQuestion;
	
	public static CacheManager instance;
	
	
	private CacheManager() {
		mUserCache = new ConcurrentHashMap<Token, User>();
		mUserIdCache = new ConcurrentHashMap<Long, Token>();
		
		mServicerCache = new ConcurrentHashMap<Token, User>();
		mServicerIDCache = new ConcurrentHashMap<Long, Token>();
		
		mCustomerCache = new ConcurrentHashMap<Token, User>();
		mCustomerIDCache = new ConcurrentHashMap<Long, Token>();
		
		pendingQuestion = new  ConcurrentHashMap<Long, Question>();
	}

	
	public static synchronized CacheManager getIntance() {
		if (instance == null) {
			instance = new CacheManager();
		}
		return instance;
	}
	
	
	public User getUser(Token token) {
		return mUserCache.get(token);
	}
	
	
	public Customer getCustomer(Token token) {
		return (Customer)this.mCustomerCache.get(token);
	}
	
	public SKServicer getSKServicer(Token token) {
		return (SKServicer)this.mServicerCache.get(token);
	}
	
	public Token saveUser(User user) {
		if (user == null) {
			throw new NullPointerException(" user is null");
		}
		//remove cache
		Token cacheToken = mUserIdCache.get(user.getId());
		if (cacheToken != null) {
			mUserCache.remove(cacheToken);
			mServicerCache.remove(cacheToken);
			mCustomerCache.remove(cacheToken);
		}
		
		Token token = TokenFactory.generateStringToken();
		user.setToken(token);
		mUserCache.put(token, user);
		mUserIdCache.put(user.getId(), token);
		
		if (user.getUserType() == UserType.SERVICER) {
			mServicerCache.put(token, user);
			mServicerIDCache.put(user.getId(), token);
		} else if (user.getUserType() == UserType.CUSTOMER) {
			mCustomerCache.put(token, user);
			mCustomerIDCache.put(user.getId(), token);
		}
		
		return token;
	}
	
	
	public User removeUser(Token token) {
		if (token == null) {
			throw new NullPointerException(" token is null");
		}
		
		User user = mUserCache.remove(token);
		if (user != null) {
			mUserIdCache.remove(user.getId());
			if (user.getUserType() == UserType.SERVICER) {
				mServicerCache.remove(token);
				mServicerIDCache.remove(user.getId());
			}else if (user.getUserType() == UserType.CUSTOMER) {
				mCustomerCache.remove(token);
				mCustomerIDCache.remove(user.getId());
			}
		}
		return user;
	}
	
	
	/**
	 * FIXME optimize cache management
	 * @return
	 */
	public Collection<User> getAllServicer() {
		return mServicerCache.values();
	}
	
	
	public void addPendingQuestion(Question q) {
		pendingQuestion.put(q.getId(), q);
	}
	
	
	public Question getPendingQuestion(long id) {
		return pendingQuestion.get(id);
	}
	
	public Question removePendingQuestion(long id) {
		return pendingQuestion.remove(id);
	}
}
