package com.skyworld.service.po;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_FEED_BACK")
public class SWPFeedback {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="USER_ID", columnDefinition="NUMERIC(20)")
	protected long userId;
	
	@Column(name="CUSTOMER_COMMENT", columnDefinition="VARCHAR(4000)")
	protected String comments; 
	
	@Column(name="FEED_BACK_TIME", columnDefinition="TIMESTAMP")
	protected Date feedbackTimestamp;
	
	
	@Column(name="ANSWER_COMMENT", columnDefinition="VARCHAR(4000)")
	protected String answer; 
	
	@Column(name="ANSWER_TIME", columnDefinition="TIMESTAMP")
	protected Date answerTimestamp;
	
	@Column(name="PIC_1", columnDefinition="VARCHAR(200)")
	protected String pic1; 
	
	@Column(name="PIC_2", columnDefinition="VARCHAR(200)")
	protected String pic2; 
	
	@Column(name="PIC_3", columnDefinition="VARCHAR(200)")
	protected String pic4;

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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getPic1() {
		return pic1;
	}

	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}

	public String getPic2() {
		return pic2;
	}

	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}

	public String getPic4() {
		return pic4;
	}

	public void setPic4(String pic4) {
		this.pic4 = pic4;
	}

	public Date getFeedbackTimestamp() {
		return feedbackTimestamp;
	}

	public void setFeedbackTimestamp(Date feedbackTimestamp) {
		this.feedbackTimestamp = feedbackTimestamp;
	}
	
	public void setFeedbackTimestamp() {
		this.feedbackTimestamp = new Date(System.currentTimeMillis());
	}
	

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Date getAnswerTimestamp() {
		return answerTimestamp;
	}

	public void setAnswerTimestamp(Date answerTimestamp) {
		this.answerTimestamp = answerTimestamp;
	} 
	
	
	
	
	
}
