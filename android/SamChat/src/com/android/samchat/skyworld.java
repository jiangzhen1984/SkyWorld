package com.android.samchat;

import com.android.samchat.easemobdemo.EaseMobHelper;
import com.easemob.chat.EMChat;
import com.easemob.easeui.controller.EaseUI;

import android.app.Application;
import android.content.Context;

public class skyworld extends Application {
	static final String TAG = "Application";
	static public Context appContext;
	
	public skyworld() {
		
	}
	
	@Override
	public void onCreate() {
		appContext = getApplicationContext();
		
	}

	static public void EaseMobInit(){
		EMChat.getInstance().setAutoLogin(false);
		//EMChat.getInstance().init(this.getApplicationContext());
		EMChat.getInstance().setDebugMode(false);
		if(EaseUI.getInstance().init(appContext)){
			EaseMobHelper.getInstance().init(appContext);
		}
	}
	
	

}
