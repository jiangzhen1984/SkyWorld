package com.android.samchat;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;

public class SearchListAdapter extends BaseAdapter{
	static private String TAG = "SearchListAdapter";

	public static final int LIST_TYPE_TOP_SEARCH = 0;
	public static final int LIST_TYPE_ANSWER = 1;

	
	private final int TYPE_TEXT = 0;
	private final int TYPE_ANSWER=1;
	
	private final int TYPE_MAX = TYPE_ANSWER + 1;
	
	private int list_type;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = 20;

	private ArrayList<ReceivedAnswer> answerArray = new ArrayList<ReceivedAnswer>();
	private ArrayList<readOrNot> isReadArray = new ArrayList<readOrNot>();
	
	private class readOrNot{
		public boolean isRead;
		public readOrNot(boolean isRead){
			this.isRead = isRead;
		}
		public void set(boolean isRead){
			this.isRead = isRead;
		}
		
		public boolean get(){
			return isRead;
		}
	}

	public void addAnswerInfo(ReceivedAnswer answer){
		answerArray.add(answer);
		isReadArray.add(new readOrNot(false));
	}

	public void setRead(int position){
		isReadArray.get(position).set(true);
	}
	
	public boolean getRead(int position){
		return isReadArray.get(position).get();
	}

	public ReceivedAnswer getAnswerInfo(int position){
		return answerArray.get(position);
	}



	public int getCountOfAnswerInfo(){
		return answerArray.size();
	}

	public void clearAnswerInfo(){
		answerArray.clear();
	}

	public void setListType_topSearch(){
		list_type = LIST_TYPE_TOP_SEARCH;
	}

	public void setListType_answer(){
		list_type = LIST_TYPE_ANSWER;
	}

	public int getListType(){
		return list_type;
	}
	
	public SearchListAdapter(Context context){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		list_type = LIST_TYPE_TOP_SEARCH;
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
		return list_type==LIST_TYPE_TOP_SEARCH?TYPE_TEXT:TYPE_ANSWER;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(list_type == LIST_TYPE_TOP_SEARCH){
				convertView = mInflater.inflate(R.layout.top_search_list_item,parent,false);
				holder.search_info = (TextView) convertView.findViewById(R.id.top_search_item);
			}else{
				convertView = mInflater.inflate(R.layout.answer_list_item,parent,false);
				holder.userimage = (ImageView) convertView.findViewById(R.id.userimage);
				holder.username = (TextView)convertView.findViewById(R.id.username);
				holder.userdesc = (TextView)convertView.findViewById(R.id.userdesc);
				holder.answer = (TextView)convertView.findViewById(R.id.answer);
				holder.readBage = new BadgeView(mContext, holder.userimage);
			}
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
		case TYPE_TEXT:
			if(position % 2 !=0)	holder.search_info.setText("hellohellohellohellohellohellohellohellohellohellohellohellohellohellohello");
			else holder.search_info.setText("yes");
		break;
		case TYPE_ANSWER:
			ReceivedAnswer info = answerArray.get(position);
			if(holder.user == null){
				holder.user = SamService.getInstance().query_ContactUser_db(info.getcontactuserid());
				holder.username.setText(holder.user.get_username());
				holder.userdesc.setText(holder.user.get_description());
				holder.userimage.setImageResource(R.drawable.logo);
			}
			
			holder.answer.setText(info.answer);
			if(!getRead(position)){
				holder.readBage.setText("...");
				holder.readBage.show();
			}else{
				SamLog.e(TAG,"We call hide!!!!");
				holder.readBage.hide();
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
		public TextView search_info;
		public ImageView userimage;
		public TextView username;
		public TextView userdesc;
		public TextView answer;
		public BadgeView readBage;
		public ContactUser user;
		
	}
	
	
	
}
