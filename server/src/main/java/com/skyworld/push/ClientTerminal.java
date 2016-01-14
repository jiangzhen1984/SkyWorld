package com.skyworld.push;

import java.io.IOException;
import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.cache.Token;
import com.skyworld.push.event.MessageEvent;
import com.skyworld.push.event.SHPEvent;
import com.skyworld.push.msg.HttpPushMessage;

public class ClientTerminal  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4197293545564086480L;
	
	private Log log = LogFactory.getLog(this.getClass());


	protected Token token;
	
	
	protected long lastUpdate;
	
	protected boolean isTimeout;
	
	protected long timeout;
	
	
	protected Queue<SHPEvent> events;
	

	protected TerminalSocket socket;
	
	protected  HttpPushMessageTransformer<HttpPushMessage> transformer;
	
	private Thread attacher;
	
	

	public ClientTerminal(Token token, TerminalSocket socket, HttpPushMessageTransformer<HttpPushMessage> transformer) {
		this.token = token;
		this.socket = socket;
		this.transformer = transformer;
		lastUpdate = System.currentTimeMillis();
		events = new LinkedBlockingDeque<SHPEvent>();
	}
	
	
	public void updateTimeStamp() {
		synchronized(this) {
			this.lastUpdate = System.currentTimeMillis();
		}
	}
	
	
	
	
	public boolean isTimeout() {
		return isTimeout;
	}


	public void setTimeout(boolean isTimeout) {
		synchronized(this) {
			this.isTimeout = isTimeout;
		}
	}


	public long getTimeout() {
		return timeout;
	}


	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}


	public boolean postEvent(SHPEvent ev) {
		if (ev == null) {
			throw new NullPointerException(" event is  null");
		}
		synchronized (events) {
			events.offer(ev);
			events.notifyAll();
			log.info(events+ " ==== > post event :" + ev+"  to queue :"+events+" client :" + this);
		}
		return true;
	}

	
	public void waitForEvent() {
		SHPEvent ev = null;
		synchronized (events) {
			log.error("Set attacher:  Thread:"+ Thread.currentThread());
			attacher = Thread.currentThread();
			ev = events.poll();
			if (ev == null) {
				try {
					log.info("Wait1:  Thread:"+ Thread.currentThread());
					events.wait();
					log.info(events+ "Resume2:  Thread:"+ Thread.currentThread());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (attacher != Thread.currentThread()) {
					log.warn("attacher quit due to not match" + Thread.currentThread());
					events.notifyAll();
					return;
				}
				ev = events.poll();
			}
		}
		if (ev == null) {
			throw new IllegalArgumentException(" event is null ");
		}
		
		try {
			log.info("start to handle event: " + ev+" Thread:"+ Thread.currentThread());
			handleEvent(ev);
		} catch (IOException e) {
			log.error("handle event error " + ev , e);
		}
		
	}
	
	
	
	private void handleEvent(SHPEvent event) throws IOException {
		SHPEvent.Type st = event.getEvType();
		switch (st) {
		case POST_MESSAGE:
			String data = transformer.serialize(((MessageEvent)event).getMessage());
			socket.setHeader("Content-Type", transformer.getContentType());
			socket.setHeader("Content-Length", Integer.toOctalString(data.length()));
			socket.write(data);
			break;
		case CLOSE:
		case SHUTDOWN:
			socket.setHeader("connection", "close");
			socket.writeError(Code.SC_SERVICE_UNAVAILABLE, " connection close ");
			break;
		case TIME_OUT:
			this.isTimeout = true;
			this.timeout = System.currentTimeMillis();
			socket.writeError(Code.SC_REQUEST_TIMEOUT, " time out ");
			break;
		default:
			break;
		}
	}


	@Override
	public String toString() {
		return "[token : "+token+"  lastUpdate:+"+lastUpdate+"  isTimeout:+"+isTimeout+"   timeout:"+timeout+"]";
	}
	
	
	
	
	
}
