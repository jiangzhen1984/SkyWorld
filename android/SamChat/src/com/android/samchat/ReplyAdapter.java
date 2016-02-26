package com.android.samchat;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReplyAdapter extends BaseAdapter {
	private Context mContext;
	private List<ReplyBean> list;

	public ReplyAdapter(Context mContext) {
		this.mContext = mContext; 
	}

	public void setData( List<ReplyBean> list){
		this.list = list;
	}
	
	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		if (list != null) {
			return list.get(arg0);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int arg0) {
		if (list != null) {
			return arg0;
		} else {
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		HolderView holderView = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.reply,
					null);
			holderView = new HolderView();
			holderView.commentImg = (ImageView) convertView
					.findViewById(R.id.comment_img);
			holderView.commentTitle = (TextView) convertView
					.findViewById(R.id.comment_title);
			holderView.commentContent = (TextView) convertView
					.findViewById(R.id.comment_context);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}

		ReplyBean cc = list.get(position);
		String showTitil = cc.getrUser(); 
//		holderView.commentTitle.setVisibility(View.VISIBLE);
		holderView.commentContent.setVisibility(View.VISIBLE);
		SpannableStringBuilder builder = new SpannableStringBuilder(
				showTitil+cc.getrCotent());
		ForegroundColorSpan blueSpan = new ForegroundColorSpan(
				Color.parseColor("#3366cc"));
		builder.setSpan(blueSpan, 0, showTitil.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		holderView.commentContent.setText(builder);
		return convertView;
	}

	class HolderView {
		ImageView commentImg;
		TextView commentTitle;
		TextView commentContent;
		TextView commentTime;
	}
}

