package com.android.samchat;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMGroup;

public class GroupAdapter extends ArrayAdapter<EMGroup> {
	public static final int GROUP_CREATED = 0;
	public static final int GROUP_EXISTED = 1;
	public static final int GRROUP_TYPE_MAX = GROUP_EXISTED+1;
	
	private LayoutInflater inflater;
	private String newGroup;

	public GroupAdapter(Context context, int res, List<EMGroup> groups) {
		super(context, res, groups);
		this.inflater = LayoutInflater.from(context);
		newGroup = context.getResources().getString(R.string.The_new_group_chat);
	}

	@Override
	public int getViewTypeCount() {
		return GRROUP_TYPE_MAX;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return GROUP_CREATED;
		} else {
			return GROUP_EXISTED;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == GROUP_CREATED) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.sm_create_group_item,parent,false);
			}
			((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.em_create_group);
			((TextView) convertView.findViewById(R.id.name)).setText(newGroup);
		} else {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.sm_row_group,parent,false);
			}
			((TextView) convertView.findViewById(R.id.name)).setText(getItem(position - 1).getGroupName());

		}

		return convertView;
	}

	@Override
	public int getCount() {
		return super.getCount() + 1;
	}

}