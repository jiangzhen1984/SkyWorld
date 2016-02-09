package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_ARTICLE")
public class SWPArticle {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="USER_ID", columnDefinition="NUMERIC(20)")
	protected long userId;
	
	@Column(name="AR_COMMENT", columnDefinition="VARCHAR(2000)")
	protected String comment;
	
	@Column(name="AR_RECOMMENDS_COUNT", columnDefinition="NUMERIC(4)")
	protected int recommendsCount;
	
	@Column(name="AR_COMMENTS_COUNT", columnDefinition="NUMERIC(4)")
	protected int commentsCount;
	
	@Column(name="AR_GPS_LAT", columnDefinition="DOUBLE")
	protected double lat;
	
	@Column(name="AR_GPS_LNG", columnDefinition="DOUBLE")
	protected double lng;
	
	@Column(name="AR_LOCATION", columnDefinition="VARCHAR(200)")
	protected String location;
	
	@Column(name="AR_SHOW_LOCATION", columnDefinition="CHAR(1)")
	protected boolean showLoation;
	
	@Column(name="AR_TIME_STAMP", columnDefinition="NUMERIC(30)")
	protected long timeStamp;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getRecommendsCount() {
		return recommendsCount;
	}

	public void setRecommendsCount(int recommendsCount) {
		this.recommendsCount = recommendsCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isShowLoation() {
		return showLoation;
	}

	public void setShowLoation(boolean showLoation) {
		this.showLoation = showLoation;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	
	
	
}
