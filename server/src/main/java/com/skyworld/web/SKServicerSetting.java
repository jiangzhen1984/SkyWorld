package com.skyworld.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.SKServicer;

@MultipartConfig(maxFileSize=5 * 1024 *1024, maxRequestSize= 6 * 1024 *1024)
public class SKServicerSetting extends HttpServlet {

	private static final boolean DEBUG = true;
	
	private static  int CATEGORY_IDX = 0;
	private static final  int CONTEXT_OFFSET = 1;
	private static final  int ACTION_OFFSET =  1;
	private static final  int METHOD_OFFSET =  2;
	private static final  int SUB_METHOD_OFFSET =  3;
	
	
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String contextPath = config.getServletContext().getContextPath();
		if (contextPath != null && !"".equals(contextPath)) {
			CATEGORY_IDX = CONTEXT_OFFSET;
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp);
	}

	
	
	private void handle(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String cp = req.getRequestURI();
		Pattern p = Pattern.compile("(/)([0-9A-Za-z]+)");
		Matcher m = p.matcher(cp);
		List<String> groupList = new ArrayList<String>(6);
		while(m.find()) {
			groupList.add(m.group());
		}
		Restful rf = matchRestful(groupList);
	
		
		if ("/setting".equalsIgnoreCase(rf.action) && "/info".equals(rf.method)) {
			String auth = null;
			SKServicer sk = null;
			Token tk;
			if (DEBUG) {
				
				sk = new SKServicer();
				sk.setName("aa");
				sk.setMail("bbb");
				sk.setPassword("123456");
				sk.setCmpName("www");
				sk.setCmpDesc("bbb");
				sk.setWebsite("http://www.google.com");
				sk.setCmpPhone("131231");
				ServiceFactory.getESUserService().addUser(sk);
				
				tk = CacheManager.getIntance().saveUser(sk);
			} else {
				auth = req.getHeader("Authorization");
				tk =TokenFactory.valueOf(auth);
				sk = CacheManager.getIntance().getSKServicer(
						tk);
			}
			forwardSettingIndex(req, resp, sk, tk);
		} else if ("/setting".equals(rf.action) && "/update".equals(rf.method)) {
			updateServicer(req, resp);
		} else if ("/setting".equals(rf.action) && "/cmplist".equals(rf.method)) {
			if  (rf.submethod == null) {
				showCmpList(req, resp);
			} else if ("/update".equals(rf.submethod)) {
				ServiceFactory.getAPIService(ServiceFactory.API_CODE_SERVICER).service(req, resp);
			}
		}
		
	}
	
	
	private void forwardSettingIndex(HttpServletRequest req, HttpServletResponse resp, SKServicer sk, Token token) throws ServletException, IOException {
		if (sk == null) {
			return;
		}
		//Query
		HttpSession sess = req.getSession(true);
		sess.setAttribute("uid", sk.getId());
		resp.addCookie(new Cookie("cp", req.getContextPath()));
		resp.addCookie(new Cookie("uid", sk.getId()+""));
		resp.addCookie(new Cookie("wwebsite", sk.getWebsite()));
		resp.addCookie(new Cookie("logo", sk.getLogoURL()));
		resp.addCookie(new Cookie("desc", sk.getCmpDesc()));
		resp.addCookie(new Cookie("name", sk.getCmpName()));
		resp.addCookie(new Cookie("phone", sk.getCmpPhone()));
		resp.addCookie(new Cookie("at", token.getValue().toString()));
		req.getRequestDispatcher("/web/info_basic_company.html").forward(req, resp);
	}
	
	
	private void updateServicer(HttpServletRequest req, HttpServletResponse resp) {
		ServiceFactory.getAPIService(ServiceFactory.API_CODE_SERVICER).service(req, resp);
	}
	
	private void showCmpList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/web/editor.html").forward(req, resp);
	}
	
	
	
	private Restful matchRestful(List<String> group) {
		Restful rf = new Restful();
		int size = group.size();
		if (size > CATEGORY_IDX) {
			rf.category = group.get(CATEGORY_IDX);
			rf.isExistCategory = true;
		}
		
		if (size > CATEGORY_IDX + ACTION_OFFSET) {
			rf.action = group.get(CATEGORY_IDX + ACTION_OFFSET);
			rf.isExistAction = true;
		}
		if (size > CATEGORY_IDX + METHOD_OFFSET) {
			rf.method = group.get(CATEGORY_IDX + METHOD_OFFSET);
			rf.isExistMethod = true;
		}
		if (size > CATEGORY_IDX + SUB_METHOD_OFFSET) {
			rf.submethod = group.get(CATEGORY_IDX + SUB_METHOD_OFFSET);
			rf.isExistSubMethod = true;
		}
		return rf;
	}
	
	
	class Restful {
		boolean isExistCategory;
		String category;
		boolean isExistAction;
		String action;
		boolean isExistMethod;
		String method;
		boolean isExistSubMethod;
		String submethod;
		String id;
		
	}
}
