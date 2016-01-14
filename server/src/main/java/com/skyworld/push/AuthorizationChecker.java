package com.skyworld.push;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthorizationChecker {
	
	
	public boolean authorize(HttpServletRequest req, HttpServletResponse resp);
	
}
