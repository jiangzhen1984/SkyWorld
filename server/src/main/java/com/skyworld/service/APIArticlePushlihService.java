package com.skyworld.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.Part;

import org.json.JSONObject;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.resp.ArticleResponse;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.RTCodeResponse;
import com.skyworld.utils.GlobalPath;
import com.skyworld.utils.ImageUtil;

public class APIArticlePushlihService extends APIBasicJsonPartApiService {

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
		
		Article arc = extractData(header, body);
		
		arc.setPublisher(user);
		boolean ret;
		try {
			ret = handlePart(arc,  partwrapper.getParts());
			if (!ret) {
				return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new RTCodeResponse(APICode.HANDLER_STREAM_FAILED);
		}
		
		
		ServiceFactory.getEArticleService().addArticle(arc);
		return new ArticleResponse(arc);
	}

	
	private Article extractData(JSONObject header, JSONObject body) {
		Article article = new Article();
		if (body.has("lat")) {
			article.setLat(body.getDouble("lat"));
		}
		if (body.has("lng")) {
			article.setLng(body.getDouble("lng"));
		}
		if (body.has("location")) {
			article.setLocation(body.getString("location"));
		}
		if (body.has("comment")) {
			article.setComment(body.getString("comment"));
		}
		return article;
	}
	
	private boolean handlePart(Article article, Collection<Part> parts) {
		if (parts == null || parts.size() <= 0) {
			return true;
		}
		Iterator<Part> its = parts.iterator();
		int index = 1;
		while (its.hasNext()) {
			Part p = its.next();
			
			if (p == null || p.getContentType() == null) {
				continue;
			}
			//TODO handle video
			
			if (!"image/".startsWith(p.getContentType())) {
				log.error("Found non-image type part: " + p.getContentType());
				continue;
			}
			String filename = "article_"+article.getPublisher().getId()+"_"+System.currentTimeMillis()+"_" + index +".png";
			String imageDir = GlobalPath.getArticlePicHome();
			String contextPath = GlobalPath.getArticlePicContext();
			log.info("write article image to :"  +  (imageDir+"/" + filename));
			InputStream in = null;
			try {
				in = p.getInputStream();
				boolean ret = ImageUtil.copyImage(in, imageDir+"/" + filename);
				if (!ret) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
					
			}
			index ++ ;
			//TODO 0 for image 1 for video
			article.addMedia(0, contextPath+filename, 0);
		}
		
		return true;
	}
}
