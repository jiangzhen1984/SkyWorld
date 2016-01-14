package com.skyworld.pushimpl;

import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.service.dsf.Answer;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.SKServicer;

public class AnswerMessage extends HttpPushMessage {

	private Question question;
	
	private Answer ans;
	
	private SKServicer servicer;

	public AnswerMessage(Question question,Answer ans, SKServicer servicer) {
		super();
		this.question = question;
		this.ans = ans;
		this.servicer = servicer;
	}


	public Question getQuestion() {
		return question;
	}

	public Answer getAns() {
		return ans;
	}

	public SKServicer getServicer() {
		return servicer;
	}

	
	
	
	
}
