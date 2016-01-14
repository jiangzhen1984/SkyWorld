package com.android.samservice.info;

import java.io.Serializable;

/*
		id(primary) |question_id | answer |contact user id | received time 
		*/
public class ReceivedAnswer implements Serializable
{
	public long id;
	public String question_id;
	public String answer;
	public long contactuserid;
	public long receivedtime;

	public ReceivedAnswer(){
		id = 0;
		question_id = null;
		answer = null;
		contactuserid = 0;
		receivedtime = System.currentTimeMillis();
	}

	public void setid(long id){
		this.id = id;
	}
	public long getid(){
		return this.id;
	}

	public void setquestion_id(String question_id){
		this.question_id = question_id;
	}
	public String getquestion_id(){
		return this.question_id;
	}

	public void setanswer(String answer){
		this.answer = answer;
	}
	public String getanswer(){
		return this.answer;
	}

	public void setcontactuserid(long contactuserid){
		this.contactuserid = contactuserid;
	}
	public long getcontactuserid(){
		return this.contactuserid;
	}

	public void setreceivedtime(long receivedtime){
		this.receivedtime = receivedtime;
	}
	public long getreceivedtime(){
		return this.receivedtime;
	}

}