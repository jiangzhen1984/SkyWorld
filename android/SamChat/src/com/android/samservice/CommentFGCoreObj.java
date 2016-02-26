package com.android.samservice;

import java.util.List;

public class CommentFGCoreObj extends SamCoreObj{

	public long article_id;
	public String comment;
	
	public CommentFGCoreObj(CBObj cbobj,long article_id,String comment){
		refCBObj = cbobj;
		request_status = STATUS_INIT;
		this.article_id= article_id;
		this.comment = comment;
	}
}