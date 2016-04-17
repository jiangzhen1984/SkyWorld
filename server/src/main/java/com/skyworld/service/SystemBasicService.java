package com.skyworld.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.skyworld.service.dsf.AndroidAppVersion;
import com.skyworld.service.dsf.Feedback;
import com.skyworld.service.dsf.IOSAppVersion;
import com.skyworld.service.po.SWAppVersion;
import com.skyworld.service.po.SWPFeedback;

public class SystemBasicService extends BaseService {

	
	public void createFeedback(Feedback feedback) {
		feedback.setFeedbackTimestamp(new Date(System.currentTimeMillis()));
		Session session = openSession();
		SWPFeedback fb = new SWPFeedback();
		fb.setComments(feedback.getComments());
		fb.setFeedbackTimestamp(feedback.getFeedbackTimestamp());
		fb.setAnswer(feedback.getAnswer());
		fb.setAnswerTimestamp(feedback.getAnswerTimestamp());
		fb.setPic1(feedback.getPic1());
		fb.setPic2(feedback.getPic2());
		fb.setPic4(feedback.getPic4());
		fb.setUserId(feedback.getUserId());
		
		Transaction t = session.beginTransaction();
		session.save(fb);
		t.commit();
		
		session.close();
		feedback.setId(fb.getId());
	}
	
	
	public void respondFeedback(String response, long id) {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		SWPFeedback fd = (SWPFeedback)session.load(SWPFeedback.class, id);
		fd.setAnswer(response);
		fd.setAnswerTimestamp(new Date(System.currentTimeMillis()));
		session.update(fd);
		t.commit();
		session.close();
	}
	
	
	public List<Feedback> getFeedbackList(int pageNo, int pageSize) {
		
		Session session = openSession();
		Query query = session.createQuery(" from SWPFeedback  ");
		query.setFirstResult((pageNo - 1) * pageSize);
		query.setMaxResults(pageSize);
		List<SWPFeedback>  sfblist = query.list();
		
		List<Feedback>  list = new ArrayList<Feedback>(sfblist.size());
		Iterator<SWPFeedback> it = sfblist.iterator();
		while (it.hasNext()) {
			SWPFeedback swp = it.next();
			Feedback nb = new Feedback();
			nb.setComments(swp.getComments());
			nb.setFeedbackTimestamp(swp.getFeedbackTimestamp());
			nb.setAnswer(swp.getAnswer());
			nb.setAnswerTimestamp(swp.getAnswerTimestamp());
			nb.setPic1(swp.getPic1());
			nb.setPic2(swp.getPic2());
			nb.setPic4(swp.getPic4());
			
			list.add(nb);
		}
		return list;
		
	}
	
	
	public IOSAppVersion  getIOSAppVersion() {
		IOSAppVersion ios  = null;
		Session session = openSession();
		Query query = session.createQuery(" from SWAppVersion where appType = ?  ");
		//1 for ios
		query.setInteger(0, 1);
		List<SWAppVersion> list = query.list();
		if (list != null  && list.size() > 0) {
			SWAppVersion swapp = list.iterator().next();
			ios = new IOSAppVersion();
			ios.setId(swapp.getId());
			ios.setAppType(swapp.getAppType());
			ios.setFilename(swapp.getFilename());
			ios.setForce(swapp.isForce());
			ios.setType(swapp.getType());
			ios.setNumber(swapp.getNumber());
		}
		session.close();
		return ios;
	}
	
	
	public AndroidAppVersion  getAndroidAppVersion() {
		AndroidAppVersion android  = null;
		Session session = openSession();
		Query query = session.createQuery(" from SWAppVersion where appType = ?  ");
		//0 for android
		query.setInteger(0, 0);
		List<SWAppVersion> list = query.list();
		if (list != null  && list.size() > 0) {
			SWAppVersion swapp = list.iterator().next();
			android = new AndroidAppVersion();
			android.setId(swapp.getId());
			android.setAppType(swapp.getAppType());
			android.setFilename(swapp.getFilename());
			android.setForce(swapp.isForce());
			android.setType(swapp.getType());
			android.setNumber(swapp.getNumber());
		}
		session.close();
		return android;
	}
}
