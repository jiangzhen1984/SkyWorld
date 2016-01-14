package com.skyworld.push;

import com.skyworld.cache.Token;

public interface OnConnectionNotifier {
	
	public void onConnected(Token token, ClientTerminal terminal);

}
