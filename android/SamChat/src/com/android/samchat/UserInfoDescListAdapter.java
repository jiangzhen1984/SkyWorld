package com.android.samchat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.*;


public class UserInfoDescListAdapter extends BaseAdapter{
	static private String TAG = "UserInfoDescListAdapter";
	

	private final int TYPE_DESC = 1;
	
	private final int TYPE_MAX = TYPE_DESC + 1;


	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount;
	private String desc;

	public void setDesc(String desc){
		this.desc = desc;
	}

	public String getDesc(){
		return this.desc;
	}
	
	public UserInfoDescListAdapter(Context context){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	public void setCount(int count){
		mCount = count;
	}
	
	@Override
	public int getCount(){
		return mCount;
	}
	
	@Override
	public int getViewTypeCount(){
		return TYPE_MAX;
	}
	
	@Override
	public int getItemViewType(int position){
		return TYPE_DESC;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_DESC){
				convertView = mInflater.inflate(R.layout.desc_list_item,parent,false);
				holder.desc= (TextView) convertView.findViewById(R.id.desc);
			}
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
			case TYPE_DESC:
				if(desc!=null)
					holder.desc.setText(desc);
				
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
		public TextView desc;
	}
	
	
	
}




