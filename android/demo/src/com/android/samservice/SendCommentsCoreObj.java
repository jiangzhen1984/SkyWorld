package com.android.samservice;

public class SendCommentsCoreObj extends SamCoreObj{

	public String comments;
	
	public SendCommentsCoreObj(CBObj cbobj,String comments){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.comments = comments;
	}
}