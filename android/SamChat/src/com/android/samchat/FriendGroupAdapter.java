package com.android.samchat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.samservice.*;
import com.android.samservice.info.*;
import com.easemob.easeui.utils.EaseUserUtils;

public class FriendGroupAdapter extends BaseAdapter{
	static private String TAG = "FriendGroupAdapter";
	

	private final int TYPE_NORMAL=1;
	private final int TYPE_VEDIO = 2;
	private final int TYPE_SHARE_TEXT = 3;
	
	private final int TYPE_MAX = TYPE_SHARE_TEXT + 1;


	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount=0;
	private List<FGRecord> rdList;

	private Handler inputhandler;

	public FriendGroupAdapter(Context context,Handler inputhandler){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.inputhandler = inputhandler;
	}

	public void setFGRecordList(List<FGRecord> rdList ){
		this.rdList = rdList;
	}

	public List<FGRecord> getFGRecordList(){
		return this.rdList;
	}
	
	public void setCount(int count){
		mCount = count;
	}

	private String getNickName(String phonenumber){
		if(SamService.getInstance().get_current_user().getphonenumber().equals(phonenumber)){
			return SamService.getInstance().get_current_user().getusername();
		}else{
			ContactUser user = SamService.getInstance().getDao().query_ContactUser_db(phonenumber);
			if(user!=null){
				return user.getusername();
			}else{
				return null;
			}
		}
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
		return TYPE_NORMAL;
	}
	
