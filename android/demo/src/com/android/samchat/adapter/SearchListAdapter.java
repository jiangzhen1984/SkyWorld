package com.android.samchat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;
import com.netease.nim.demo.R;

public class SearchListAdapter extends BaseAdapter{
	static private String TAG = "SearchListAdapter";
	
	private final int TYPE_TOPIC = 0;
	private final int TYPE_MAX = TYPE_TOPIC + 1;
	
	private Context mContext;
	private LayoutInflater mInflater;

	private List<String> hotTopicArray=null;

	public void setHotTopicArray(List<String> hotTopicArray){
		this.hotTopicArray = hotTopicArray;
	}

	public List<String> getHotTopicArray(){
		return hotTopicArray;
	}

	public SearchListAdapter(Context context){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount(){
		return hotTopicArray.size();
	}
	
	@Override
	public int getViewTypeCount(){
		return TYPE_MAX;
	}
	
	@Override
	public int getItemViewType(int position){
		return TYPE_TOPIC;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.top_search_list_item,parent,false);
			holder.search_info = (TextView) convertView.findViewById(R.id.top_search_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
		case TYPE_TOPIC:
			holder.search_info.setText(hotTopicArray.get(position));
			break;
		}
		
		return convertView;
	}
	
	@Override
	public long getItemId(int position){
		return position;
	}
	
	@Override
	public String getItem(int position){
		return null;
	}
	
	
	public static class ViewHolder{
		public TextView search_info;
	}
	
	
	
}

