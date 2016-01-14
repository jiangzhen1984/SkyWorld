package com.skyworld.service.dsf;

import com.skyworld.cache.Token;

public class UserToken extends Token {
	
	private Token token;
	
	private User user;

	public UserToken(User user, Token token) {
		super();
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserToken other = (UserToken) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public Object getValue() {
		return user;
	}

	
	public Token getToken() {
		return token;
	}
	
	
	
}
