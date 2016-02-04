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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.*;
import com.android.samservice.info.InviteMessageRecord.InviteMessageStatus;
import com.easemob.chat.EMChatManager;
import com.easemob.easeui.utils.EaseUserUtils;
import com.easemob.exceptions.EaseMobException;

public class NewFriendListAdapter extends BaseAdapter{
	static private String TAG = "NewFriendListAdapter";
	

	private final int TYPE_FROM_SAMSYSTEM = 1;
	
	private final int TYPE_MAX = TYPE_FROM_SAMSYSTEM + 1;

	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = 0;

	private List<InviteMessageRecord> contactInviteRecordArray;

	public void setContactInviteRecordArray(List<InviteMessageRecord> contactInviteRecordArray){
		this.contactInviteRecordArray = contactInviteRecordArray;
	}

	public List<InviteMessageRecord> getContactInviteRecordArray(){
		return this.contactInviteRecordArray;
	}
	
	public NewFriendListAdapter(Context context){
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
		return TYPE_FROM_SAMSYSTEM;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_FROM_SAMSYSTEM){
				convertView = mInflater.inflate(R.layout.contact_invite_list_item,parent,false);
				holder.userimg = (ImageView) convertView.findViewById(R.id.userimg);
				holder.username =  (TextView)convertView.findViewById(R.id.username);
				holder.reason =  (TextView)convertView.findViewById(R.id.reason);
				holder.added_already = (TextView)convertView.findViewById(R.id.added_already);
				holder.action_refused_layout = (LinearLayout) convertView.findViewById(R.id.action_refused_layout);
				holder.action_refused_layout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int pos = (Integer)v.getTag();
						InviteMessageRecord rd = contactInviteRecordArray.get(pos);
						String sender_easemob = rd.getsender();
						
						try{
							EMChatManager.getInstance().refuseInvitation(sender_easemob);
							SamLog.e(TAG,"refused "+sender_easemob + " invite");
						}catch(EaseMobException e){
							e.printStackTrace(); 
						}
					}
				});
				holder.action_refused_layout.setTag(position);



				holder.action_accept_layout = (LinearLayout) convertView.findViewById(R.id.action_accept_layout);
				holder.action_accept_layout.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int pos = (Integer)v.getTag();
						InviteMessageRecord rd = contactInviteRecordArray.get(pos);
						String sender_easemob = rd.getsender();
						
						try{
							EMChatManager.getInstance().acceptInvitation(sender_easemob);
							SamLog.e(TAG,"acception "+sender_easemob + " invite");
						}catch(EaseMobException e){
							e.printStackTrace(); 
						}
					}
				});
				holder.action_accept_layout.setTag(position);
			}
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
			case TYPE_FROM_SAMSYSTEM:
				if(contactInviteRecordArray!=null && contactInviteRecordArray.size()>0){
					InviteMessageRecord record = contactInviteRecordArray.get(position);
					holder.username.setText(record.getsender());
					holder.userimg.setImageResource(R.drawable.em_default_avatar);
					ContactUser cuser = SamService.getInstance().getDao().query_ContactUser_db(record.getsender());
					if(cuser!=null){
						SamLog.e(TAG,"cuser is existed");
						holder.username.setText(cuser.getusername());
						AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db(record.getsender());
						if(rd!=null && rd.getavatarname()!=null){
							SamLog.e(TAG,"rd is existed:"+holder.userimg.getHeight()+":"+holder.userimg.getWidth());
							Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
												   43,
												   43);
							if(bp!=null){
								SamLog.e(TAG,"bp is existed");
								holder.userimg.setImageBitmap(bp);
							}
						}
					}
					
					holder.reason.setText(record.getreason());
					if(record.getstatus() == InviteMessageStatus.AGREED.ordinal()){
						holder.action_accept_layout.setVisibility(View.GONE);
						holder.action_refused_layout.setVisibility(View.GONE);
						holder.added_already.setVisibility(View.VISIBLE);
					}else if(record.getstatus() == InviteMessageStatus.BEINVITEED.ordinal()){
						holder.action_accept_layout.setVisibility(View.VISIBLE);
						holder.action_refused_layout.setVisibility(View.GONE);
						holder.added_already.setVisibility(View.GONE);
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
		public ImageView userimg;
		public TextView username;
		public TextView reason;
		public TextView added_already;
		public LinearLayout action_refused_layout;
		public LinearLayout action_accept_layout;
	}
	
	
	
}



