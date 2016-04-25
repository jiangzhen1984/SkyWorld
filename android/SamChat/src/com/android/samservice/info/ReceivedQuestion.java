package com.android.samservice.info;

import java.io.Serializable;

/*
		id(primary) |question_id | question |contact user id | status | response| received time | canceled time | receivercellphone | receiverusername 
	*/
public class ReceivedQuestion implements Serializable{
	public static final int CANCEL = 0;
	public static final int ACTIVE = 1;

	public static final int NOT_RESPONSED = 0;
	public static final int RESPONSED = 1;
	
	public long id;
	public String question_id;
	public String question;
	public long contactuserid;
	public int status;
	public int response;
	public long receivedtime;
	public long canceledtime;
	public String receivercellphone;
	public String receiverusername;


	public void ReceivedQuestion(){
		id = 0;
		question_id = null;
		question = null;
		contactuserid = 0;
		status = CANCEL;
		response = NOT_RESPONSED;
		receivedtime = 0;
		canceledtime = 0;
		receivercellphone = null;
		receiverusername = null;
	}

	public long getid(){
		return id;
	}
	public void setid(long id){
		this.id = id;
	}

	public String getquestion_id(){
		return question_id;
	}

	public void setquestion_id(String question_id){
		this.question_id = question_id;
	}

	public String getquestion(){
		return question;
	}

	public void setquestion(String question){
		this.question = question;
	}

	public long getcontactuserid(){
		return contactuserid;
	}

	public void setcontactuserid(long contactuserid){
		this.contactuserid = contactuserid;
	}

	public int getstatus(){
		return status;
	}

	public void setstatus(int status){
		this.status = status;
	}

	public int getresponse(){
		return response;
	}

	public void setresponse(int response){
		this.response = response;
	}

	public long getreceivedtime(){
		return receivedtime;
	}

	public void setreceivedtime(long receivedtime){
		this.receivedtime = receivedtime;
	}

	public long getcanceledtime(){
		return canceledtime;
	}

	public void setcanceledtime(long canceledtime){
		this.canceledtime = canceledtime;
	}

	public String getreceivercellphone(){
		return receivercellphone;
	}

	public void setreceivercellphone(String receivercellphone){
		this.receivercellphone = receivercellphone;
	}

	public String getreceiverusername(){
		return receiverusername;
	}

	public void setreceiverusername(String receiverusername){
		this.receiverusername = receiverusername;
	}

	
	
}