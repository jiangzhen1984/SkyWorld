package com.android.samchat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SoftInputMonitorLinearLayout extends LinearLayout{   
	private InputWindowListener listener;
         
	public SoftInputMonitorLinearLayout(Context context, AttributeSet attrs) {   
	    super(context, attrs);   
	}   
	   
	@Override   
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {       
	    super.onSizeChanged(w, h, oldw, oldh);   
		if (oldh > h) {
			listener.show();
		} else{
			listener.hidden();
		}  
	}
	   
	@Override   
	protected void onLayout(boolean changed, int l, int t, int r, int b) {   
	    super.onLayout(changed, l, t, r, b);   
	}   
	   
	@Override   
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
	       
	} 

	public void setListener(InputWindowListener listener) {
		this.listener = listener;
	}

	public interface InputWindowListener {
		void show();

		void hidden();
	}
}