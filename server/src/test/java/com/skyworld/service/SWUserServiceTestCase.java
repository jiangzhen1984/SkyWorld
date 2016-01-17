package com.skyworld.service;


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.skyworld.service.dsf.User;

public class SWUserServiceTestCase extends TestCase {
	
	SWUserService service;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		service = new SWUserService();
	}

	@Test
	public void testAddUser() {
		User u = new User();
		u.setName("aaa1");
		u.setCellPhone("12341");
		u.setPassword("a");
		u.setAvatarPath("aaaa");
		service.addUser(u);
		
	}

}
