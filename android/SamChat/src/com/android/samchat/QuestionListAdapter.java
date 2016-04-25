package com.android.samchat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedQuestion;

public class QuestionListAdapter extends BaseAdapter{
	static private String TAG = "QuestionListAdapter";
	

	private final int TYPE_TEXT = 0;
	private final int TYPE_VOICE=1;
	
	private final int TYPE_MAX = TYPE_VOICE + 1;


	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = 20;

	private List<ReceivedQuestion> receivedQuestionArray;

	public void setReceivedQuestionArray(List<ReceivedQuestion> array){
		receivedQuestionArray = array;
	}

	public List<ReceivedQuestion> getReceivedQuestionArray(){
		return receivedQuestionArray;
	}

	public QuestionListAdapter(Context context){
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
		return TYPE_TEXT;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_TEXT){
				convertView = mInflater.inflate(R.layout.question_list_item,parent,false);
				holder.image_relativelayout = (RelativeLayout) convertView.findViewById(R.id.image_relativelayout);
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.question_flag= (TextView)convertView.findViewById(R.id.question_flag);
				holder.question = (TextView)convertView.findViewById(R.id.question);
			}
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
		case TYPE_TEXT:
			ReceivedQuestion question = receivedQuestionArray.get(position);
						
			holder.question.setText(question.question);
			if(question.response== ReceivedQuestion.NOT_RESPONSED){
				holder.image.setImageResource(R.drawable.samqa_unresp);
				holder.question_flag.setText(mContext.getString(R.string.unresponsed_question));
				holder.question_flag.setTextColor(mContext.getResources().getColor(R.color.orange));
			}else{
				holder.image.setImageResource(R.drawable.samqa);
				holder.question_flag.setText(mContext.getString(R.string.responsed_question));
				holder.question_flag.setTextColor(mContext.getResources().getColor(R.color.common_bg_green));
			}
			
			
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
		public RelativeLayout image_relativelayout;
		public ImageView image;
		public TextView question_flag;
		public TextView question;
		//public ContactUser user;
	}
	
	
	
}

