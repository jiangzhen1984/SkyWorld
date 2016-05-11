package com.android.samservice;

public class QuestionInfo {
	//send question info
	public String question;
	public String question_id;
	public int ret;


	public QuestionInfo(){
		this.question = null;
		this.question_id = null;
		this.ret = 0;
	}
	
	public QuestionInfo(String question){
		this.question = question;
		this.question_id = null;
		this.ret = 0;
	}
}
