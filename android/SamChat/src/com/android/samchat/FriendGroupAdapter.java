package com.android.samchat;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
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

	private final int FIX_KEEP_START = 5;
	private final int START_POSTION_FROM_CLEAR = 10;
	private final int GAP_KEEP_FROM_CLEAR = 5;

	private final int MSG_DECODE_IMAGE = 1;

	List<Bitmap> bitmap_list;

	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount=0;
	private List<FGRecord> rdList;
	private ListView mListView;

	private Handler inputhandler;

	private  boolean mBusy=false;
	private Map<Long, List<Bitmap> > bitmapMap = new HashMap<Long, List<Bitmap>>();
	private Object mapLock = new Object();

	private HandlerThread mLoadDecoderThread;
    	private LoadDecoderHandler mLoadDecoderHandler;


	private void sendDecodeImageMsg(long article_id){
		ArticleID ainfo = new ArticleID();
		ainfo.article_id = article_id;
		Message msg = mLoadDecoderHandler.obtainMessage(MSG_DECODE_IMAGE, ainfo);
		mLoadDecoderHandler.sendMessage(msg);
	}


	private void recycleBitMap(int position){
		FGRecord rd = rdList.get(position);
		long fg_id = rd.getfg_id();


		List<Bitmap> bitmapList = bitmapMap.get(Long.valueOf(fg_id));
		if(bitmapList!=null && bitmapList.size()>0){
			for(int i=0; i< bitmapList.size(); i++){
				Bitmap bp = bitmapList.get(i);
				if (bp!=null && !bp.isRecycled()) {  
             			 bp.recycle();
				}
				bitmapList.remove(i);
			}

			bitmapMap.remove(Long.valueOf(fg_id));
		}

		
	}

	private void clearBitmapMap(int first, int last){
		int start = 0;
		int end = 0;

		SamLog.e(TAG,"first:"+first+" last:"+last);
		synchronized(mapLock){
		
			if(first >= START_POSTION_FROM_CLEAR && last >=first ){
				//clear bitmap which is before first visible
				if(first - GAP_KEEP_FROM_CLEAR >= FIX_KEEP_START){
					start = FIX_KEEP_START;
					end = first - GAP_KEEP_FROM_CLEAR;
					for(int i=start; i<=end; i++){
						recycleBitMap(i);
					}
				}
				//clear bitmap which is after last visible
				if(last + GAP_KEEP_FROM_CLEAR <= rdList.size()-1){
					start = last + GAP_KEEP_FROM_CLEAR;
					end = rdList.size()-1;
					for(int i=start; i<=end; i++){
						recycleBitMap(i);
					}				
				}
			
			}
		}
	}

	

	public FriendGroupAdapter(Context context,ListView list, Handler inputhandler){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.inputhandler = inputhandler;
		this.mListView = list;
	}


	public AbsListView.OnScrollListener getScrollListener(){
		return onScrollListener;
	} 
	
	private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			SamLog.i(TAG,"OnScrollListener is running");
			int first;
			int count;
			int last;
			switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
					mBusy = false;
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					mBusy = false;
					first = view.getFirstVisiblePosition();
					count = view.getChildCount();
					last = view.getLastVisiblePosition();
					SamLog.e(TAG,"clearBitmapMap:"+first+" "+last);
					clearBitmapMap(first, last);

					//notifyDataSetChanged();
						
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					mBusy = false;
					first = view.getFirstVisiblePosition();
					count = view.getChildCount();
					last = view.getLastVisiblePosition();
					clearBitmapMap(first, last);
					break;
	
				default:
					break;
			}
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			SamLog.i(TAG,"onScroll is running");
			
		}
	};
	

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
					SamDBDao dao = SamService.getInstance().getDao();
					FGRecord rd = rdList.get(position);
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
					LoginUser luser = dao.query_LoginUser_db(publisher_phonenumber);
					if(luser!=null){
						nickname = luser.getusername();
					}else{
						nickname="";
					}
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

				if(mBusy){
					if(picList.size() == 0){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.GONE);
					}else if(picList.size() == 1){
						holder.one_img.setVisibility(View.VISIBLE);
						holder.nine_img_layout.setVisibility(View.GONE);
						holder.one_img.setImageResource(R.drawable.default_image);
					}else if(picList.size() > 1){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.VISIBLE);
						for(int i=0; i< picList.size() && i<9;i++){
							holder.imgList.get(i).setVisibility(View.VISIBLE);
							holder.imgList.get(i).setImageResource(R.drawable.default_image);
						}
					}
				}else{
					if(picList.size() == 0){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.GONE);
					}else if(picList.size() == 1){
						holder.one_img.setVisibility(View.VISIBLE);
						holder.nine_img_layout.setVisibility(View.GONE);
						
						List<Bitmap> bitmapList =  bitmapMap.get(Long.valueOf(rd.getfg_id()));
						if(bitmapList == null || bitmapList.size() == 0){
							//holder.one_img.setImageResource(R.drawable.default_image);
							//sendDecodeImageMsg(rd.getfg_id());
						
							PictureRecord picRd = picList.get(0);
							Bitmap bp = null;
							bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),240,240);

							if(bp!=null){
								holder.one_img.setImageBitmap(bp);
								bitmapList =  new ArrayList<Bitmap>();
								bitmapList.add(bp);
								bitmapMap.put(Long.valueOf(rd.getfg_id()),bitmapList);
							}
						}else{
							Bitmap bp = bitmapList.get(0);
							holder.one_img.setImageBitmap(bp);
						}
					}else if(picList.size() > 1){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.VISIBLE);
						List<Bitmap> bitmapList =  bitmapMap.get(Long.valueOf(rd.getfg_id()));
						if(bitmapList == null || bitmapList.size() == 0){
							bitmapList =  new ArrayList<Bitmap>();
							for(int i=0; i< picList.size() && i<9;i++){
								holder.imgList.get(i).setVisibility(View.VISIBLE);
							//	holder.imgList.get(i).setImageResource(R.drawable.default_image);
								
								PictureRecord picRd = picList.get(i);
								Bitmap bp = null;
								bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),80,80);

								if(bp!=null){
									holder.imgList.get(i).setImageBitmap(bp);
									bitmapList.add(bp);									
								}
							}
							//sendDecodeImageMsg(rd.getfg_id());
							bitmapMap.put(Long.valueOf(rd.getfg_id()),bitmapList);
						}else{
							for(int i=0; i< bitmapList.size() && i<9;i++){
								holder.imgList.get(i).setVisibility(View.VISIBLE);
								
								Bitmap bp = bitmapList.get(i);
								if(bp!=null){
									holder.imgList.get(i).setImageBitmap(bp);								
								}
							}
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
		
		return convertView;
			
	}

	
	
	/*@Override
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
					SamDBDao dao = SamService.getInstance().getDao();
					FGRecord rd = rdList.get(position);
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
					LoginUser luser = dao.query_LoginUser_db(publisher_phonenumber);
					if(luser!=null){
						nickname = luser.getusername();
					}else{
						nickname="";
					}
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

				if(mBusy){
					if(picList.size() == 0){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.GONE);
					}else if(picList.size() == 1){
						holder.one_img.setVisibility(View.VISIBLE);
						holder.nine_img_layout.setVisibility(View.GONE);
						holder.one_img.setImageResource(R.drawable.default_image);
					}else if(picList.size() > 1){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.VISIBLE);
						for(int i=0; i< picList.size() && i<9;i++){
							holder.imgList.get(i).setVisibility(View.VISIBLE);
							holder.imgList.get(i).setImageResource(R.drawable.default_image);
						}
					}
				}else{
					if(picList.size() == 0){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.GONE);
					}else if(picList.size() == 1){
						holder.one_img.setVisibility(View.VISIBLE);
						holder.nine_img_layout.setVisibility(View.GONE);
						
						List<Bitmap> bitmapList =  bitmapMap.get(Long.valueOf(rd.getfg_id()));
						if(bitmapList == null || bitmapList.size() == 0){
							//holder.one_img.setImageResource(R.drawable.default_image);
							//sendDecodeImageMsg(rd.getfg_id());
						
							PictureRecord picRd = picList.get(0);
							Bitmap bp = null;
							bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),240,240);

							if(bp!=null){
								holder.one_img.setImageBitmap(bp);
								bitmapList =  new ArrayList<Bitmap>();
								bitmapList.add(bp);
								bitmapMap.put(Long.valueOf(rd.getfg_id()),bitmapList);
							}
						}else{
							Bitmap bp = bitmapList.get(0);
							holder.one_img.setImageBitmap(bp);
						}
					}else if(picList.size() > 1){
						holder.one_img.setVisibility(View.GONE);
						holder.nine_img_layout.setVisibility(View.VISIBLE);
						List<Bitmap> bitmapList =  bitmapMap.get(Long.valueOf(rd.getfg_id()));
						if(bitmapList == null || bitmapList.size() == 0){
							bitmapList =  new ArrayList<Bitmap>();
							for(int i=0; i< picList.size() && i<9;i++){
								holder.imgList.get(i).setVisibility(View.VISIBLE);
							//	holder.imgList.get(i).setImageResource(R.drawable.default_image);
								
								PictureRecord picRd = picList.get(i);
								Bitmap bp = null;
								bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),80,80);

								if(bp!=null){
									holder.imgList.get(i).setImageBitmap(bp);
									bitmapList.add(bp);									
								}
							}
							//sendDecodeImageMsg(rd.getfg_id());
							bitmapMap.put(Long.valueOf(rd.getfg_id()),bitmapList);
						}else{
							for(int i=0; i< bitmapList.size() && i<9;i++){
								holder.imgList.get(i).setVisibility(View.VISIBLE);
								
								Bitmap bp = bitmapList.get(i);
								if(bp!=null){
									holder.imgList.get(i).setImageBitmap(bp);								
								}
							}
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
		
		return convertView;
			
	}*/
		
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


	public void InitHandlerThread(Activity hostActivity){
    		mLoadDecoderThread = new HandlerThread("LoadDecoder");
    		mLoadDecoderThread.start();
    		mLoadDecoderHandler = new LoadDecoderHandler(mLoadDecoderThread.getLooper(),hostActivity);
	}

	public void stopLoadDecoderThread(){
		mLoadDecoderHandler.removeMessages(MSG_DECODE_IMAGE);
		mLoadDecoderThread.getLooper().quit();
	}
	
	private final class LoadDecoderHandler extends Handler{

		private WeakReference<Activity> mWeakActivity;
		
		public LoadDecoderHandler(Looper looper,Activity HostActivity)
		{
		   super(looper);
		   mWeakActivity = new WeakReference<Activity>(HostActivity);
		}

		@Override
		public void handleMessage(Message msg){
			Activity hostActivity = mWeakActivity.get();
			if(hostActivity == null){
				return;
			}

			switch(msg.what){
				case MSG_DECODE_IMAGE:
					ArticleID ainfo = (ArticleID)msg.obj;
					long article_id = ainfo.article_id;
					SamDBDao dao = SamService.getInstance().getDao();
					List<PictureRecord> picList = dao.query_PictureRecord_db(article_id);
					List<Bitmap> bitmapList  =  new ArrayList<Bitmap>();
					for(int i=0; i< picList.size() && i<9;i++){
						PictureRecord picRd = picList.get(i);
						Bitmap bp = null;
						if(picList.size() == 1){
							bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),240,240);
						}else{
							bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+picRd.getthumbnail_pic(),80,80);
						}
						
						if(bp!=null){
							bitmapList.add(bp);									
						}
					}

					synchronized(mapLock){
						bitmapMap.put(Long.valueOf(article_id),bitmapList);
					}

					inputhandler.sendEmptyMessage(2);
									
					
				break;
			}


			
		}
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

	public static class ArticleID{
		public long article_id;
	}
	
	
	
}


