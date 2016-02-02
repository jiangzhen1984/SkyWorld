package com.skyworld.pushimpl;

import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.service.dsf.Question;

public class QuestionMessage extends HttpPushMessage {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2514023169079326280L;

	public static final int QM_TYPE = 3;

	private Question question;

	public QuestionMessage(Question question) {
		super();
		this.question = question;
		this.type = QM_TYPE;
	}



	public Question getQuestion() {
		return question;
	}

	
	
	
	
}
