package com.skyworld.service.po;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_ARTICLE_COMMENT")
public class SWPArticleComment {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="ARTICLE_ID", columnDefinition="BIGINT")
	protected long articleId;
	
	@Column(name="USER_ID", columnDefinition="BIGINT")
	protected long userId;
	
	@Column(name="TO_USER_ID", columnDefinition="BIGINT")
	protected long toUserId;
	
	@Column(name="AR_COMMENT", columnDefinition="VARCHAR(2000)")
	protected String comment;
	
	@Column(name="AR_TIME_STAMP", columnDefinition="DATETIME")
	protected Date timeStamp;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}
	

	
	

	
}
