package com.skyworld.easemob;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class EaseMobDeamonTest extends TestCase {
	
	Log log = LogFactory.getLog(EaseMobDeamonTest.class);
	private EaseMobDeamon deamon;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		deamon = new EaseMobDeamon();
		deamon.start();
		
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		deamon.shutdown();
	}

	
	@Test
	public void testAuthorization() {
		log.info("=======start testAuthorization======");
		try {
			deamon.authorize("SkyWorld", "SkyWorld", "YXA6UW3TIKTGEeW8okGnOCdMYw", "YXA60GJ7UHua7FFXKEf_P3brVRdUusM");
			Thread.currentThread().sleep(5000);
			assertTrue(deamon.isAuthed());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("=======end testAuthorization======");
	}


	
	
	@Test
	public void testRegister() {
		log.info("=======start testRegister======");
		try {
			deamon.authorize("SkyWorld", "SkyWorld", "YXA6UW3TIKTGEeW8okGnOCdMYw", "YXA60GJ7UHua7FFXKEf_P3brVRdUusM");
			Thread.currentThread().sleep(5000);
			assertTrue(deamon.isAuthed());
			
			deamon.register("aaa1", "aaa1");
			Thread.currentThread().sleep(10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("=======end testRegister======");
	}

}
