package com.android.samservice;

import com.hyphenate.easeui.EaseConstant;

public class Constants extends EaseConstant{
	/*
		true: server use username to be easemob id
		false: server use cellphone to be easemob id
	*/
	public static final boolean USERNAME_EQUAL_EASEMOB_ID = true;
	public static final String COUNTRY_CODE = "country_code";
	public static final String CELLPHONE_NUMBER = "cellphone";

	public static final String SEX_SELECT = "sex_select";
	public static final String BASIC_INFO_TYPE = "basic_info_type";
	public static final String BASIC_INFO_DEFAULT_VALUE="basic_info_default_value";

	public static final String ACTION_FOLLOWER_CHANAGED = "action_follower_changed";

	
	public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";
	public static final String ACCOUNT_CONFLICT = "conflict";
	public static final String ACCOUNT_REMOVED = "account_removed";

	public static final String ACTION_AVATAR_UPDATE = "action_avatar_update";

	public static final String ACTION_QAACTIVITY_DESTROYED = "qa_activity_destroyed";
	
	public static final String GROUP_MEMBER_INFO_UPDATE="group_member_info_update";

	public static final String ACTION_NEW_MSG_FROM_SERVICE = "new_msg_from_service";
	public static final String ACTION_NEW_MSG_FROM_CHAT = "new_msg_from_chat";
	public static final String ACTION_NEW_MSG_FROM_VENDOR = "new_msg_from_vendor";

	public static final String ACTION_NEW_MSG_FROM_GROUP = "new_msg_from_group";

}

