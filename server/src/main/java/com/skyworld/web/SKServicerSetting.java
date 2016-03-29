package com.skyworld.web;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.TokenFactory;
import com.skyworld.service.ServiceFactory;
import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.User;
import com.skyworld.service.dsf.UserType;

public class SKServicerSetting extends HttpServlet {

	
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
				strId =  m.group();
				i++;
				break;
			case 3:
				method =  m.group();
				i++;
				break;
			}
				
		}
		
		String auth = req.getHeader("Authorization");
		SKServicer sk = CacheManager.getIntance().getSKServicer(TokenFactory.valueOf(auth));
		if ("/setting".equalsIgnoreCase(action)) {
			
			if (method == null && strId != null) {
				forwardSettingIndex(req, resp, sk);
			}else if ("update".equals(method) && strId != null) {
			}
		}
		
	}
	
	
	private void forwardSettingIndex(HttpServletRequest req, HttpServletResponse resp, SKServicer sk) throws ServletException, IOException {
		if (sk == null) {
			return;
		}
		
		//Query
		HttpSession sess = req.getSession(true);
		sess.setAttribute("uid", sk.getId());
		resp.addCookie(new Cookie("uid", sk.getId()+""));
		resp.addCookie(new Cookie("website", sk.getWebsite()));
		resp.addCookie(new Cookie("logo", sk.getLogoURL()));
		resp.addCookie(new Cookie("desc", sk.getCmpDesc()));
		resp.addCookie(new Cookie("name", sk.getCmpName()));
		req.getRequestDispatcher("/web/info_basic_company.html").forward(req, resp);
	}
	
	private void updateIndex(HttpServletRequest req, HttpServletResponse resp, String id) {
		
	}
}
