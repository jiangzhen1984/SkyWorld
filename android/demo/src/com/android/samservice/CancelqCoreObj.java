package com.android.samservice;

public class CancelqCoreObj extends SamCoreObj{

	public String question_id;

	public CancelqCoreObj(CBObj cbobj, String question_id){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.question_id = question_id;
	}
}
