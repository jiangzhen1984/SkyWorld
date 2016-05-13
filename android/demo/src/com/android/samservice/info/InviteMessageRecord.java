package com.android.samservice.info;

import java.io.Serializable;

/*
	id(primary) |sender | receiver | status | reason | time
*/
public class InviteMessageRecord implements Serializable
{
	public long id;
	public String sender;
	public String receiver;
	public int status;
	public String reason;
	public long time;

	public InviteMessageRecord(){
		this.id = 0;
		this.sender = null;
		this.receiver = null;
		this.status = InviteMessageStatus.BEINVITEED.ordinal();
		this.reason = null;
		this.time = System.currentTimeMillis();
	}

	public InviteMessageRecord(String sender,String receiver,String reason,int status){
		this.id = 0;
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.reason = reason;
		this.time = System.currentTimeMillis();;
	}

	public void setid(long id){
		this.id = id;
	}

	public long getid(){
		return this.id;
	}

	public void setsender(String sender){
		this.sender = sender;
	}

	public String getsender(){
		return this.sender;
	}

	public void setreceiver(String receiver){
		this.receiver = receiver;
	}
	
	public String getreceiver(){
		return this.receiver;
	}

	public void setstatus(int status){
		this.status = status;
	}
	public int getstatus(){
		return this.status;
	}

	public void setreason(String reason){
		this.reason = reason;
	}
	
	public String getreason(){
		return this.reason;
	}

	public void settime(long time){
		this.time = time;
	}
	public long gettime(){
		return this.time;
	}


	public enum InviteMessageStatus{
		/*sender view*/
		INVITE,
		BEAGREED,
		BEREFUSED,

		/*receiver view*/
		BEINVITEED,
		AGREED,
		REFUSED
		
	}
}