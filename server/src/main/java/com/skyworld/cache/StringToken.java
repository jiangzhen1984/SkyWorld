package com.skyworld.cache;

/**
 * Use to String wrap token id
 * @author 28851274
 *
 */
public class StringToken extends Token {

	private String token;
	
	public StringToken(String token) {
		this.token = token;
	}
	
	public StringToken(long token) {
		this.token = String.valueOf(token);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		StringToken other = (StringToken) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return token;
	}

	@Override
	public Object getValue() {
		return token;
	}


	
	
}
