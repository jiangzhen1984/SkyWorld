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
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.AvatarRecord;
import com.android.samservice.info.ContactUser;
import com.android.samservice.info.ReceivedAnswer;
import com.easemob.easeui.utils.EaseUserUtils;

public class SearchListAdapter extends BaseAdapter{
	static private String TAG = "SearchListAdapter";

	public static final int LIST_TYPE_TOP_SEARCH = 0;
	public static final int LIST_TYPE_ANSWER = 1;

	
	private final int TYPE_TOPIC = 0;
	private final int TYPE_ANSWER=1;
	private final int TYPE_FLAG=2;
	
	private final int TYPE_MAX = TYPE_FLAG + 1;
	
	private int list_type;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = 5;

	private List<ReceivedAnswer> answerArray = new ArrayList<ReceivedAnswer>();
	private List<readOrNot> isReadArray = new ArrayList<readOrNot>();
	
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
		if(list_type == LIST_TYPE_TOP_SEARCH){
			return TYPE_TOPIC;
		}else{
			if(position+1 == mCount){
				return TYPE_FLAG;
			}else{
				return TYPE_ANSWER;
			}
		}
		
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_TOPIC){
				convertView = mInflater.inflate(R.layout.top_search_list_item,parent,false);
				holder.search_info = (TextView) convertView.findViewById(R.id.top_search_item);
			}else if(viewType == TYPE_ANSWER){
				convertView = mInflater.inflate(R.layout.answer_list_item,parent,false);
				holder.userimage = (ImageView) convertView.findViewById(R.id.userimage);
				holder.username = (TextView)convertView.findViewById(R.id.username);
				holder.answer = (TextView)convertView.findViewById(R.id.answer);
			}else{
				convertView = mInflater.inflate(R.layout.flag_list_item,parent,false);
				holder.flag = (TextView)convertView.findViewById(R.id.flag);
			}
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		switch(viewType){
		case TYPE_TOPIC:
			if(position ==0)	holder.search_info.setText("硅谷比较好的学区在哪里");
			else if(position == 1) holder.search_info.setText("女儿去美国 读高中,怎么样才能找到合适的寄宿家庭");
			else if(position == 2) holder.search_info.setText("如何得到医院的医疗补助");
			else if(position == 3) holder.search_info.setText("美国的一栋房产,每年要多少花费");
			else if(position == 4) holder.search_info.setText("想去cosco,没有会员卡怎么办");
		break;
		case TYPE_ANSWER:
			ReceivedAnswer info = answerArray.get(position);
			//if(holder.user == null){
				holder.user = SamService.getInstance().getDao().query_ContactUser_db(info.getcontactuserid());
				SamLog.e(TAG,"getcontactuserid:"+info.getcontactuserid());
				SamLog.e(TAG,"show:"+holder.user.getusername());
				holder.username.setText(holder.user.getusername());
				
				boolean avatarExisted=false;
				ContactUser cuser = holder.user;
				if(cuser!=null){
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
				}

				if(!avatarExisted){
					holder.userimage.setImageResource(R.drawable.em_default_avatar);
				}
				
			//}
			
			holder.answer.setText(info.answer);
			
		break;
		case TYPE_FLAG:
			holder.flag.setText(mContext.getResources().getString(R.string.samservice_answer));
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
		public TextView answer;
		public ContactUser user;
		public TextView flag;
		
	}
	
	
	
}
