package com.skyworld.service.resp;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyworld.service.APICode;
import com.skyworld.service.dsf.Question;
import com.skyworld.utils.JSONFormat;

public class InquireQueryResponse extends JSONBasicResponse {
	
	
	private List<Question> ques;
	
	



	public InquireQueryResponse(List<Question> ques) {
		super();
		this.ques = ques;
	}





	@Override
	public JSONObject getResponseJSON() {
		JSONObject resp = new JSONObject();
		resp.put("ret", APICode.SUCCESS);
		resp.put("count", ques.size());
		JSONArray qarr = new JSONArray();
		if (ques != null && ques.size() > 0) {
			for (Question q : ques) {
				JSONObject jsonQ = new JSONObject();
				jsonQ.put("id", q.getId());
				jsonQ.put("qs", q.getQuestion());
				jsonQ.put("state", q.getState());
				JSONObject asker = new JSONObject();
				JSONFormat.populateUserData(asker, q.getAsker());
				jsonQ.put("asker", asker);
				qarr.put(jsonQ);
			}
		}
			
		resp.put("ques", qarr);
		return resp;
	}

}
