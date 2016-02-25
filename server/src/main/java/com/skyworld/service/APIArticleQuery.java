package com.skyworld.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.ListArticleResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIArticleQuery extends APIBasicJsonPartApiService {

	@Override
	protected BasicResponse service(JSONObject json,  PartsWrapper partwrapper) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		if (!header.has("token") || !body.has("timestamp_start") || !body.has("timestamp_end")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		Token token = TokenFactory.valueOf(tokenId);
		User user = CacheManager.getIntance().getUser(token);
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		
		Date start = new Date(body.getLong("timestamp_start"));
		Date end = new Date(body.getLong("timestamp_end"));
		
		if (!user.isRelationQueryFlag()) {
			ServiceFactory.getESUserService().queryUserRelation(user);
		}
		int fetchCount = 15;
		if (body.has("fetch_count")) {
			fetchCount = body.getInt("fetch_count");
		}
		int count = user.getRelationUserCount();
		List<Long> ids = new ArrayList<Long>(count + 1);
		ids.add(user.getId());
		for(int i =0; i < count; i++) {
			ids.add(user.getRelationUser(i).getId());
		}
		List<Article> list = ServiceFactory.getEArticleService().queryArticle(ids, start, end, fetchCount);
		return new ListArticleResponse(list);
	}

}
