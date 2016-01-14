package com.skyworld.cache;

import java.util.concurrent.atomic.AtomicLong;

public class TokenFactory {

	private static AtomicLong atomic = new AtomicLong();
	
	public static Token generateLongToken() {
		return new LongToken(System.currentTimeMillis() << 16 | atomic.getAndIncrement());
	}
	
	
	public static Token generateStringToken() {
		return new StringToken(System.currentTimeMillis() << 16 | atomic.getAndIncrement());
	}
	
	

	public static Token valueOf(long lid) {
		return new LongToken(lid);
	}
	
	

	public static Token valueOf(String t) {
		return new StringToken(t);
	}
	
	
	
}
