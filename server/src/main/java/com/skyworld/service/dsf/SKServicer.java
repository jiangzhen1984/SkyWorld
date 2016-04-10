package com.skyworld.service.dsf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.skyworld.service.po.SWPUser;



public class SKServicer extends User {
	
	public static final int CMP_PAGE_NO = 15;

	
	private String location;
	
	private String area;
	
	private String serviceDesc;
	
	
	///////company
	private String cmpName;
	
	private String website;
	
	private String cmpDesc;
	
	private String logoPath;
	
	private String cmpPhone;
	
	/////Item
	private List<SKServicerCMPItem> items;
	
	

	public SKServicer() {
		super();
		super.setUserType(UserType.SERVICER);
	}

	public SKServicer(User u) {
		super(u);
		super.setUserType(UserType.SERVICER);
	}
	
	

	public SKServicer(SWPUser u) {
		super(u);
		super.setUserType(UserType.SERVICER);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getCmpName() {
		return cmpName;
	}

	public void setCmpName(String cmpName) {
		this.cmpName = cmpName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCmpDesc() {
		return cmpDesc;
	}

	public void setCmpDesc(String cmpDesc) {
		this.cmpDesc = cmpDesc;
	}

	public String getLogoPath() {
		return logoPath;
	}
	
	public String getLogoURL() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public String getCmpPhone() {
		return cmpPhone;
	}

	public void setCmpPhone(String cmpPhone) {
		this.cmpPhone = cmpPhone;
	}
	
	
	public void addCmpItem(long id, String title, String pic, String content) {
		List<SKServicerCMPItem> list = getItemList();		
		list.add(new SKServicerCMPItem(id, title, pic, content));
	}
	
	
	public void addCmpItem(String title, String pic, String content) {
		List<SKServicerCMPItem> list = getItemList();		
		list.add(new SKServicerCMPItem(title, pic, content));
	}
	
	
	public int getItemCount() {
		return this.items == null? 0 : items.size();
	}
	
	
	public SKServicerCMPItem removeCMPItem(SKServicerCMPItem it) {
		Iterator<SKServicerCMPItem> itor =iteratorCMPItem ();
		if (itor == null) {
			return null;
		}
		while (itor.hasNext()) {
			if (itor.next() == it) {
				itor.remove();
				return it;
			}
		}
		return null;
	}
	
	
	public SKServicerCMPItem removeCMPItem(long id) {
		Iterator<SKServicerCMPItem> itor =iteratorCMPItem ();
		if (itor == null) {
			return null;
		}
		while (itor.hasNext()) {
			SKServicerCMPItem tmp = itor.next();
			if (tmp.id == id) {
				itor.remove();
				return tmp;
			}
		}
		return null;
	}
	
	
	public Iterator<SKServicerCMPItem> iteratorCMPItem() {
		if (items == null) {
			return null;
		}
		return items.iterator();
	}
	
	
	
	private synchronized List<SKServicerCMPItem>  getItemList() {
		if (items != null) {
			return this.items;
		}
		this.items = new ArrayList<SKServicerCMPItem>();
		return items;
	}
	
	
	private static SKServicer dummy;
	public static SKServicer getDummy() {
		if (dummy == null) {
			dummy = new SKServicer();
		}
		return dummy;
	}
	
	public class SKServicerCMPItem {
		public long id;
		
		public String title;
		
		public String pic;
		
		public String content;
		
		public boolean isNeedPersist;
		
		public boolean isNeedUpdate;
		
		public SKServicer servicer;

		public SKServicerCMPItem(long id, String title, String pic,
				String content) {
			super();
			this.id = id;
			this.title = title;
			this.pic = pic;
			this.content = content;
			isNeedPersist = false;
			isNeedUpdate = false;
		}
		
		public SKServicerCMPItem(String title, String pic,
				String content) {
			super();
			this.title = title;
			this.pic = pic;
			this.content = content;
			isNeedPersist = true;
			isNeedUpdate = false;
		}
		
		
	}
	

}
