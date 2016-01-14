package com.android.samservice;

public class UpgradeCoreObj extends SamCoreObj{
	public UpgradeCoreObj(CBObj cbobj){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
	}
}
