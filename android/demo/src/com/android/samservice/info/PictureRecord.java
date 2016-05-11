package com.android.samservice.info;

import java.io.Serializable;

/*
		id(primary) |fg_id | thumbnail_pic | original_pic | url_thumbnail | url_original | sequence
		*/
public class PictureRecord implements Serializable
{
	public long id;
	public long fg_id;
	public String thumbnail_pic;
	public String original_pic;
	public String url_thumbnail;
	public String url_original;
	public int sequence;

	public PictureRecord(){
		this.id = 0;
		this.fg_id = 0;
		this.thumbnail_pic = null;
		this.original_pic = null;
		this.url_thumbnail = null;
		this.url_original = null;
		this.sequence = 0;
	}

	public PictureRecord(long fg_id, String url_thumbnail,String url_original){
		this.id = 0;
		this.fg_id = fg_id;
		this.thumbnail_pic = null;
		this.original_pic = null;
		this.url_thumbnail = url_thumbnail;
		this.url_original = url_original;
		this.sequence = 0;
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}

	public void setfg_id(long fg_id){
		this.fg_id = fg_id;
	}
	public long getfg_id(){
		return this.fg_id;
	}

	public void setthumbnail_pic(String thumbnail_pic){
		this.thumbnail_pic = thumbnail_pic;
	}
	public String getthumbnail_pic(){
		return this.thumbnail_pic;
	}

	public void setoriginal_pic(String original_pic){
		this.original_pic = original_pic;
	}
	public String getoriginal_pic(){
		return this.original_pic;
	}

	public void seturl_thumbnail(String url_thumbnail){
		this.url_thumbnail = url_thumbnail;
	}
	public String geturl_thumbnail(){
		return this.url_thumbnail;
	}

	public void seturl_original(String url_original){
		this.url_original = url_original;
	}
	public String geturl_original(){
		return this.url_original;
	}

	public void setsequence(int sequence){
		this.sequence = sequence;
	}
	public int getsequence(){
		return this.sequence;
	}
}