package com.skyworld.service;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class PartsWrapper {
	
	HttpServletRequest req;
	
	public PartsWrapper(HttpServletRequest req) {
		this.req = req;
	}
	
	public Collection<Part> getParts() throws IOException {
		try {
			return req.getParts();
		} catch (ServletException e) {
			throw new IOException(e);
		}
	}
	
	
	public Part getPart(String name)  throws IOException {
		try {
			return req.getPart(name);
		} catch (ServletException e) {
			throw new IOException(e);
		}
	}

}
