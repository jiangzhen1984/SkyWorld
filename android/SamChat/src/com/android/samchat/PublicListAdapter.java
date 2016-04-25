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
import com.hyphenate.easeui.utils.EaseUserUtils;

public class PublicListAdapter extends BaseAdapter{
	static private String TAG = "PublicListAdapter";
	

	private final int TYPE_PUBLIC_NORMAL = 1;
	
	private final int TYPE_MAX = TYPE_PUBLIC_NORMAL + 1;


	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount;
	private List<ContactUser> publicArray = new ArrayList<ContactUser>();

	public void setPublicArray(List<ContactUser> publicArray){
		this.publicArray = publicArray;
	}

	public List<ContactUser> getPublicArray(){
		return this.publicArray;
	}
	
	public PublicListAdapter(Context context){
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
		return TYPE_PUBLIC_NORMAL;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		SamLog.e(TAG,"position:"+position);
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_PUBLIC_NORMAL){
				convertView = mInflater.inflate(R.layout.public_list_item,parent,false);
				holder.userimage = (ImageView) convertView.findViewById(R.id.userimage);
				holder.username = (TextView) convertView.findViewById(R.id.username);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.desc = (TextView) convertView.findViewById(R.id.desc);
				holder.new_msg_layout = (RelativeLayout) convertView.findViewById(R.id.new_msg_layout);
				holder.new_msg_num =  (TextView) convertView.findViewById(R.id.new_msg_num);
			}
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
		Date curDate;
		
		
		switch(viewType){
			case TYPE_PUBLIC_NORMAL:
				ContactUser cuser = publicArray.get(position);
				holder.username.setText(cuser.getusername());
				
				boolean avatarExisted=false;
				AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(cuser.getusername());
				if(rd!=null && rd.getavatarname()!=null){
					Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
											   45,
											   45);
					if(bp!=null){
						holder.userimage.setImageBitmap(bp);
						avatarExisted = true;
					}
				}

				if(!avatarExisted){
					holder.userimage.setImageResource(R.drawable.em_default_avatar);
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
		public ImageView userimage;
		public TextView username;
		public TextView date;
		public TextView desc;
		public RelativeLayout new_msg_layout;
		public TextView new_msg_num;
	}
	
	
	
}



