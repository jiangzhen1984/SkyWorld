package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.push.event.MessageEvent;
import com.skyworld.pushimpl.AnswerMessage;
import com.skyworld.service.dsf.Answer;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIAnswerService extends APIBasicJsonApiService {

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");
		

		if (!header.has("token")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}


		
		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		Token token = TokenFactory.valueOf(tokenId);
		JSONObject body = json.getJSONObject("body");
		
		
		if (!body.has("answer") || !body.has("question_id")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		String	ans = body.getString("answer");
		if (ans == null || ans.isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		long	questId = body.getLong("question_id");
	
		
		SKServicer servicer = CacheManager.getIntance().getSKServicer(token);
		if (servicer == null) {
			return new RTCodeResponse(APICode.ANSWER_ERROR_NOT_SERVICER);
		}
		
		Question quest = CacheManager.getIntance().getPendingQuestion(questId);
		if (quest == null) {
			return new RTCodeResponse(APICode.ANSWER_ERROR_NO_SUCH_QUESTION);
		}
		Answer  answer = new Answer(ans);
		quest.setAnswer(servicer, answer);
		
		if (quest.getAsker().getPushTerminal() != null) {
			quest.getAsker().getPushTerminal().postEvent(new MessageEvent(new AnswerMessage(quest, answer, servicer)));
		} else {
			log.error("[ERROR] No push terminal : " + quest.getAsker());
		}
		
		return new RTCodeResponse(APICode.SUCCESS);
		
		
	}

}
