package com.skyworld.service.po;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_ARTICLE_RECOMMENDATION")
public class SWPArticleRecommendation {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="ARTICLE_ID", columnDefinition="NUMERIC(20)")
	protected long articleId;
	
	@Column(name="USER_ID", columnDefinition="NUMERIC(20)")
	protected long userId;
	
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

	
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}


	
}
