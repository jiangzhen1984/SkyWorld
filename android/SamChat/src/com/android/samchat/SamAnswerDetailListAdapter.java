package com.android.samchat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
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

public class SamAnswerDetailListAdapter extends BaseAdapter{
	static private String TAG = "SamAnswerDetailListAdapter";
	

	private final int TYPE_ANSWER = 1;
	
	private final int TYPE_MAX = TYPE_ANSWER + 1;


	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = 1;

	private ReceivedAnswer receivedAnswer;

	public void setReceivedAnswer(ReceivedAnswer receivedAnswer){
		this.receivedAnswer = receivedAnswer;
	}

	public ReceivedAnswer getReceivedAnswer(){
		return this.receivedAnswer;
	}
	
	public SamAnswerDetailListAdapter(Context context){
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
		return TYPE_ANSWER;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_ANSWER){
				convertView = mInflater.inflate(R.layout.sam_answer_detail_list_item,parent,false);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.answer =  (TextView)convertView.findViewById(R.id.answer);
			}
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
		Date curDate;
		
		switch(viewType){
			case TYPE_ANSWER:
				curDate = new Date(receivedAnswer.receivedtime);   
				String strRecvTime = formatter.format(curDate);
				holder.date.setText(strRecvTime);
				holder.answer.setText(receivedAnswer.answer);
			
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
		public TextView date;
		public TextView answer;
		
	}
	
	
	
}


