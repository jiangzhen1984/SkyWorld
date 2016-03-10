package com.skyworld.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.service.dsf.Question;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.InquireQueryResponse;
import com.skyworld.service.resp.InquireResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIInquireService extends APIBasicJsonApiService {
	
	
	public static final int INQUERY_TYPE_NEW = 1;
	public static final int INQUERY_TYPE_CANCEL = 2;
	public static final int INQUERY_TYPE_FINISH = 3;
	public static final int INQUERY_TYPE_QUERY = 4;
	
	public static final int INQUERY_TYPE_QUERY_ACT_QID = 1;
	public static final int INQUERY_TYPE_QUERY_ACT_ASKER_ID = 2;

	@Override
	protected BasicResponse service(JSONObject json) {
		JSONObject header = json.getJSONObject("header");

		Token token = checkAuth(header);
		if (token == null) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		JSONObject body = json.getJSONObject("body");

		if (!body.has("opt")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}

		int opt = body.getInt("opt");
		switch (opt) {
		case INQUERY_TYPE_NEW:
			return handleNewInquire(body, token);
		case INQUERY_TYPE_CANCEL:
			return cancelInquire(body, token);
		case INQUERY_TYPE_FINISH:
			return finishInquire(body, token);
		case INQUERY_TYPE_QUERY:
			return queryInquire(body, token);
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
	
	
	
	private BasicResponse queryInquire(JSONObject body, Token token) {
		if (!body.has("act")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		List<Question> list = null;
		int act = body.getInt("act");
		long questionId = 0;
		switch (act) {
		case INQUERY_TYPE_QUERY_ACT_QID:
			if (!body.has("question_id")) {
				return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
			}
			questionId = body.getLong("question_id");
			Question quest = CacheManager.getIntance().getPendingQuestion(questionId);
			if (quest == null) {
				list = ServiceFactory.getQuestionService().queryQuestion(questionId);
			} else {
				list = new ArrayList<Question>();
				list.add(quest);
			}
			break;
		case INQUERY_TYPE_QUERY_ACT_ASKER_ID:
			if (!body.has("asker_id")) {
				return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
			}
			long uid = body.getLong("asker_id");
			User asker = ServiceFactory.getESUserService().getUser(uid);
			if (asker == null) {
				 new InquireQueryResponse(list);
			}
			list = ServiceFactory.getQuestionService().queryQuestion(asker);
			break;
		}
		
		
		return new InquireQueryResponse(list);
	}

}
