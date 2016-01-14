package com.skyworld.easemob;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.cache.Token;

public class EaseMobDeamon {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	private ForkJoinPool mPool;
	
	private Configuration config;
	
	public EaseMobDeamon() {
		mPool = new ForkJoinPool();
	}

	
	public void start() {
	}
	
	
	public void shutdown() {
		mPool.shutdownNow();
	}
	
	
	public boolean isAuthed() {
		return config.isAuthed();
	}
	
	public void authorize(String org, String app, String clientId, String clientSecret) {
		config = new Configuration("https://a1.easemob.com/", org, app, clientId, clientSecret);
		mPool.execute(new AuthorizationRunnable(config));
	}
	
	
	
	public void register(String username, String pwd) {
		if (config == null) {
			log.error("Configuration doesn't initalize yet ");
			return;
		}
		if (!config.isAuthed()) {
			mPool.execute(new ChainRunnable(new Runnable[]{new AuthorizationRunnable(config), new RegisterRunnable(config, username, pwd, null)}));
		} else {
			mPool.execute(new RegisterRunnable(config, username, pwd, null));
		}
	}
	
	public void register(String username, String pwd, EasemobRegisterCallback callback) {
		if (config == null) {
			log.error("Configuration doesn't initalize yet ");
			return;
		}
		if (!config.isAuthed()) {
			mPool.execute(new ChainRunnable(new Runnable[]{new AuthorizationRunnable(config), new RegisterRunnable(config, username, pwd, callback)}));
		} else {
			mPool.execute(new RegisterRunnable(config, username, pwd, callback));
		}
	}
	
	class Configuration {
		String url;
		String org;
		String app;
		String clientId;
		String clientSecret;
		EaseModToken token;
		public Configuration(String url, String org, String app, String clientId,
				String clientSecret) {
			super();
			this.url = url;
			this.org = org;
			this.app = app;
			this.clientId = clientId;
			this.clientSecret = clientSecret;
		}
		
		
		
		public void updateToken(String token, int expires, String uuid) {
			this.token = new EaseModToken(token, expires, uuid);
		}
		
		public boolean isAuthed() {
			return (token != null && !token.isExpired()) ? true : false;
		}
		
	}
	
	
	
	class EaseModToken extends Token {
		
		
		private String val;
		private Date authDate;
		private Date exiresDate;
		private String uuid;
		
		
		public EaseModToken(String val, int exiresSec, String uuid) {
			this.val = val;
			authDate = new Date();
			exiresDate = new Date(authDate.getTime() + exiresSec * 1000);
			this.uuid = uuid;
		}
		
		public boolean isExpired() {
			return exiresDate == null ? true : System.currentTimeMillis() > exiresDate.getTime();
		}
		
		

		@Override
		public Object getValue() {
			return val;
		}
		
	}
}
