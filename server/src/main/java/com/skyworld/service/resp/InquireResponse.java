package com.skyworld.service.resp;

import org.json.JSONObject;

import com.skyworld.cache.Token;
import com.skyworld.service.APICode;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.User;

public class InquireResponse extends JSONBasicResponse {
	
	private User user;
	
	private Question ques;
	

	


	public InquireResponse(User user, Question ques) {
		super();
		this.user = user;
		this.ques = ques;
	}







	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		resp.put("ret", APICode.SUCCESS);
		resp.put("question_id", ques.getId());
		return resp;
	}





	public User getUser() {
		return user;
	}





	public void setUser(User user) {
		this.user = user;
	}





	public Question getQues() {
		return ques;
	}







	public void setQues(Question ques) {
		this.ques = ques;
	}




	
}
