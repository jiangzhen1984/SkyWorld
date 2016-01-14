package com.skyworld.cache;

public class LongToken extends Token {
	
	private long id;

	public LongToken(long tr) {
		super();
		this.id = tr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		LongToken other = (LongToken) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LongToken [id=" + id + "]";
	}

	@Override
	public Object getValue() {
		return id;
	}

	
	
}
