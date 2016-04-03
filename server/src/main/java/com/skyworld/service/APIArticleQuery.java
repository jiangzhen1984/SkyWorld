package com.skyworld.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.easemob.EasemobUser;
import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.ListArticleResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIArticleQuery extends APIBasicJsonPartApiService {

	private static final int QT_NATIVE_ID = 0;
	private static final int QT_EASEMOB_USER_NAME = 1;
	
	@Override
	protected BasicResponse service(JSONObject json,  PartsWrapper partwrapper) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		if (!header.has("token")  || !body.has("qt")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		String tokenId = header.getString("token");
		if (tokenId == null || tokenId.trim().isEmpty()) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		int queryType = body.getInt("qt");
		if (queryType != QT_EASEMOB_USER_NAME && queryType != QT_NATIVE_ID) {
			return new RTCodeResponse(APICode.ARTICLE_QUERY_ERROR_TYPE_NOT_SUPPORT);
		}
			
		
		Token token = TokenFactory.valueOf(tokenId);
		User user = CacheManager.getIntance().getUser(token);
		if (user == null) {
			return new RTCodeResponse(APICode.TOKEN_INVALID);
		}
		
		
		
		Date start = null;
		Date end = null;
		if (body.has("timestamp_start")) {
			start = new Date(body.getLong("timestamp_start"));
		} else {
			start = new Date(System.currentTimeMillis());
		}
		if (body.has("timestamp_end")) {
			end = new Date(body.getLong("timestamp_end"));
		}
		int fetchCount = 15;
		if (body.has("fetch_count")) {
			fetchCount = body.getInt("fetch_count");
		}

		List<Long> ids = null;
		if (queryType == QT_EASEMOB_USER_NAME) {
			EasemobUser easemobUser = new EasemobUser();
			easemobUser.userName = user.getCellPhone();
			List<EasemobUser> contactsList= ServiceFactory.getEaseMobService().queryContacts(easemobUser);
			
			int size = contactsList == null ? 0 : contactsList.size();
			
			if (size > 0) {
				String[] phones = new String[size];
				String[] mails = new String[size];
				for (EasemobUser eu : contactsList) {
					--size;
					phones[size] = eu.userName;
					mails[size] = eu.userName;
					
				}
				List<User> us = ServiceFactory.getESUserService().selectUserList(phones, mails);
				int count = us.size();
				ids = new ArrayList<Long>(count + 1);
				ids.add(user.getId());
				for(int i = 0; i < count; i++) {
					ids.add(us.get(i).getId());
				}
			} else {
				ids = new ArrayList<Long>(1);
				ids.add(user.getId());
			}
			
			
			
			
			
		} else {
			if (!user.isRelationQueryFlag()) {
				ServiceFactory.getESUserService().queryUserRelation(user);
			}
			int count = user.getRelationUserCount();
			ids = new ArrayList<Long>(count + 1);
			ids.add(user.getId());
			
			Iterator<User> it = user.iteratorRelationUser();
			if (it != null) {
				while(it.hasNext()) {
					ids.add(it.next().getId());
				}
			}
			
		}
		
		
		if (ids == null || ids.size() <= 0) {
			return new ListArticleResponse(null);
		} else {
			List<Article> list = ServiceFactory.getEArticleService().queryArticle(ids, start, end, fetchCount);
			return new ListArticleResponse(list);
		}
	}

}
