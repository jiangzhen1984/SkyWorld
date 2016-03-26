package com.android.samservice;

import java.util.List;

public class FollowCoreObj extends SamCoreObj{
	public static final int FOLLOW = 0;
	public static final int UNFOLLOW = 1;
	
	public long unique_id;
	public String username;
	public int cmd;
	
	public FollowCoreObj(CBObj cbobj,long unique_id,String username,int cmd){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.unique_id= unique_id;
		this.cmd = cmd;
		this.username = username;
	}
}
