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
import com.easemob.easeui.utils.EaseUserUtils;

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
				holder.userimage_relativelayout = (RelativeLayout) convertView.findViewById(R.id.userimage_relativelayout);
				holder.userimage = (ImageView) convertView.findViewById(R.id.userimage);
				holder.username = (TextView)convertView.findViewById(R.id.username);
				holder.question = (TextView)convertView.findViewById(R.id.question);
				holder.badge = new BadgeView(mContext,holder.userimage_relativelayout);
				holder.badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			}
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
		case TYPE_TEXT:
			ReceivedQuestion question = receivedQuestionArray.get(position);
			if(holder.user == null){
				holder.user = SamService.getInstance().getDao().query_ContactUser_db(question.getcontactuserid());
			}

			holder.userimage.setImageResource(R.drawable.samqa);
			
			if(holder.user!=null){
				AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db(holder.user.getphonenumber());
				if(rd!=null && rd.getavatarname()!=null){
					SamLog.e(TAG,"rd is existed:"+holder.userimage.getHeight()+":"+holder.userimage.getWidth());
					Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
												   43,
												   43);
					if(bp!=null){
						SamLog.e(TAG,"bp is existed");
						holder.userimage.setImageBitmap(bp);
					}
				}
			}
			
			holder.username.setText(holder.user.getusername());
			holder.question.setText(question.question);
			if(question.shown == ReceivedQuestion.NOT_SHOWN){
				holder.badge.setText("1");
				holder.badge.show();
			}else{
				if(holder.badge.isShown()){
					holder.badge.hide();
				}
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
		public RelativeLayout userimage_relativelayout;
		public ImageView userimage;
		public TextView username;
		public TextView question;
		public BadgeView badge;
		public ContactUser user;
	}
	
	
	
}

