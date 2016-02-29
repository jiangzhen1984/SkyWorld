package com.skyworld.service.dsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Article {

	
	protected long id;
	
	protected long userId;
	
	protected String comment;
	
	protected double lat;
	
	protected double lng;
	
	protected String location;
	
	protected boolean showLoation;
	
	protected User publisher;
	
	protected Date time;
	
	private List<Pic> pics;
	
	private List<Comment> comments;
	
	private List<Recommendation> recommendations;
	
	private int status;
	
	public Article() {
		super();
		pics = new ArrayList<Pic>();
		comments = new ArrayList<Comment>();
		recommendations = new ArrayList<Recommendation>();
		time = Calendar.getInstance().getTime();
	}
	
	public Article(Date time) {
		super();
		pics = new ArrayList<Pic>();
		comments = new ArrayList<Comment>();
		recommendations = new ArrayList<Recommendation>();
		this.time = time;
	}


	public int getPicsCount() {
		return pics.size();
	}
	
	
	public void addMedia(long id, String originPath,int type) {
		pics.add(new Pic(id, originPath, type));
	}
	
	public String getPicPath(int index) {
		return pics.get(index).path;
	}
	
	public long getPicId(int index) {
		return pics.get(index).id;
	}
	
	public boolean updatePicId(int index, long id) {
		Pic pic = pics.get(index);
		if (pic.id > 0) {
			return false;
		}
		pic.id = id;
		return true;
	}

	
	public void addComment(long id, User user, String comment, User toUser, long timestamp) {
		comments.add(new Comment(id, user,toUser, comment, timestamp));
	}
	
	public Comment getComment(int index) {
		if (index < 0 || index >= comments.size()) {
			throw new IndexOutOfBoundsException(" index is out of bounds "+ index+"  size:"+ comments.size());
		}
		return comments.get(index);
	}
	
	public int getCommentCount() {
		return comments.size();
	}
	
	
	public int getRecommendationCount() {
		return recommendations.size();
	}
	
	public void addRecommend(long id, User user) {
		recommendations.add(new Recommendation(id, user));
	}
	
	public User getRecommendationUser(int index) {
		if (index < 0 || index >= recommendations.size()) {
			throw new IndexOutOfBoundsException("index incorrect :"+ recommendations.size()+"  index:"+ index);
		}
		return recommendations.get(index).rUser;
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


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
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
	
	
	
	
	
	
	public User getPublisher() {
		return publisher;
	}


	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}

	
	





	public Date getTime() {
		return time;
	}


	public void setTime(Date time) {
		this.time = time;
	}








	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}








	class Pic {
		long id;
		String path;
		int type;
		long time;
		public Pic(long id, String path, int type) {
			super();
			this.id = id;
			this.path = path;
			this.type = type;
		}
		
		
	}
	
	
	
	public class Comment {
		public long id;
		public User commentUser;
		public User toUser;
		public String comment;
		public long time;
		public Comment(long id, User commentUser, User toUser, String comment, long time) {
			super();
			this.id = id;
			this.commentUser = commentUser;
			this.toUser = toUser;
			this.comment = comment;
			this.time = time;
		}
		
		
	}
	
	
	class Recommendation {
		long id;
		User rUser;
		public Recommendation(long id, User rUser) {
			super();
			this.id = id;
			this.rUser = rUser;
		}
		
		
	}
	
	
}
