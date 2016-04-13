package com.skyworld.dao;

import java.util.List;

import com.skyworld.service.po.SWPHotTopic;


public interface HotTopicDao extends BaseDao{
	
	/**
	 * 查询热点话题列表
	 */
	public List<SWPHotTopic> findHotTopicList(int opt, int type, int curCount, long start);
}
