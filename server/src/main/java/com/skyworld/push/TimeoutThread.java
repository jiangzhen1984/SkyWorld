package com.skyworld.push;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.push.event.TimeoutEvent;

public class TimeoutThread extends Thread {
	
	private boolean requestStopFlag;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	
	

	public TimeoutThread() {
		super();
		this.setName("Push Time out Thread ");
	}



	@Override
	public void run() {
		while (!requestStopFlag) {
			List<ClientTerminal> list = TerminalManager.getInstance().queueTerminal();
			log.info(" Scan client terminals size: " +list.size());
			for (ClientTerminal ter : list) {
				boolean triggerTimeout = false;
				boolean remove = false;
				synchronized(ter){
					if (ter.isTimeout()) {
						if ((System.currentTimeMillis() - ter.getTimeout()) / 1000 > 20) {
							triggerTimeout = true;
							remove = true;
						}
					} else {
						if ((System.currentTimeMillis() - ter.lastUpdate) / 1000 > 40) {
							triggerTimeout = true;
							ter.setTimeout(true);
						}
					}
				}
				
				if (triggerTimeout) {
					ter.postEvent(new TimeoutEvent());
					
				}
				if (remove) {
					TerminalManager.getInstance().removeTerminal(ter.token);
				}
				
				log.info(" Terminal " + ter+"   remove:"+ remove+"  timeout:"+triggerTimeout+"");
			}
			
			synchronized (this) {
				try {
					wait(10000);
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
