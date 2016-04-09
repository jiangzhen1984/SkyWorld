package com.skyworld.service.trans;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONObject;

import com.skyworld.dao.HotTopicDao;
import com.skyworld.service.po.SWPHotTopic;
import com.skyworld.service.resp.BasicResponse;
import com.skyworld.service.resp.HotTopicQueryListResponse;

public class APITransHotTopicQueryListService implements APITransService{

	private HotTopicDao hotTopicDao;

	public HotTopicDao getHotTopicDao() {
		return hotTopicDao;
	}

	public void setHotTopicDao(HotTopicDao hotTopicDao) {
		this.hotTopicDao = hotTopicDao;
	}

	public BasicResponse service(JSONObject json) {

 		JSONObject body = json.getJSONObject("body");
		int optType = body.getInt("opt_type");
		int topicType = body.getInt("topic_type");
		int curCount = body.getInt("cur_count");
		long time = body.getLong("update_time_pre");

		List<SWPHotTopic> list =  hotTopicDao.findHotTopicList(optType, topicType, curCount, time);
		Timestamp curDate = hotTopicDao.querySysdate();
		return new HotTopicQueryListResponse(list, curDate);
 
	}
}
