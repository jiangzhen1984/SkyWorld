package com.skyworld.init;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.push.PushServerDeamon;
import com.skyworld.pushimpl.DefaultPushConnectionNotifier;
import com.skyworld.pushimpl.JSONTransformer;
import com.skyworld.service.ServiceFactory;

public class StartupServlet extends GenericServlet {

	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void destroy() {
		super.destroy();
		log.info("============server destroyed=======");
	}

	@Override
	public void init() throws ServletException {
		super.init();
		log.info("============server starting up=======");
		ServiceFactory.getEaseMobService().start();
		log.info("============server initialized easemod service ==>"+ServiceFactory.getEaseMobService());
		log.info("============server initialized user service ==>"+ServiceFactory.getESUserService());
		log.info("============server initialized querstion service ==>"+ServiceFactory.getQuestionService());
		log.info("============server initialized API service ==>"+ ServiceFactory.getAPIService());
		
		ServiceFactory.getEaseMobService().authorize("SkyWorld", "SkyWorld", "YXA6UW3TIKTGEeW8okGnOCdMYw", "YXA60GJ7UHua7FFXKEf_P3brVRdUusM");
		log.info("============server start request easemod token <<<=====");
		
		PushServerDeamon.getInstance().setHttpPushMessageTransformer(new JSONTransformer());
		PushServerDeamon.getInstance().setNotifiier(new DefaultPushConnectionNotifier());
	}

	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		
	}

	
	
	

}
