package com.android.samservice;

import com.android.samservice.info.SendAnswer;

public class SendaCoreObj extends SamCoreObj{

	public SendAnswer sda;
	
	public SendaCoreObj(CBObj obj, SendAnswer sda){
		refCBObj = obj;
		request_status = STATUS_INIT;
		this.sda = sda;
	}
}

