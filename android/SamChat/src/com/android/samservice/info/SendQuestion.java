package com.android.samservice.info;

/*
	id(primary) |question_id | send user id | question | status | send time | cancel time | sendercellphone | senderusername
	*/
public class SendQuestion
{
	public static final int CANCEL = 0;
	public static final int ACTIVE = 1;
	
	public long id;
	public String question_id;
	public long senduserid;
	public String question;
	public int status;
	public long sendtime;
	public long canceltime;
	public String sendercellphone;
	public String senderusername;

	public SendQuestion(long senduserid,String question_id, String question){
		this.id = 0;
		this.question_id = question_id;
		this.senduserid = senduserid;
		this.question = question;
		this.status = ACTIVE ;
		this.sendtime = System.currentTimeMillis();
		this.canceltime = 0;
		this.sendercellphone = null;
		this.senderusername = null;
	}

	public SendQuestion(){
		this.id = 0;
		this.question_id = null;
		this.senduserid = 0;
		this.question = null;
		this.status = CANCEL ;
		this.sendtime = 0;
		this.canceltime = 0;
		this.sendercellphone = null;
		this.senderusername = null;
	}

	public void setsendercellphone(String sendercellphone){
		this.sendercellphone = sendercellphone;
	}

	public void setsenderusername(String senderusername){
		this.senderusername = senderusername; 
	}
	
}