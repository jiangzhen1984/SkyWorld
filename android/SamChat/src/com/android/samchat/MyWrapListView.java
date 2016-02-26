package com.android.samchat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyWrapListView extends ListView {
	public MyWrapListView(Context context) {
		super(context);
	}

	public MyWrapListView(Context context, AttributeSet as) {
		super(context, as);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}

