package com.skyworld.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SSOFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hsr = (HttpServletRequest)req;
		String auth = hsr.getHeader("Authorization");
		if (auth == null) {
			//req.getRequestDispatcher("/no_login.html").forward(req, resp);
			chain.doFilter(req, resp);
		} else {
			chain.doFilter(req, resp);
		}
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {

	}

}
