package com.skyworld.easemob;

import java.util.List;

public interface EaseMobService {

	public void start();

	public void shutdown();

	public boolean isAuthed();

	public void authorize(String org, String app, String clientId,
			String clientSecret);

	public void register(String username, String pwd);
	
	

	public void register(String username, String pwd,
			EasemobRegisterCallback callback);
	
	
	public List<EasemobUser> queryContacts(EasemobUser user);
	
	
	public void queryContacts(EasemobUser user, EasemobQueryContactsCallback callback);

}
