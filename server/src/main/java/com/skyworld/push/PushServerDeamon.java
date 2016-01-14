package com.skyworld.push;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.cache.Token;
import com.skyworld.cache.TokenFactory;
import com.skyworld.push.msg.HttpPushMessage;
import com.skyworld.pushimpl.IntervalDebugPush;

public class PushServerDeamon {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static boolean LOCAL_DEBUG = false;
	
	private HttpPushMessageTransformer<HttpPushMessage> transformer;
	
	private AuthorizationChecker  authorizationChecker;
	
	private OnConnectionNotifier notifiier;
	
	private TimeoutThread timeoutThread;
	
	private IntervalDebugPush debugPush;
	
	private static PushServerDeamon deamon;

	private PushServerDeamon() {
		
	}
	
	public static PushServerDeamon getInstance() {
		if (deamon == null) {
			deamon = new PushServerDeamon();
		}
		return deamon;
	}
	
	
	public void setHttpPushMessageTransformer(HttpPushMessageTransformer<HttpPushMessage> transformer) {
		this.transformer = transformer;
	}
	
	public void setAuthorizationChecker(AuthorizationChecker checker) {
		this.authorizationChecker = checker;
	}
	
	
	
	
	public OnConnectionNotifier getNotifiier() {
		return notifiier;
	}

	public void setNotifiier(OnConnectionNotifier notifiier) {
		this.notifiier = notifiier;
	}

	public void init() {
		if (timeoutThread != null && timeoutThread.isAlive()) { 
			timeoutThread.requestStop();
		}
		timeoutThread = new TimeoutThread();
		timeoutThread.start();
		
		if (LOCAL_DEBUG) {
			if (debugPush != null && debugPush.isAlive()) { 
				debugPush.requestStop();
			}
			debugPush = new IntervalDebugPush();
			debugPush.start();
		}
		
	}
	
	
	public boolean authorize(TerminalSocket socket) {
		if (authorizationChecker == null) {
			return true;
		}
		return true;
	}
	
	
	
	public Token initToken(TerminalSocket socket) {
		return TokenFactory.valueOf(socket.req.getHeader("Authorization"));
	}
	

	public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		TerminalSocket socket = new TerminalSocket(req, resp);
		if (!authorize(socket)) {
			boolean ret = socket.writeError(Code.SC_UNAUTHORIZED, " Token is unavailable ");
			log.info(" send  error message result: "+ ret);
			return;
		}
		
		Token token = initToken(socket);
		if (token != null) {
			//FIXME check token legal
			ClientTerminal terminal = TerminalManager.getInstance().getTerminal(token);
			log.info(" fetch token " + token.getValue()+"   terminal:"+  terminal);
			if (terminal == null) {
				terminal = new ClientTerminal(token, socket, transformer);
				TerminalManager.getInstance().recordTerminal(token, terminal);
			} else {
				//Make sure prior thread resume
				terminal.socket = socket;
			}
			terminal.updateTimeStamp();
			terminal.socket.isAvl = true;
			terminal.isTimeout = false;
			terminal.timeout = 0;
			if (notifiier != null) {
				notifiier.onConnected(token, terminal);
			}
			
			terminal.waitForEvent();
		} else {
			boolean ret = socket.writeError(Code.SC_UNAUTHORIZED, " Token is unavailable ");
			log.info(" send  error message result: "+ ret);
		}
	}
	
	
	public void destroy() {
		if (timeoutThread != null) {
			timeoutThread.requestStop();
			timeoutThread.interrupt();
		}
		
		
		if (LOCAL_DEBUG) {
			if (debugPush != null) {
				debugPush.requestStop();
				debugPush.interrupt();
			}
		}
		
	}
}
