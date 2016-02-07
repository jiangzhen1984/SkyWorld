package com.skyworld.service;


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.skyworld.service.dsf.Feedback;

public class SystemBasicServiceTestCase extends TestCase {
	
	SystemBasicService basicService;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		basicService = new SystemBasicService();
	}

	@Test
	public void testCreateFeedback() {
		Feedback feedback = new Feedback();
		feedback.setComments("aaa");
		basicService.createFeedback(feedback);
		assertTrue(feedback.getId() > 0);
		assertEquals(feedback.getComments(), "aaa");
	}

	@Test
	public void testRespondFeedback() {
		Feedback feedback = new Feedback();
		feedback.setComments("aaa");
		basicService.createFeedback(feedback);
		
		basicService.respondFeedback("bbb", feedback.getId());
		
	}

}
