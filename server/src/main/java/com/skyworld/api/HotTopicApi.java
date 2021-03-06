package com.skyworld.api;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.service.APIService;

public class HotTopicApi  extends HttpServlet {
	
	Log log = LogFactory.getLog(this.getClass());
	
 	 
	@Resource(name="hotTopicService")
	private APIService hotTopicService;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		hotTopicService.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		hotTopicService.service(req, resp);
	}

}
