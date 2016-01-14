package com.skyworld.push;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.push.event.ShutdownEvent;

public class SimplePushServlet extends HttpServlet {
	
	Log log = LogFactory.getLog(this.getClass());
	
	private static boolean DEBUG_MODE = true;
	private PushServerDeamon deamon;
	

	@Override
	public void destroy() {
		super.destroy();
		List<ClientTerminal> list = TerminalManager.getInstance().queueTerminal();
		log.info("Start to shut down ");
		for (ClientTerminal ter : list) {
			ter.postEvent(new ShutdownEvent());
		}
		if (deamon != null) {
			deamon.destroy();
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		PushServerDeamon cache = (PushServerDeamon)config.getServletContext().getAttribute("PushServerDeamon");
		if (cache != null) {
			cache.destroy();
		}
		deamon = PushServerDeamon.getInstance();
		deamon.init();
		config.getServletContext().setAttribute("PushServerDeamon", deamon);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (DEBUG_MODE) {
			handle(req, resp);
		} else {
			resp.setHeader("connection", "close");
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handle(req, resp);
	}
	
	
	
	
	private void handle(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (deamon == null) {
			resp.setHeader("connection", "close");
			resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, " deamon is not running");
			return;
		}
		resp.setCharacterEncoding("utf8");
		deamon.service(req, resp);
		
		HttpSession sess = req.getSession(false);
		if (sess != null) {
			sess.invalidate();
		}
	}
	
	

}
