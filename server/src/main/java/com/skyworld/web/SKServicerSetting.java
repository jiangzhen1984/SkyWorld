package com.skyworld.web;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String contextp = req.getContextPath();
		Pattern p = Pattern.compile("(/)([0-9A-Za-z]+)");
		Matcher m = p.matcher(cp);
		String category= null;
		String action = null;;
		String method= null;
		String strId= null;
		int i = 0;
		while(m.find()) {
			switch(i) {
			case 0:
				category = m.group();
				if (contextp.equalsIgnoreCase(category)) {
					i = 0;
					continue;
				}
				i++;
				break;
			case 1:
				action =  m.group();
				i++;
				break;
			case 2:
				method =  m.group();
				i++;
				break;
			case 3:
				method =  m.group();
				i++;
				break;
			}
				
		}
		
	
		
		if ("/setting".equalsIgnoreCase(action) && method == null) {
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
		} else if ("/setting".equals(action) && "/update".equals(method)) {
			updateServicer(req, resp);
		}
		
	}
	
	
	private void forwardSettingIndex(HttpServletRequest req, HttpServletResponse resp, SKServicer sk, Token token) throws ServletException, IOException {
		if (sk == null) {
			return;
		}
		//Query
		HttpSession sess = req.getSession(true);
		sess.setAttribute("uid", sk.getId());
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
}
