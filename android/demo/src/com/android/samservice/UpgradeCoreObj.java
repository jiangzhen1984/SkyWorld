package com.android.samservice;

import com.android.samchat.SamVendorInfo;

public class UpgradeCoreObj extends SamCoreObj{
	public SamVendorInfo vInfo;
	
	public UpgradeCoreObj(CBObj cbobj,SamVendorInfo vInfo){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.vInfo = vInfo;
	}
}
