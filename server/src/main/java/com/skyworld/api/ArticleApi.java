package com.skyworld.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.service.ServiceFactory;

@MultipartConfig(maxFileSize=5 * 1024 *1024, maxRequestSize= 6 * 1024 *1024)
public class ArticleApi extends HttpServlet {

	
	Log log = LogFactory.getLog(this.getClass());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServiceFactory.getAPIService(ServiceFactory.API_CODE_ARTICLE).service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServiceFactory.getAPIService(ServiceFactory.API_CODE_ARTICLE).service(req, resp);
	}

}
