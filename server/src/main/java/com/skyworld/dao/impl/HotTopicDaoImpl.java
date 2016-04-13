package com.skyworld.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.skyworld.dao.HotTopicDao;
import com.skyworld.service.po.SWPHotTopic;

public class HotTopicDaoImpl  extends BaseDaoImpl implements HotTopicDao{
	
	public List<SWPHotTopic> findHotTopicList(int opt, int type, int curCount, long start){
 		List param = new ArrayList();
		StringBuffer sql = new StringBuffer("from SWPHotTopic where 1 = 1");
		
 		if(type != 0){
			sql.append(" and type=?");
			param.add(type);
		}
 		if(start != 0){
			if(opt == 0){
				sql.append(" and update_time > ? order by update_time asc ");
	 		}else if(opt == 1){
	 			sql.append(" and update_time <= ? order by update_time desc ");
	 		}
			param.add(new Timestamp(start));
		}
  		return this.find(sql.toString(), param, curCount/10, 10);
	}
}
