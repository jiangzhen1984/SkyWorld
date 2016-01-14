package com.skyworld.pushimpl;

import java.util.Queue;

import com.skyworld.cache.CacheManager;
import com.skyworld.cache.Token;
import com.skyworld.push.ClientTerminal;
import com.skyworld.push.OnConnectionNotifier;
import com.skyworld.push.event.SHPEvent;
import com.skyworld.service.dsf.User;

public class DefaultPushConnectionNotifier implements OnConnectionNotifier {

	@Override
	public void onConnected(Token token, ClientTerminal terminal) {
		User user = CacheManager.getIntance().getUser(token);
		if (user != null) {
			user.setPushTerminal(terminal);
			//post pending message
			Queue<SHPEvent> queue = user.getPendingEvents();
			SHPEvent ev = null;
			while (queue != null && (ev = queue.poll()) != null) {
				terminal.postEvent(ev);
			}
		}
	}

}