	@Override
	public View getView(final int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_NORMAL){
				convertView = mInflater.inflate(R.layout.fg_list_item_normal,parent,false);

				holder.publisher_img = (ImageView) convertView.findViewById(R.id.publisher_img);
				holder.publisher_nickname = (TextView) convertView.findViewById(R.id.publisher_nickname);
				holder.comment = (TextView) convertView.findViewById(R.id.comment);
				holder.one_img = (ImageView) convertView.findViewById(R.id.one_img);
				holder.nine_img_layout = (RelativeLayout) convertView.findViewById(R.id.nine_img_layout);
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_1));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_2));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_3));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_4));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_5));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_6));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_7));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_8));
				holder.imgList.add((ImageView) convertView.findViewById(R.id.img_9));
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.img_comment = (ImageView) convertView.findViewById(R.id.img_comment);
				holder.comments_list = (MyWrapListView) convertView.findViewById(R.id.comments_list);

				holder.listAdapter = new ReplyAdapter(mContext);
				holder.comments_list.setAdapter(holder.listAdapter);

				convertView.setTag(holder);
				
			}
			
			
			
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.img_comment.setTag(Integer.valueOf(position));
		holder.img_comment.setOnClickListener(new View.OnClickListener() {  
			@Override  
			public void onClick(View v) { 
				Message msg = inputhandler.obtainMessage(0);
				msg.arg1 = (Integer) v.getTag();
				inputhandler.sendMessage(msg);              
			}  
		});

		holder.one_img.setTag(Integer.valueOf(position));
		holder.one_img.setOnClickListener(new View.OnClickListener() {  
			@Override  
			public void onClick(View v) {
				int position = (Integer) (v.getTag());
				SamDBDao dao = SamService.getInstance().getDao();
				FGRecord rd = rdList.get(position);
				List<PictureRecord> picList = dao.query_PictureRecord_db(rd.getfg_id());
				if(picList.size() == 0){
					return;
				}else{
					SamLog.e(TAG,"get postiont:"+position);
					ArrayList<String> pathList = new ArrayList<String>();
					for(PictureRecord prd: picList){
						pathList.add(prd.getthumbnail_pic());
					}
					Message msg = inputhandler.obtainMessage(1);
					msg.arg1 = 0;
					msg.obj = pathList;
					inputhandler.sendMessage(msg);    
				}         
			}  
		});

		for(int i=0;i<holder.imgList.size();i++){
			final int  indication=i;
			holder.imgList.get(i).setTag(Integer.valueOf(position));
			holder.imgList.get(i).setOnClickListener(new View.OnClickListener() {  
				@Override  
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					//SamLog.e(TAG,"imgList:"+indication+" onclick");
					SamDBDao dao = SamService.getInstance().getDao();
					FGRecord rd = rdList.get(position);
					//SamLog.e(TAG,"get postiont:"+position+"indication:"+indication);
					List<PictureRecord> picList = dao.query_PictureRecord_db(rd.getfg_id());
					if(picList.size() == 0){
						return;
					}else{
						ArrayList<String> pathList = new ArrayList<String>();
						for(PictureRecord prd: picList){
							pathList.add(prd.getthumbnail_pic());
						}
						Message msg = inputhandler.obtainMessage(1);
						msg.arg1 = indication;
						msg.obj = pathList;
		 				inputhandler.sendMessage(msg);    
					}         
				}  
			});
		}

		switch(viewType){
			case TYPE_NORMAL:
				FGRecord rd = rdList.get(position);
				SamDBDao dao = SamService.getInstance().getDao();
				String publisher_phonenumber = rd.getpublisher_phonenumber();
				ContactUser user = dao.query_ContactUser_db(publisher_phonenumber);
				String nickname = null;
				if(user ==  null){
					nickname = SamService.getInstance().get_current_user().getusername();
				}else{
				 	nickname = user.getusername();
				 }
				AvatarRecord ard = dao.query_AvatarRecord_db(publisher_phonenumber);
				if(ard!=null && ard.getavatarname()!=null){
					Bitmap bp = null;
					bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+ard.getavatarname(),43,43);

					if(bp!=null){
						holder.publisher_img.setImageBitmap(bp);
					}
				}
				holder.publisher_nickname.setText(nickname);
				if(rd.getcomment()!=null){
					holder.comment.setText(rd.getcomment());
				}else{
					holder.comment.setVisibility(View.GONE);
				}

				List<PictureRecord> picList = dao.query_PictureRecord_db(rd.getfg_id());
				if(picList.size() == 0){
					holder.one_img.setVisibility(View.GONE);
					holder.nine_img_layout.setVisibility(View.GONE);
				}else if(picList.size() == 1){
					holder.one_img.setVisibility(View.VISIBLE);
					holder.nine_img_layout.setVisibility(View.GONE);
					PictureRecord picRd = picList.get(0);
					Bitmap bp = null;
					bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),240,240);

					if(bp!=null){
						holder.one_img.setImageBitmap(bp);
					}
				}else if(picList.size() > 1){
					holder.one_img.setVisibility(View.GONE);
					holder.nine_img_layout.setVisibility(View.VISIBLE);
					for(int i=0; i< picList.size() && i<9;i++){
						holder.imgList.get(i).setVisibility(View.VISIBLE);
						PictureRecord picRd = picList.get(i);
						Bitmap bp = null;
						bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),80,80);

						if(bp!=null){
							holder.imgList.get(i).setImageBitmap(bp);
						}
					}
				}

				//SamLog.e(TAG,"article:"+rd.getfg_id()+" timestamp:"+rd.gettimestamp());
				SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  hh:mm");
				Date curDate = new Date(rd.gettimestamp());
				String strTime = formatter.format(curDate);
				holder.date.setText(strTime);
				
				List<ReplyBean> rbList = new ArrayList<ReplyBean>();
				
				List<CommenterRecord> crlist = dao.query_CommenterRecord_db(rd.getfg_id());
				//SamLog.e(TAG,"article:"+rd.fg_id+"crlist size :"+crlist.size());
				for(CommenterRecord cr: crlist){
					
					ReplyBean rb = new ReplyBean();
					rb.setrUser(getNickName(cr.commenter_phonenumber)+": ");
					rb.setrCotent(cr.getcontent());
					rbList.add(rb);
				}

				holder.listAdapter.setData(rbList);
				holder.listAdapter.notifyDataSetChanged();

				//SamLog.e(TAG,"article:"+rd.fg_id+"rbList size :"+rbList.size());

				if(rbList.size()>0){
					Message message = Message.obtain();
					message.what = 0;
					message.obj = holder;
					showHeighHandle.sendMessageDelayed(message, 100);
				}
				
			break;
		}
		
		//SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
		//Date curDate;
		
		return convertView;
			
	}
		
	protected Handler showHeighHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ViewHolder holderView = (ViewHolder) msg.obj;
				int n = 0;
				for (int i = 0; i < holderView.listAdapter.getCount(); i++) {
					View aView = holderView.listAdapter.getView(i, null,
							holderView.comments_list);
					aView.measure(0, 0);
					n = n + aView.getMeasuredHeight();
				}
				android.view.ViewGroup.LayoutParams lp = holderView.comments_list
						.getLayoutParams();
				lp.height = n
						+ (holderView.comments_list.getDividerHeight() * (holderView.listAdapter
								.getCount() - 1));
				holderView.comments_list.setLayoutParams(lp);
				break;
			default:
				break;
			}
		}
	};

	public static int dipChangePx(float dpValue, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
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
		public ImageView publisher_img;
		public TextView publisher_nickname;
		public TextView comment;
		public ImageView one_img;
		public RelativeLayout nine_img_layout;
		public List<ImageView> imgList = new ArrayList<ImageView>();
		public TextView date;
		public ImageView img_comment;
		public ListView comments_list;
		public ReplyAdapter listAdapter;
	}
	
	
	
}


