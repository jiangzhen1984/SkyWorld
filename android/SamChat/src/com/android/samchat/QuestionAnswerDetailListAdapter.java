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

public class QuestionAnswerDetailListAdapter extends BaseAdapter{
	static private String TAG = "QuestionAnswerDetailListAdapter";
	

	private final int TYPE_QUESTION = 0;
	private final int TYPE_ANSWER = 1;
	
	private final int TYPE_MAX = TYPE_ANSWER + 1;


	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = 20;

	private ContactUser user;
	private ReceivedQuestion receivedQuest;
	private List<SendAnswer> sendAnswerArray;

	public void setContactUser(ContactUser user){
		this.user = user;
	}

	public void setReceivedQuestion(ReceivedQuestion receivedQuest){
		this.receivedQuest = receivedQuest;
	}

	public ReceivedQuestion getReceivedQuestion(){
		return this.receivedQuest;
	}

	public void setSendAnswerArray(List<SendAnswer> sendAnswerArray){
		this.sendAnswerArray = sendAnswerArray;
	}

	public List<SendAnswer> getSendAnswerArray(){
		return this.sendAnswerArray;
	}

	public QuestionAnswerDetailListAdapter(Context context){
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
		return position==0?TYPE_QUESTION:TYPE_ANSWER;
	}
	
	@Override
	public View getView(int position,View convertView, ViewGroup parent){
		int viewType = getItemViewType(position);
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			if(viewType == TYPE_QUESTION){
				convertView = mInflater.inflate(R.layout.question_detail_list_item,parent,false);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.userimage = (ImageView)convertView.findViewById(R.id.userimage);
				holder.question = (TextView)convertView.findViewById(R.id.question);
			}else if(viewType == TYPE_ANSWER){
				convertView = mInflater.inflate(R.layout.answer_detail_list_item_right,parent,false);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.userimage =  (ImageView)convertView.findViewById(R.id.userimage);
				holder.answer =  (TextView)convertView.findViewById(R.id.answershow);
				holder.processimage = (ImageView)convertView.findViewById(R.id.sendprocessbar);
			}
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
		Date curDate;
		
		switch(viewType){
			case TYPE_QUESTION:
				ReceivedQuestion question = receivedQuest;
				long time = question.receivedtime;//System.currentTimeMillis();
				SamLog.e(TAG,"time:"+time);
				curDate = new Date(time);   
				String strRecvTime = formatter.format(curDate); 
				holder.date.setText(strRecvTime);
				holder.userimage.setImageResource(R.drawable.samqa);

				if(user!=null){
					AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(user.getusername());
					if(rd!=null && rd.getavatarname()!=null){
						SamLog.e(TAG,"rd is existed:"+holder.userimage.getHeight()+":"+holder.userimage.getWidth());
						Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
												   45,
												   45);
						if(bp!=null){
							SamLog.e(TAG,"bp is existed");
							holder.userimage.setImageBitmap(bp);
						}
					}
				}

				
				holder.question.setText(question.question);
			
				break;
			case TYPE_ANSWER:
				SendAnswer answer = sendAnswerArray.get(position-1);
				curDate = new Date(answer.sendtime);   
				String strSendTime = formatter.format(curDate);
				holder.date.setText(strSendTime);
				holder.userimage.setImageResource(R.drawable.samqa);

				LoginUser currentuser = SamService.getInstance().get_current_user();
				AvatarRecord rd = SamService.getInstance().getDao().query_AvatarRecord_db_by_username(currentuser.getusername());
				if(rd!=null && rd.getavatarname()!=null){
					SamLog.e(TAG,"rd is existed:"+holder.userimage.getHeight()+":"+holder.userimage.getWidth());
					Bitmap bp = EaseUserUtils.decodeFile(SamService.sam_cache_path+SamService.AVATAR_FOLDER+"/"+rd.getavatarname(), 
											   45,
											   45);
					if(bp!=null){
						SamLog.e(TAG,"bp is existed");
						holder.userimage.setImageBitmap(bp);
					}
				}


				
				holder.answer.setText(answer.answer);

				int status = answer.getstatus();
				if(status ==SendAnswer.SEND_ING){
					Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.sendaprocess); 
					LinearInterpolator lin = new LinearInterpolator(); 
					operatingAnim.setInterpolator(lin);
 					holder.processimage.startAnimation(operatingAnim); 
				}else if(status == SendAnswer.SEND_FAILED){
					holder.processimage.clearAnimation(); 
					holder.processimage.setImageResource(R.drawable.failed);
				}else if(status == SendAnswer.SEND_SUCCEED){
					holder.processimage.clearAnimation(); 
					holder.processimage.setVisibility(View.INVISIBLE);
					getReceivedQuestion().setresponse(ReceivedQuestion.RESPONSED);
					SamService.getInstance().getDao().add_update_ReceivedQuestion_db(getReceivedQuestion());
				}else{
					SamLog.e(TAG,"Send Answer status: send_others,never run here");
					holder.processimage.clearAnimation(); 
					holder.processimage.setVisibility(View.INVISIBLE);
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
		public TextView date;
		public ImageView userimage;
		public TextView question;
		public TextView answer;
		public ImageView processimage;
		
	}
	
	
	
}

