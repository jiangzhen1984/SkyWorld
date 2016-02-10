package com.skyworld.service;


import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.po.SWPArticleComment;
import com.skyworld.service.po.SWPArticleRecommendation;

public class SWArticleServiceTestCase extends TestCase {
	
	SWArticleService  service;
	SWUserService userService;
	User u ;
	User u1;
	User u2;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		service = new SWArticleService();
		userService = new SWUserService();
		 u = new User();
		 u1 = new User();
		 u2 = new User();
		
		userService.addUser(u);
		userService.addUser(u1);
		userService.addUser(u2);
	}

	@Test
	public void testAddArticle() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		assertTrue(article.getId() > 0); 
	}

	@Test
	public void testAddCommentArticleUserUserString() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		
		SWPArticleComment ac = service.addComment(article, u1, u2, "bbb1");
		SWPArticleComment ac1 = service.addComment(article, u1, u2, "bb2b");
		SWPArticleComment ac2 = service.addComment(article, u1, u2, "bbb3");
		assertTrue(ac.getId() > 0);
		assertTrue(ac.getUserId() == u1.getId());
		
		assertTrue(ac1.getId() > 0);
		assertTrue(ac1.getUserId() == u1.getId());
		
		assertTrue(ac2.getId() > 0);
		assertTrue(ac2.getUserId() == u1.getId());
	}

	@Test
	public void testAddCommentLongLongLongString() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		
		SWPArticleComment ac = service.addComment(article.getId(), u1.getId(), u2.getId(), "bbb");
		SWPArticleComment ac1 = service.addComment(article.getId(), u1.getId(), u2.getId(), "bbb");
		SWPArticleComment ac2 = service.addComment(article.getId(), u1.getId(), u2.getId(), "bbb");
		assertTrue(ac.getId() > 0);
		assertTrue(ac.getUserId() == u1.getId());
		
		assertTrue(ac1.getId() > 0);
		assertTrue(ac1.getUserId() == u1.getId());
		
		assertTrue(ac2.getId() > 0);
		assertTrue(ac2.getUserId() == u1.getId());
	}

	@Test
	public void testAddRecommendationArticleUser() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		
		SWPArticleRecommendation ac = service.addRecommendation(article, u1);
		assertTrue(ac.getId() > 0);
		assertTrue(ac.getUserId() == u1.getId());
		
	}

	@Test
	public void testAddRecommendationLongLong() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		
		SWPArticleRecommendation ac = service.addRecommendation(article.getId(), u1.getId());
		assertTrue(ac.getId() > 0);
		assertTrue(ac.getUserId() == u1.getId());
	}

	@Test
	public void testCancelRecommedationArticleUser() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		
		SWPArticleRecommendation ac = service.addRecommendation(article.getId(), u1.getId());
		
		
		service.cancelRecommedation(article, u1);
	}

	@Test
	public void testCancelRecommedationLongLong() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(u);
		service.addArticle(article);
		
		SWPArticleRecommendation ac = service.addRecommendation(article.getId(), u1.getId());
		
		
		service.cancelRecommedation(article.getId(), u1.getId());
	}

	@Test
	public void testQueryArticleList() {
		
		for (int i =0; i < 10; i++) {
			Article article = new Article();
			article.setComment("aaaa");
			article.addPic(0, "bbb");
			article.setLat(2.0D);
			article.setLng(1.0D);
			article.addPic(0, "9999");
			article.setPublisher(u);
			service.addArticle(article);
			
		    service.addRecommendation(article.getId(), u1.getId());
		    service.addRecommendation(article.getId(), u2.getId());
		    
		    service.addComment(article.getId(), u2.getId(), 0, "bbb"+i);
		    service.addComment(article.getId(), u2.getId(), 0, "bbb1"+ i);
		    service.addComment(article.getId(), u1.getId(), 0, "bbb2"+i);
		}
		
		
		ArrayList<Long> userIds = new ArrayList<Long>();
		userIds.add(Long.valueOf(u1.getId()));
		userIds.add(Long.valueOf(u2.getId()));
		List<Article> list = service.queryArticle(userIds);
		assertTrue(list.size() == 0);
		
		
		userIds.add(Long.valueOf(u.getId()));
		list = service.queryArticle(userIds);
		assertTrue(list.size() == 10);
	}
	
	

}
