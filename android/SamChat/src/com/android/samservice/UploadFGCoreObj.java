package com.android.samservice;

import java.util.List;

public class UploadFGCoreObj extends SamCoreObj{

	public String comments;
	public List<String> photoes;
	
	public UploadFGCoreObj(CBObj cbobj,List<String> photoes,String comments){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.photoes= photoes;
		this.comments = comments;
	}
}