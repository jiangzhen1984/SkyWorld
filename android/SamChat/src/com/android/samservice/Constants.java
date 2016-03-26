package com.android.samservice;

import com.easemob.easeui.EaseConstant;

public class Constants extends EaseConstant{
	/*
		true: server use username to be easemob id
		false: server use cellphone to be easemob id
	*/
	public static final boolean USERNAME_EQUAL_EASEMOB_ID = true;
	public static final String COUNTRY_CODE = "country_code";
	public static final String CELLPHONE_NUMBER = "cellphone";

	public static final String ACTION_FOLLOWER_CHANAGED = "action_follower_changed";

	
	public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";
	public static final String ACCOUNT_CONFLICT = "conflict";
	public static final String ACCOUNT_REMOVED = "account_removed";
	
}

