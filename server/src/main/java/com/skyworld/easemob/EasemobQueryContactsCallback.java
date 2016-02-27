package com.skyworld.easemob;

import java.util.List;

public interface EasemobQueryContactsCallback {
	
	public void onError(int code);
	
	public void onCompleted(List<EasemobUser> contacts);
}
