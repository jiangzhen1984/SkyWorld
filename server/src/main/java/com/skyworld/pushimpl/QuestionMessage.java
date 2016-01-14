package com.skyworld.pushimpl;

import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.service.dsf.Question;

public class QuestionMessage extends HttpPushMessage {

	private Question question;

	public QuestionMessage(Question question) {
		super();
		this.question = question;
	}



	public Question getQuestion() {
		return question;
	}

	
	
	
	
}
