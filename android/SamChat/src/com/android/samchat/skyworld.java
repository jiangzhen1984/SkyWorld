package com.android.samchat;

import com.easemob.chat.EMChat;
import com.easemob.easeui.controller.EaseUI;

import android.app.Application;

public class skyworld extends Application {
	static final String TAG = "Application";
	public skyworld() {
		
	}
	
	@Override
	public void onCreate() {
		
		EMChat.getInstance().setAutoLogin(false);
		//EMChat.getInstance().init(this.getApplicationContext());
		EMChat.getInstance().setDebugMode(true);
		EaseUI.getInstance().init(this);
	}
	
	

}
