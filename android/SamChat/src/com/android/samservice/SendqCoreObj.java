package com.android.samservice;

public class SendqCoreObj extends SamCoreObj{

	public String question_id;

	
	public SendqCoreObj(CBObj cbobj){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		question_id=null;
	}
}
