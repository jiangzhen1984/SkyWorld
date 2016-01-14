package com.skyworld.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.skyworld.cache.Token;

public class TerminalManager {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static TerminalManager instance;
	private Map<Token, TerminalWrapper> wrpperMap;

	
	private TerminalManager() {
		wrpperMap = new ConcurrentHashMap<Token, TerminalWrapper>();
	}
	
	public synchronized static TerminalManager getInstance() {
		if (instance == null) {
			instance = new TerminalManager();
		}
		return instance;
	}
	
	
	public void recordTerminal(Token token, ClientTerminal terminal) {
		if (token == null || terminal == null) {
			throw new NullPointerException(" token or terminal is null");
		}
		TerminalWrapper tw = wrpperMap.get(token);
		if (tw != null) {
			if (tw.terminal != terminal) {
				throw new IllegalArgumentException(" Termial "+  tw.terminal+"  already exist ");
			}
		} else {
			tw = new TerminalWrapper(token, terminal);
			wrpperMap.put(token, tw);
			log.info(" Record  token: " + token +"   termina: "+ terminal);
		}
		
	}
	
	
	public ClientTerminal getTerminal(Token token) {
		if (token == null) {
			throw new NullPointerException(" token  is null");
		}
		TerminalWrapper tw = wrpperMap.get(token);
		return tw == null? null : tw.terminal;
	}
	
	
	public boolean removeTerminal(Token token) {
		if (token == null) {
			throw new NullPointerException(" token is null");
		}
		TerminalWrapper tw = wrpperMap.remove(token);
		return tw != null;
	}
	
	
	public List<ClientTerminal> queueTerminal() {
		List<ClientTerminal> terminals = new ArrayList<ClientTerminal>();
		Iterator<TerminalWrapper> its = wrpperMap.values().iterator();
		while(its.hasNext()) {
			terminals.add(its.next().terminal);
		}
		Collections.sort(terminals, new Comparator<ClientTerminal>(){

			@Override
			public int compare(ClientTerminal o1, ClientTerminal o2) {
				return o1.lastUpdate > o2.lastUpdate ? -1 :(o1.lastUpdate == o2.lastUpdate ? 0 : 1);
			}
			
		});
		return terminals;
	}
	
	
	
	
	class TerminalWrapper implements Comparable<TerminalWrapper> {
		
		Token token;
		ClientTerminal terminal;
		

		public TerminalWrapper(Token token, ClientTerminal terminal) {
			super();
			this.token = token;
			this.terminal = terminal;
		}


		@Override
		public int compareTo(TerminalWrapper o) {
			return this.terminal.lastUpdate > o.terminal.lastUpdate ? 1 : (this.terminal.lastUpdate  == o.terminal.lastUpdate  ? 0 : -1);
		}
	}
}
