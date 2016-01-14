package com.skyworld.pushimpl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.push.ClientTerminal;
import com.skyworld.push.TerminalManager;
import com.skyworld.push.event.MessageEvent;

public class IntervalDebugPush extends Thread {
	
	private boolean requestStopFlag;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	
	

	public IntervalDebugPush() {
		super();
		this.setName("IntervalDebugPush Thread ");
	}



	@Override
	public void run() {
		while (!requestStopFlag) {
			List<ClientTerminal> list = TerminalManager.getInstance().queueTerminal();
			log.info(" DEBUG===> broadcast : " + list+"  size: " +list.size());
			for (ClientTerminal ter : list) {
				ter.postEvent(new MessageEvent(new DebugMessage()));
			}
			
			synchronized (this) {
				try {
					wait(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void requestStop() {
		requestStopFlag = true;
		this.interrupt();
	}

}
