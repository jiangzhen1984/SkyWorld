package com.skyworld.service.dsf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Question {
	
	private User asker;
	
	private long id;
	
	private String question;
	
	private Date timestamp;
	
	private State state;
	
	
	private List<SKServicer> waitanswerList;
	
	private List<Answer> answeredList;
	
	public Question() {
		super();
	}

	public Question(User user, String question) {
		super();
		this.asker = user;
		this.question = question;
		timestamp = new Date(System.currentTimeMillis());
		this.state = State.INQUIREING;
		
		if (waitanswerList == null) {
			waitanswerList = new ArrayList<SKServicer>();
		}
		if (answeredList == null) {
			answeredList = new ArrayList<Answer>();
		}
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	

	
	
	public User getAsker() {
		return asker;
	}

	public void setAsker(User asker) {
		this.asker = asker;
	}

	
	
	public void addSKServicer(SKServicer servicer) {
		synchronized(this) {
			if (waitanswerList == null) {
				waitanswerList = new ArrayList<SKServicer>();
			}
		}
		waitanswerList.add(servicer);
	}
	
	
	public int getServicerCount() {
		int aswCount = answeredList== null? 0 : answeredList.size();
		int pendingCount= waitanswerList == null? 0 : waitanswerList.size();
		return aswCount + pendingCount;
	}
	
	
	public int getAnswerCount() {
		return answeredList== null? 0 : answeredList.size();
	}
	
	public int getWaitingCount() {
		return  waitanswerList == null? 0 : waitanswerList.size();
	}
	
	public void setAnswer(SKServicer servicer, Answer ans) {
		waitanswerList.remove(servicer);
		answeredList.add(ans);
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
		Question other = (Question) obj;
		if (id != other.id)
			return false;
		return true;
	}




	public enum State {
		INQUIREING,
		CANCEL,
		RESOVLED
	}
	
}
