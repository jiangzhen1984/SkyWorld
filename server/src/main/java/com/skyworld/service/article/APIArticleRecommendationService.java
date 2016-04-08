package com.skyworld.service.article;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.APIBasicJsonPartApiService;
import com.skyworld.service.APICode;
import com.skyworld.service.PartsWrapper;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.ArticleResponse;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;

public class APIArticleRecommendationService extends APIBasicJsonPartApiService {

	@Override
	protected BasicResponse service(JSONObject json,  PartsWrapper partwrapper) {
		JSONObject header = json.getJSONObject("header");
		JSONObject body = json.getJSONObject("body");
		
		if (!header.has("token")) {
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
		
		if (!body.has("article_id")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		if (!body.has("flag")) {
			return new RTCodeResponse(APICode.REQUEST_PARAMETER_NOT_STISFIED);
		}
		
		long articleId = body.getLong("article_id");
		boolean flag = body.getBoolean("flag");
		if (flag) {
			ServiceFactory.getEArticleService().addRecommendation(articleId, user.getId());
		} else {
			ServiceFactory.getEArticleService().cancelRecommedation(articleId,  user.getId());
		}
		Article article = ServiceFactory.getEArticleService().queryArticle(articleId);
		if (article == null) {
			return new RTCodeResponse(APICode.ARTICLE_ERROR_ARTICLE_NOT_EXIST);
		}
		
		return new ArticleResponse(article);
	}

}
