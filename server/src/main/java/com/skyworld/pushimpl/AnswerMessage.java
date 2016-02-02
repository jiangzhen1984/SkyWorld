package com.skyworld.pushimpl;

import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.service.dsf.Answer;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.SKServicer;

public class AnswerMessage extends HttpPushMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7948225066643830391L;

	public static final int AM_TYPE = 1;

	private Question question;
	
	private Answer ans;
	
	private SKServicer servicer;
	
	

	public AnswerMessage(Question question,Answer ans, SKServicer servicer) {
		super();
		this.question = question;
		this.ans = ans;
		this.servicer = servicer;
		this.type = AM_TYPE;
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
