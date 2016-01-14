package com.skyworld.push;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TerminalSocket {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	boolean isAvl;
	
	HttpServletRequest req;
	HttpServletResponse resp;

	
	
	
	
	public TerminalSocket(HttpServletRequest req, HttpServletResponse resp) {
		super();
		this.req = req;
		this.resp = resp;
		isAvl = true;
		log.info(" [SOCKET] req: "+req+" resp:" + resp);
	}





	public boolean isAvailable() {
		return isAvl;
	}
	
	
	
	public boolean writeError(int code, String msg) throws IOException {
		if (isAvailable()) {
			log.info(" [ERROR-CODE : " + code + "] :" + msg);
			switch (code) {
			case HttpServletResponse.SC_REQUEST_TIMEOUT:
				resp.setHeader("connection", "close");
				break;
			}

			resp.sendError(code, msg);
			isAvl = false;
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean write(char[] buf, int off, int len) throws IOException {
		if (isAvailable()) {
			log.info(" [WRITE-CHAR] :" + new String(buf, off,len));
			log.info(" [WRITE] :" + resp);
			PrintWriter pw = resp.getWriter();
			pw.write(buf, off, len);
			pw.flush();
			resp.flushBuffer();
			isAvl = false;
			return true;
		} else {
			return false;
		}
	}
	
	
	
	public boolean write(String msg) throws IOException {
		if (isAvailable()) {
			log.info(" [WRITE ] :" + msg);
			log.info(" [WRITE] :" + resp);
			resp.setBufferSize(msg.length());
			PrintWriter pw = resp.getWriter();
			pw.write(msg);
			pw.flush();
			resp.flushBuffer();
			isAvl = false;
			return true;
		} else {
			return false;
		}
	}
	
	
	public void setHeader(String name, String val) {
		if (isAvailable()) {
			resp.setHeader(name, val);			
		}
	}
}
