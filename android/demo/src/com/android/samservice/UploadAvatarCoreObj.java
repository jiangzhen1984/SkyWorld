package com.android.samservice;

public class UploadAvatarCoreObj extends SamCoreObj{

	public String filePath;
	
	public UploadAvatarCoreObj(CBObj cbobj,String filePath){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.filePath = filePath;
	}
}

