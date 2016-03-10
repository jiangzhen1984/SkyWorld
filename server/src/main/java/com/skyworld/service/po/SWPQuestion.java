package com.skyworld.service.po;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table( name = "SW_QUESTION" )
public class SWPQuestion {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	
	@Column(name="QUESTION", columnDefinition="VARCHAR(2000)")
	private String question;
	
	@Column(name="USER_ID", columnDefinition="BIGINT")
	protected long userId;
	
	@Column(name="TIME_STAMP", columnDefinition="datetime")
	private Date timestamp;
	
	@Column(name="Q_STATE", columnDefinition="NUMERIC(1)")
	private int state;

	
	
	public SWPQuestion() {
		
	}
	
	public SWPQuestion(SWPQuestion u) {
		if (u == null) {
			throw new NullPointerException(" u is null");
		}
		this.setId(u.getId());
		this.setQuestion(u.getQuestion());
		this.setUserId(u.getUserId());
		this.setTimestamp(u.getTimestamp());
		this.setState(u.getState());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	
	
}
