package com.netease.nim.uikit.samwraper;

/*
	id(primary) |question_id | question | status | send time | cancel time | sendercellphone | senderusername
	*/
public class SendQuestionWraper
{
	public static final int CANCEL = 0;
	public static final int ACTIVE = 1;
	
	public long id;
	public String question_id;
	public String question;
	public int status;
	public long sendtime;
	public long canceltime;
	public String sendercellphone;
	public String senderusername;

	public SendQuestionWraper(String question_id, String question){
		this.id = 0;
		this.question_id = question_id;
		this.question = question;
		this.status = ACTIVE ;
		this.sendtime = System.currentTimeMillis();
		this.canceltime = 0;
		this.sendercellphone = null;
		this.senderusername = null;
	}

	public SendQuestionWraper(){
		this.id = 0;
		this.question_id = null;
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