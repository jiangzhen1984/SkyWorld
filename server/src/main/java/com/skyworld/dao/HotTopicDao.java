package com.skyworld.dao;

import java.util.List;

import com.skyworld.service.po.SWPHotTopic;


public interface HotTopicDao extends BaseDao{
	
	/**
	 * ��ѯ�ȵ㻰���б�
	 */
	public List<SWPHotTopic> findHotTopicList(int opt, int type, int curCount, long start);
}
