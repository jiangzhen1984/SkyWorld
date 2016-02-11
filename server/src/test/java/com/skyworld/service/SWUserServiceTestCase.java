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
		int ret = service.addUser(u);
		assertEquals(0, ret);
		
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
	
	@Test
	public void testMakeUserRelationation() {
		
		User u1 = new User();
		u1.setName("aaa1");
		u1.setCellPhone("123412");
		u1.setPassword("a");
		u1.setAvatarPath("aaaa");
		service.addUser(u1);
		
		
		User u2 = new User();
		u2.setName("aaa1");
		u2.setCellPhone("123413");
		u2.setPassword("a");
		u2.setAvatarPath("aaaa");
		service.addUser(u2);
		
		service.makeRelation(u1, u2, true);
		assertTrue(u1.isInRelation(u2));
		
		
		
	}
	@Test
	public void testRemoveUserRelation() {
		
		
		User u1 = new User();
		u1.setName("aaa1");
		u1.setCellPhone("111121");
		u1.setPassword("a");
		u1.setAvatarPath("aaaa");
		service.addUser(u1);
		
		
		User u2 = new User();
		u2.setName("aaa12");
		u2.setCellPhone("111124");
		u2.setPassword("a");
		u2.setAvatarPath("aaaa");
		service.addUser(u2);
		
		
		User u3 = new User();
		u3.setName("aaa13");
		u3.setCellPhone("111123");
		u3.setPassword("a");
		u3.setAvatarPath("aaaa");
		service.addUser(u3);
		
		
		service.makeRelation(u1, u2, true);
		service.makeRelation(u1, u3, true);
		service.makeRelation(u2, u3, true);
		
		
		service.removeRelation(u1, u2, true);
		assertFalse(u1.isInRelation(u2));
		assertFalse(u2.isInRelation(u1));
		
		
		service.removeRelation(u1, u3, false);
		assertFalse(u1.isInRelation(u3));
		assertTrue(u3.isInRelation(u1));
		
	}
	
	
	@Test
	public void testQueryUserRelation() {
		
		
		User u3 = new User();
		u3.setName("aaa13");
		u3.setCellPhone("111113");
		u3.setPassword("a");
		u3.setAvatarPath("aaaa");
		service.addUser(u3);
		
		
		User u1 = new User();
		u1.setName("aaa1");
		u1.setCellPhone("111111");
		u1.setPassword("a");
		u1.setAvatarPath("aaaa");
		service.addUser(u1);
		
		
		User u2 = new User();
		u2.setName("aaa12");
		u2.setCellPhone("111114");
		u2.setPassword("a");
		u2.setAvatarPath("aaaa");
		service.addUser(u2);
		
		service.makeRelation(u1, u2, true);
		service.makeRelation(u1, u3, true);
		service.makeRelation(u2, u3, true);
		
		User u = service.getUser(u1.getId());
		service.queryUserRelation(u);
		assertTrue(u.isInRelation(u2));
		assertTrue(u.isInRelation(u3));
		
	}
	
	
}
