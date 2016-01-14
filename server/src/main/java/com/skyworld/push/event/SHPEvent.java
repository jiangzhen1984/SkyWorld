package com.skyworld.push.event;


public abstract class SHPEvent{
	
	
	protected Type evType;
	
	public SHPEvent(Type evType) {
		super();
		this.evType = evType;
	}
	
	


	public Type getEvType() {
		return evType;
	}




	public enum Type {
		POST_MESSAGE,
		TIME_OUT,
		CLOSE,
		SHUTDOWN,
	}
	
	
	
	
	
	
	@Override
	public String toString() {
		return "SHPEvent [evType=" + evType + "]";
	}

		


}
