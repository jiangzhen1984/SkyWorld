package com.netease.nim.uikit.sam.userinfo;

import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

public class SamUserInfo implements UserInfoProvider.UserInfo{
	String account;
	String avatar;
	String name;

	public SamUserInfo(String account, String avatar, String name){
		this.account = account;
		this.avatar = avatar;
		this.name = name;
	}

	public String getAccount(){
		return account;
	}

	public String getAvatar(){
		return avatar;
	}

	public String getName(){
		return name;
	}
}