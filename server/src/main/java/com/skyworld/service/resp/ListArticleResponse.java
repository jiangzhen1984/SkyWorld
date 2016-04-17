package com.skyworld.service.resp;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyworld.init.GlobalConstants;
import com.skyworld.service.APICode;
import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;
import com.skyworld.utils.JSONFormat;

public class ListArticleResponse extends JSONBasicResponse {
	
	List<Article> articles;
	
	
	

	public ListArticleResponse(List<Article> articles) {
		super();
		this.articles = articles;
	}




	@Override
	public JSONObject getResponseJSON() {
		JSONArray arrs = new JSONArray();
		int size = articles == null ? 0 : articles.size();
		for (int idx = 0; idx < size ; idx++) {
			Article article = articles.get(idx);
			JSONObject jsonArticle = new JSONObject();
			jsonArticle.put("id", article.getId());
			jsonArticle.put("status", article.getStatus());
			jsonArticle.put("comment",article.getComment());
			jsonArticle.put("timestamp", article.getTime().getTime());
			
			// publisher
			JSONObject jsonPublisher = new JSONObject();
			JSONFormat.populateUserData(jsonPublisher, article.getPublisher());
			jsonArticle.put("publisher", jsonPublisher);
			
			
			
			// pics array
			JSONArray jsonPics = new JSONArray();
			int ps = article.getPicsCount();
			for (int i = 0; i < ps; i++) {
				String pic = article.getPicPath(i);
				JSONObject pi = new JSONObject();
				pi.put("url", GlobalConstants.ARTICLE_HOST+pic);
				jsonPics.put(pi);
			}
			jsonArticle.put("pics", jsonPics);
			
			// recommendations array
			JSONArray jsonRecds = new JSONArray();
			int rs = article.getRecommendationCount();
			for (int i = 0; i < rs; i++) {
				User user = article.getRecommendationUser(i);
				JSONObject jsonReUser = new JSONObject();
				JSONFormat.populateUserData(jsonReUser, user);
				jsonRecds.put(jsonReUser);
			}
			jsonArticle.put("recommends", jsonRecds);
			
			//comments
			JSONArray jsonComs = new JSONArray();
			int cs = article.getCommentCount();
			for (int i = 0; i < cs; i++) {
				Article.Comment co = article.getComment(i);
				JSONObject jsonComment = new JSONObject();
				jsonComment.put("content", co.comment);
				jsonComment.put("timestamp", co.time);
				
				JSONObject jsonCommentUser = new JSONObject();
				JSONFormat.populateUserData(jsonCommentUser, co.commentUser);
				if (co.commentUser.getUserType() == UserType.SERVICER) {
					JSONFormat.populateServicerData(jsonCommentUser, (SKServicer)co.commentUser);
				}
				JSONFormat.populateEasemobData(jsonCommentUser, co.commentUser);
				jsonComment.put("user", jsonCommentUser);
				
				jsonComs.put(jsonComment);
				
			}
			jsonArticle.put("comments", jsonComs);
			
			
			arrs.put(jsonArticle);
		}
		// root
		JSONObject root = new JSONObject();
		root.put("ret", APICode.SUCCESS);
		root.put("articles", arrs);
		root.put("articles_count", size);
		return root;
	}

}

