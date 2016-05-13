package com.android.samservice.info;

/*
	id|receiver_name|sender_name|question_id
*/
public class RespQuest{
	public long id;
	public String receiver_name;
	public String sender_name;
	public String question_id;

	public RespQuest(){
		id = 0;
		receiver_name = null;
		sender_name = null;
		question_id = null;
	}

	public RespQuest(String receiver_name,String sender_name,String question_id){
		this.id = 0;
		this.receiver_name = receiver_name;
		this.sender_name = sender_name;
		this.question_id = question_id;
	}
}