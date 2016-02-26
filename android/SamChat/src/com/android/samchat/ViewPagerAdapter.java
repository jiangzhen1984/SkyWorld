package com.android.samchat; 
   
import java.util.ArrayList;
import java.util.List; 

import com.android.samservice.SamService;
import com.easemob.easeui.utils.EaseUserUtils;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter; 
import android.support.v4.view.ViewPager; 
import android.view.View; 
import android.widget.ImageView;
   
public class ViewPagerAdapter extends PagerAdapter { 
   
	List<View> viewLists; 
	ArrayList<String>page_list;
       
	public ViewPagerAdapter(List<View> lists,ArrayList<String>page_list) 
	{ 
		this.viewLists = lists;
		this.page_list = page_list;
	} 
   
    //获得size 
    @Override 
    public int getCount() {  
        return viewLists.size(); 
    } 
   
    @Override 
    public boolean isViewFromObject(View arg0, Object arg1) {                          
        return arg0 == arg1; 
    } 
       
    //销毁Item 
    @Override 
    public void destroyItem(View view, int position, Object object) 
    { 
        ((ViewPager) view).removeView(viewLists.get(position)); 
    } 
       
	//实例化Item 
	@Override 
	public Object instantiateItem(View view, int position) 
	{ 
		ImageView iv = (ImageView)viewLists.get(position).findViewById(R.id.imageView1);
		Bitmap bp = EaseUserUtils.getLoacalBitmap(SamService.sam_cache_path+SamService.FG_PIC_FOLDER+"/"+page_list.get(position));
		if(bp!=null){
			iv.setImageBitmap(bp);
		}
		
		((ViewPager) view).addView(viewLists.get(position), 0); 
		return viewLists.get(position); 
	} 
}