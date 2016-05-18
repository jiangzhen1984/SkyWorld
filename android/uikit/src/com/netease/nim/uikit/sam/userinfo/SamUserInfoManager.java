package com.netease.nim.uikit.sam.userinfo;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider.UserInfo;

public class SamUserInfoManager{
	private static SamUserInfoManager mSamUserInfoManager;
	public static synchronized SamUserInfoManager getInstance(){
		if(mSamUserInfoManager == null){
			mSamUserInfoManager	= new SamUserInfoManager();
		}

		return mSamUserInfoManager;
	}

	public UserInfo getUserInfo(String account){
		String avatar = NimUIKit.samListner.getAvatar(account);

		return new SamUserInfo(account, avatar,account);
	}
	
}