package com.android.samchat;

 
import java.util.ArrayList; 
import java.util.List; 

import com.android.samservice.SamLog;

import android.app.Activity; 
import android.content.Intent;
import android.graphics.Bitmap; 
import android.graphics.BitmapFactory; 
import android.graphics.Matrix; 
import android.os.Bundle; 
import android.support.v4.view.ViewPager; 
import android.util.DisplayMetrics; 
import android.view.Menu; 
import android.view.View; 
import android.view.animation.Animation; 
import android.view.animation.TranslateAnimation; 
import android.widget.ImageView; 
import android.widget.TextView; 
   
public class ViewPagerScrollActivity extends Activity {
	public static String TAG="ViewPagerScrollActivity";
	public static String EXTRA_PAGE_INDICATION="page_indication";
	public static String EXTRA_PAGE_LIST="page_list";
	
	private int indication;
	private ArrayList<String> pagelist;
	private ViewPager viewPager; 
	private ImageView imageView; 
	private List<View> lists = new ArrayList<View>(); 
	private ViewPagerAdapter adapter; 
	private Bitmap cursor; 
	private int offSet; 
	private int currentItem; 
	private Matrix matrix = new Matrix(); 
	private int bmWidth; 
	private Animation animation; 
	private TextView textView1; 
	private TextView textView2; 
	private TextView textView3; 
   
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_viewpager); 

		initFromIntent(getIntent());

		for(int i=0;i<pagelist.size();i++){
			lists.add(getLayoutInflater().inflate(R.layout.item_page, null)); 
		}
		
		//initeCursor(); 
   
		adapter = new ViewPagerAdapter(lists,pagelist); 
		viewPager = (ViewPager) findViewById(R.id.viewPager); 
		viewPager.setAdapter(adapter); 
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { 
   			@Override 
			public void onPageSelected(int arg0) { 
   				SamLog.e(TAG,"viewPager select page:"+arg0);
			} 
			
			@Override 
			public void onPageScrolled(int arg0, float arg1, int arg2) { 
   
			} 

			@Override 
			public void onPageScrollStateChanged(int arg0) { 
   
			} 
		}); 

		SamLog.e(TAG,"viewPager setCurrentItem:"+indication);
		viewPager.setCurrentItem(indication); 
	} 
   
/*    private void initeCursor() { 
        cursor = BitmapFactory.decodeResource(getResources(), R.drawable.cursor); 
        bmWidth = cursor.getWidth(); 
   
        DisplayMetrics dm; 
        dm = getResources().getDisplayMetrics(); 
   
        offSet = (dm.widthPixels - 3 * bmWidth) / 6; 
        matrix.setTranslate(offSet, 0); 
        imageView.setImageMatrix(matrix); 
        currentItem = 0; 
    } */
   
	private void initFromIntent(Intent intent) {
	         if (intent != null) {
	             indication = intent.getIntExtra(EXTRA_PAGE_INDICATION,0);
	             pagelist = intent.getStringArrayListExtra(EXTRA_PAGE_LIST);
	         } else {
	             finish();
	         }
     }
}


