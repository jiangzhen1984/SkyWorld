package com.android.samservice.info;

import java.io.Serializable;

/*
	id(primary) |question_id | answer |status | loginuserid | sendtime 
*/
public class SendAnswer implements Serializable
{
	public static int SEND_ING = 0;
	public static int SEND_SUCCEED = 1;
	public static int SEND_FAILED= 2;
	
	
	public long id;
	public String question_id;
	public String answer;
	public int status;
	public long loginuserid;
	public long sendtime;

	public SendAnswer(){
		id = 0;
		question_id = null;
		answer = null;
		status = SEND_ING;
		loginuserid = 0;
		sendtime = System.currentTimeMillis();
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

	public void setstatus(int status){
		this.status = status;
	}
	public int getstatus(){
		return this.status;
	}

	public void setloginuserid(long loginuserid){
		this.loginuserid = loginuserid;
	}
	public long getloginuserid(){
		return this.loginuserid;
	}

	public void setsendtime(long sendtime){
		this.sendtime = sendtime;
	}
	public long getsendtime(){
		return this.sendtime;
	}

}