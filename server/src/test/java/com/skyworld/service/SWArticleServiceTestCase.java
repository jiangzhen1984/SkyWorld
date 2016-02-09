package com.skyworld.service;


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.skyworld.service.dsf.Article;
import com.skyworld.service.dsf.User;
import com.skyworld.service.po.SWPArticleComment;

public class SWArticleServiceTestCase extends TestCase {
	
	SWArticleService  service;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		service = new SWArticleService();
	}

	@Test
	public void testAddArticle() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(new User());
		service.addArticle(article);
		assertTrue(article.getId() > 0); 
	}

	@Test
	public void testAddCommentArticleUserUserString() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddCommentLongLongLongString() {
		Article article = new Article();
		article.setComment("aaaa");
		article.addPic(0, "bbb");
		article.setLat(2.0D);
		article.setLng(1.0D);
		article.addPic(0, "9999");
		article.setPublisher(new User());
		service.addArticle(article);
		
		User u1 = new User();
		u1.setId(1);
		User u2 = new User();
		u2.setId(100);
		SWPArticleComment ac = service.addComment(article, u1, u2, "bbb");
		SWPArticleComment ac1 = service.addComment(article, u1, u2, "bbb");
		SWPArticleComment ac2 = service.addComment(article, u1, u2, "bbb");
		assertTrue(ac.getId() > 0);
		assertTrue(ac.getUserId() == u1.getId());
		
		assertTrue(ac1.getId() > 0);
		assertTrue(ac1.getUserId() == u1.getId());
		
		assertTrue(ac2.getId() > 0);
		assertTrue(ac2.getUserId() == u1.getId());
	}

	@Test
	public void testAddRecommendationArticleUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddRecommendationLongLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testCancelRecommedationArticleUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testCancelRecommedationLongLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testQueryArticleListOfInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testQueryArticleListOfIntegerDateDateInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testQueryArticleRelatedData() {
		fail("Not yet implemented");
	}

}
