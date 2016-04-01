package com.skyworld.service;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.service.dsf.SKServicer;
import com.skyworld.service.po.SWPServicerCompanyDesc;



public class SWSKServicerService extends BaseService {
	


	
	public void updateCompanyInfo(SKServicer skervicer) {
		Session sess = openSession();
		Query query = sess.createQuery(" from SWPServicerCompanyDesc where servicer.id = ? ");
		query.setLong(0, skervicer.getId());
		List<SWPServicerCompanyDesc>  descList = query.list();
		Transaction trans = sess.beginTransaction();
		if (descList.size() <= 0) {
			SWPServicerCompanyDesc desc = new SWPServicerCompanyDesc();
			desc.setCmpDesc(skervicer.getCmpDesc());
			desc.setCmpName(skervicer.getCmpName());
			desc.setLogoPath(skervicer.getLogoPath());
			desc.setWebsite(skervicer.getWebsite());
			desc.setPhone(skervicer.getCmpPhone());
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
}
