package com.skyworld.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.dsf.SKServicer.SKServicerCMPItem;
import com.skyworld.service.po.SWPServicerCompanyDesc;
import com.skyworld.service.po.SWPServicerCompanyItem;
import com.skyworld.service.po.SWPUser;



public class SWSKServicerService extends BaseService {
	


	
	public void updateCompanyInfo(SKServicer skervicer) {
		Session sess = openSession();
		Query query = sess.createQuery(" from SWPServicerCompanyDesc where servicer.id = ? ");
		query.setLong(0, skervicer.getId());
		List<SWPServicerCompanyDesc>  descList = query.list();
		Transaction trans = sess.beginTransaction();
		if (descList.size() <= 0) {
			SWPUser user = new SWPUser();
			user.setId(skervicer.getId());
			SWPServicerCompanyDesc desc = new SWPServicerCompanyDesc();
			desc.setCmpDesc(skervicer.getCmpDesc());
			desc.setCmpName(skervicer.getCmpName());
			desc.setLogoPath(skervicer.getLogoPath());
			desc.setWebsite(skervicer.getWebsite());
			desc.setPhone(skervicer.getCmpPhone());
			desc.setServicer(user);
			sess.save(desc);
		} else {
			SWPServicerCompanyDesc desc = descList.get(0);
			desc.setCmpDesc(skervicer.getCmpDesc());
			desc.setCmpName(skervicer.getCmpName());
			desc.setLogoPath(skervicer.getLogoPath());
			desc.setWebsite(skervicer.getWebsite());
			desc.setPhone(skervicer.getCmpPhone());
			sess.update(desc);
		}
		trans.commit();
		sess.close();
	}
	
	
	
	public void populateCompanyInfor(SKServicer skervicer) {
		Session sess = openSession();
		Query query = sess.createQuery(" from SWPServicerCompanyDesc where servicer.id = ? ");
		query.setLong(0, skervicer.getId());
		List<SWPServicerCompanyDesc>  descList = query.list();
		if (descList.size() > 0) {
			SWPServicerCompanyDesc desc = descList.get(0);
			skervicer.setCmpDesc(desc.getCmpDesc());
			skervicer.setCmpName(desc.getCmpName());
			skervicer.setLogoPath(desc.getLogoPath());
			skervicer.setWebsite(desc.getWebsite());
			skervicer.setCmpPhone(desc.getPhone());
		}
		sess.close();
	}
	
	
	/**
	 * Only support update and save. Doesn't support delete
	 * @param skervicer
	 * @return
	 */
	public int batchUpdateCmpItem(SKServicer skervicer) {
		Iterator<SKServicerCMPItem> itor = skervicer.iteratorCMPItem();
		if (itor == null) {
			return 0;
		}
		int count = 0;
		SKServicerCMPItem item = null;
		SWPServicerCompanyItem citem = null;
		Session sess = openSession();
		Transaction t = null;
		int opt = 0;
		while(itor.hasNext()) {
			item = itor.next();
			if (item.isNeedPersist) {
				citem = new SWPServicerCompanyItem();
				opt = 1;
			} else if (item.isNeedUpdate) {
				opt = 2;
				citem = (SWPServicerCompanyItem)sess.get(SWPServicerCompanyItem.class, item.id);
				if (citem == null) {
					//TODO log item incorrect
					continue;
				}
			} else {
				continue;
			}
			
			
			if ((opt ==1 || opt ==2 ) && t == null) {
				t = sess.beginTransaction();
				
				citem.setContent(item.content);
				citem.setPic(item.pic);
				citem.setTitle(item.title);
				citem.setServicer(skervicer);
				
				switch (opt) {
				case 1:
					sess.save(citem);
					sess.flush();
					item.id = citem.getId();
					break;
				case 2:
					sess.update(citem);
					break;
				} 
				
				item.isNeedPersist = false;
				item.isNeedUpdate = false;
				count ++;
			}
			
			item = null;
			citem = null;
		}
		
		if (t != null) {
			t.commit();
		}
		sess.close();
		
		return count;
		
	}
	
	
	
	public long  addCmpItem(SKServicer skervicer, SKServicerCMPItem item) {
		SWPServicerCompanyItem citem = new SWPServicerCompanyItem();
		citem.setContent(item.content);
		citem.setPic(item.pic);
		citem.setTitle(item.title);
		citem.setServicer(skervicer);
		Session sess = openSession();
		Transaction t = sess.beginTransaction();
		sess.save(citem);
		t.commit();
		sess.flush();
		item.id = citem.getId();
		sess.close();
		return item.id;
	}
	
	
	public void deleteCmpItem(SKServicer skervicer, SKServicerCMPItem item) {
		Session sess = openSession();
		SWPServicerCompanyItem citem = (SWPServicerCompanyItem)sess.get(SWPServicerCompanyItem.class, item.id);
		Transaction t = sess.beginTransaction();
		sess.delete(citem);
		sess.flush();
		t.commit();
		sess.close();
		skervicer.removeCMPItem(item.id);
	}
	
	
	public List<SKServicerCMPItem> querySKServicerCMPItemList(SKServicer skervicer, int startPage, int itemCount) {
		if (skervicer == null) {
			return null;
		}
		Session sess = openSession();
		Query query = sess.createQuery(" from SWPServicerCompanyItem where servicer.id = ? ");
		query.setLong(0, skervicer.getId());
		query.setFirstResult((startPage - 1) * itemCount);
		query.setMaxResults(itemCount);
		List<SWPServicerCompanyItem> list = query.list();
		if (list == null || list.size() <= 0) {
			return null;
		}
		List<SKServicerCMPItem> cmpItemList = new ArrayList<SKServicerCMPItem>(list.size());
		for (SWPServicerCompanyItem it : list) {
			SKServicerCMPItem item = skervicer.new SKServicerCMPItem(it.getId(), it.getTitle(), it.getPic(), it.getContent());
			item.servicer = skervicer;
			cmpItemList.add(item);
		}
		sess.close();
		
		return cmpItemList;
	}
	
	
	public SKServicerCMPItem querySKServicerCMPItem(SKServicer skervicer, long id) {
		Session sess = openSession();
		SWPServicerCompanyItem ci = (SWPServicerCompanyItem)sess.get(SWPServicerCompanyItem.class, id);
		SKServicerCMPItem item =  null;
		if (ci != null) {
			item = skervicer.new SKServicerCMPItem(ci.getId(), ci.getTitle(), ci.getPic(), ci.getContent());
		}
		sess.close();
		return item;
	}
}
