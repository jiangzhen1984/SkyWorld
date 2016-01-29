package com.skyworld.service;


import java.util.List;

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
	
	
	
	
	@Test
	public void testSelectUserList() {
		String[] phones = new String[10];
		String[] mails = new String[10];
		for (int i = 0; i < 10; i++) {
			User u = new User();
			u.setName("a");
			u.setCellPhone("a" + i);
			u.setMail("b" + i);
			u.setPassword("a");
			u.setAvatarPath("aaaa");
			service.addUser(u);
			
			phones[i] = "a" +i;
			mails[i] = "b" +i;
		}
		
		
		List<User> list = service.selectUserList(phones, mails);
		assertTrue(list.size() == 10);
	}
}
