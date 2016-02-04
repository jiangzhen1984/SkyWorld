package com.android.samservice;

public class ActiveQuestion{
	public String question;
	public String question_id;

	public ActiveQuestion(){
		question = null;
		question_id = null;
	}

	public ActiveQuestion(String question_id,String question){
		this.question = question;
		this.question_id = question_id;
	}
}
