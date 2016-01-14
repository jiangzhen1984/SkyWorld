package com.skyworld.service;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.InquireResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIInquireService extends APIBasicJsonApiService {

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
		// FIXME check token legal

		JSONObject body = json.getJSONObject("body");

		if (!body.has("opt")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		int opt = body.getInt("opt");
		switch (opt) {
		case 1:
			return handleNewInquire(body, token);
		case 2:
			return cancelInquire(body, token);
		case 3:
			return finishInquire(body, token);
		default:
			return new RTCodeResponse(APICode.INQUIRE_ERROR_OPT_UNSUPPORTED);
		}
	}

	private BasicResponse handleNewInquire(JSONObject body, Token token) {
		User user = CacheManager.getIntance().getUser(token);
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		if (!body.has("question")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		String quest = body.getString("question");
		Question question = new Question(user, quest);
		int ret = ServiceFactory.getQuestionService().saveQuestion(user, question);
		CacheManager.getIntance().addPendingQuestion(question);
		if (ret == 0) {
			ServiceFactory.getQuestionService().broadcastQuestion(question);
			return new InquireResponse(user, question);
		} else {
			return new RTCodeResponse(APICode.INQUIRE_ERROR_INTERNAL_ERROR);
		}
	}

	private BasicResponse cancelInquire(JSONObject body, Token token) {
		if (!body.has("question_id")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		long id = body.getLong("question_id");
		
		Question cancelQuest = CacheManager.getIntance().getPendingQuestion(id);
		if (cancelQuest == null) {
			return new RTCodeResponse(APICode.INQUIRE_ERROR_SUCH_QUESTION);
		}
		ServiceFactory.getQuestionService().cancelQuestion(cancelQuest);
		CacheManager.getIntance().removePendingQuestion(id);
		ServiceFactory.getQuestionService().broadcastQuestion(cancelQuest);
		return new InquireResponse(null, cancelQuest);
	}

	private BasicResponse finishInquire(JSONObject body, Token token) {
		if (!body.has("question_id")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		long id = body.getLong("question_id");
		Question finishQuest = CacheManager.getIntance().getPendingQuestion(id);
		if (finishQuest == null) {
			return new RTCodeResponse(APICode.INQUIRE_ERROR_SUCH_QUESTION);
		}
		ServiceFactory.getQuestionService().finishQuestion(finishQuest);
		CacheManager.getIntance().removePendingQuestion(id);
		return new InquireResponse(null, finishQuest);
	}

}
