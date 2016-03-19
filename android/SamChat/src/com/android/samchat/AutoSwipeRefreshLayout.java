package com.android.samchat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.android.samservice.SamLog;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class AutoSwipeRefreshLayout extends SwipeRefreshLayout implements OnScrollListener{
     /**
     * ������������ʱ����������
     */

    private int mTouchSlop;
    /**
     * listviewʵ��
     */
    private ListView mListView;

    /**
     * ����������, ������ײ����������ز���
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView�ļ�����footer
     */
    private View mListViewFooter;

    /**
     * ����ʱ��y����
     */
    private int mYDown;
    /**
     * ̧��ʱ��y����, ��mYDownһ�����ڻ������ײ�ʱ�ж���������������
     */
    private int mLastY;
    /**
     * �Ƿ��ڼ����� ( �������ظ��� )
     */
    private boolean isLoading = false;

	private boolean pullup_load_disable=false;

	private AbsListView.OnScrollListener mSubScrollListener;

	void disable_pullup_load(boolean disable){
		pullup_load_disable = disable;
	}
	
	public AutoSwipeRefreshLayout(Context context) {
		this(context, null);
	}
 
	public AutoSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,false);
    }
 
    /**
     * �Զ�ˢ��
     */
    public void autoRefresh() {
        try {
            Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");
            mCircleView.setAccessible(true);
            View progress = (View) mCircleView.get(this);
            progress.setVisibility(VISIBLE);
 
            Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
            setRefreshing.setAccessible(true);
            setRefreshing.invoke(this, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // ��ʼ��ListView����
        if (mListView == null) {
            getListView();
        }
    }

    /**
     * ��ȡListView����
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                // ���ù�����������ListView, ʹ�ù����������Ҳ�����Զ�����
                mListView.setOnScrollListener(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // ����
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // �ƶ�
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // ̧��
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * �Ƿ���Լ��ظ���, �����ǵ�����ײ�, listview���ڼ�����, ��Ϊ��������.
     * 
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp() && !pullup_load_disable;
    }

    /**
     * �ж��Ƿ�����ײ�
     */
    private boolean isBottom() {

        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * �Ƿ�����������
     * 
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * ���������ײ�,��������������.��ôִ��onLoad����
     */
    private void loadData() {
        if (mOnLoadListener != null) {
            // ����״̬
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
		if(!pullup_load_disable){
			mListView.addFooterView(mListViewFooter);
		}
        } else {
            mListView.removeFooterView(mListViewFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
		mSubScrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        // ����ʱ������ײ�Ҳ���Լ��ظ���
        if (canLoad()) {
            loadData();
        }

	  mSubScrollListener.onScroll(view, firstVisibleItem,visibleItemCount,totalItemCount);  
    }

    public void setOnScrollSubListener(AbsListView.OnScrollListener subScrollListener) {
        mSubScrollListener = subScrollListener;
    }

    /**
     * ���ظ���ļ�����
     * 
     * @author mrsimple
     */
    public static interface OnLoadListener {
        public void onLoad();
    }


	
}

