package com.skyworld.easemob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChainRunnable implements Runnable {
	
	Log log = LogFactory.getLog(this.getClass());
	
	private Runnable[] arr;
	

	public ChainRunnable(Runnable[] arr) {
		super();
		this.arr = arr;
	}


	@Override
	public void run() {
		if (arr == null || arr.length <= 0) {
			log.warn(" Array size is 0 ");
			return;
		}
		for(Runnable r : arr) {
			log.info(" ===> chain --> " + r.getClass().getName());
			r.run();
		}

	}

}
