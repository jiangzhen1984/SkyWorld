package com.skyworld.service.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SW_RELATIONSHIP")
public class SWPRelationship {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	protected long id;
	
	@Column(name="USER_ID1", columnDefinition="BIGINT")
	protected long userId1;
	
	@Column(name="USER_ID2", columnDefinition="BIGINT")
	protected long userId2;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId1() {
		return userId1;
	}

	public void setUserId1(long userId1) {
		this.userId1 = userId1;
	}

	public long getUserId2() {
		return userId2;
	}

	public void setUserId2(long userId2) {
		this.userId2 = userId2;
	}
	
	

}
