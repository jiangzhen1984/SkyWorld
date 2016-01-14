/**
 * 
 */
package com.android.samchat;

import com.android.samchat.R;
import com.android.samservice.*;
import com.android.samservice.info.ReceivedAnswer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;


public class SamService_Fragment extends Fragment {

	static final String TAG = "SamService_Fragment";

	public static final int MSG_SEND_QUESTION_CALLBACK = 1;
	public static final int MSG_ANSWER_BACK = 2;	
	public static final int MSG_AUTOLOGIN_CALLBACK=3;
	public static final int MSG_CANCEL_QUESTION_CALLBACK = 4;

	
	public static final String CANCEL_QUESTION_CONFIRM = "com.android.samchat.cancel_question";
	
	private ListView mTopSearchList;
	private Context mContext;
	private SearchListAdapter mAdpater;
	private View rootView;
	private EditText mSearch;
	private ImageView mCancel;
	private TextView mSearchtitle;
	private TextView mQuestionAction;

	private String current_question;
	private SamServiceType current_type;

	private SamProcessDialog mDialog;
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView == null){
			mDialog = new SamProcessDialog();
			SamLog.e(TAG, "onCreateView");
			rootView=inflater.inflate(R.layout.fragment_samservice, container,false);
			mTopSearchList = (ListView)rootView.findViewById(R.id.top_search_list);
			mSearchtitle = (TextView)rootView.findViewById(R.id.search_title);
			mContext = getActivity().getBaseContext();
			mAdpater = new SearchListAdapter(mContext);
			mTopSearchList.setAdapter(mAdpater);
			mSearch = (EditText) rootView.findViewById(R.id.samservice_search_input);
			mCancel = (ImageView) rootView.findViewById(R.id.samservice_search_cancel);
			mQuestionAction = (TextView) rootView.findViewById(R.id.question_action);

			mSearchtitle.setText(getString(R.string.samservice_top_serach_txt));
			
			if(current_type == SamServiceType.TOP_SEARCH){
				mQuestionAction.setText(R.string.question_publish);
			}else{
				mQuestionAction.setText(R.string.question_cancel);
			}
			
			mQuestionAction.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			if(current_type == SamServiceType.TOP_SEARCH){
						String question = mSearch.getText().toString().trim();
						if(!question.equals("")){
							/*send questions to server*/
							current_question = question;
							if(mDialog!=null){
    								mDialog.launchProcessDialog(getActivity(),getString(R.string.question_publish_now));
    							}
							publish_question(question);
						}
						return ;
					}else{
						launchDialogActivityNeedConfirm(getString(R.string.reminder),getString(R.string.cancel_reminder));
						
					}
		    		}
		    	
		    });

			
			mCancel.setOnClickListener(new OnClickListener(){
		    		@Override
		    		public void onClick(View arg0) {
		    			mSearch.setText("");
		    		}
		    	
			});

			mTopSearchList.setOnItemClickListener(new OnItemClickListener(){   
			@Override   
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {   
				/*arg2 is postion*/
				if(mAdpater.getListType() == SearchListAdapter.LIST_TYPE_ANSWER){
				//click answer to view:
					ReceivedAnswer answer = mAdpater.getAnswerInfo(arg2);
					mAdpater.setRead(arg2);
					Intent intent = new Intent();
					intent.setClass(getActivity(),SamAnswerDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("ReceivedAnswer",answer);
					intent.putExtras(bundle);
					startActivityForResult(intent,2);
									
				}else{
				//click top search to view:

				}
				
			}   
               
       	 }); 

			
			
		}
		
		return rootView;
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SamLog.i(TAG, "onCreated");
		mTopSearchList = null;
		current_type = SamServiceType.TOP_SEARCH; 

	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		SamLog.i(TAG, "onAttach");
		
		

	}
	
	@Override
	public void onStart(){
		super.onStart();
		SamLog.i(TAG, "onStart");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		SamLog.i(TAG, "onResume");
		
	}
	
	
	@Override
	public void onDetach(){
		super.onDetach();
		SamLog.i(TAG, "onDetach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		SamLog.i(TAG, "onActivityCreated");

	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if(rootView != null){
			SamLog.i(TAG, "onDestroyView");
			((ViewGroup)(rootView.getParent())).removeView(rootView);
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
		SamLog.i(TAG, "onPause");
		if(mSearch!=null) 
			mSearch.clearFocus();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		SamLog.i(TAG, "onStop");
	}


	private void cancel_question(){
		/*send cancel to server*/
		SamService.getInstance().cancel_question( mHandler, MSG_CANCEL_QUESTION_CALLBACK);

	}
	
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    		}
	}

	private void publish_question(String question){
		closeInputMethod(); 
	
		/*send question to server*/
		SamService.getInstance().send_question(question, mHandler, MSG_SEND_QUESTION_CALLBACK);
	}

	private void update_answers(ReceivedAnswer answer){
		mAdpater.addAnswerInfo(answer);
		mAdpater.setCount(mAdpater.getCountOfAnswerInfo());
		mAdpater.setListType_answer();
		mAdpater.notifyDataSetChanged();
	}

	private void back_to_topsearch(){
		mSearchtitle.setText(getString(R.string.samservice_top_serach_txt));
		mAdpater.clearAnswerInfo();
		mAdpater.setCount(20);		
		mAdpater.setListType_topSearch();
		mAdpater.notifyDataSetChanged();
	}
	
	public Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	if(getActivity() == null){
	    		SamLog.e(TAG,"MainActivity is killed, drop msg...");
	    		return;
	    	}
	    	
	    	switch(msg.what){
	    	case MSG_SEND_QUESTION_CALLBACK:
	    		isTopActivity();
	    		isTopFragment();
	    		if(msg.arg1 == SamService.R_SEND_QUESTION_OK){
	    			SamLog.i(TAG,"question_id = " + ((SamCoreObj)msg.obj).refCBObj.qinfo.question_id);
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				mQuestionAction.setText(R.string.question_cancel);
				current_type = SamServiceType.ANSWER;
				mSearch.setInputType(InputType.TYPE_NULL);
				mSearchtitle.setText(R.string.samservice_answer);
				mAdpater.setCount(0);
				mAdpater.notifyDataSetChanged();
				/*store samobj*/
	    		}else if(msg.arg1 == SamService.R_SEND_QUESTION_ERROR){
	    			SamLog.i(TAG,"question send error ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}

				launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_error_statement));
	    		}else if(msg.arg1 == SamService.R_SEND_QUESTION_FAILED){
	    			SamLog.i(TAG,"question send failed ...");
				/*send question failed due to server error*/
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity(getString(R.string.question_publish_failed),getString(R.string.question_action_server_error_statement));				
	    		}else{
	    			SamLog.i(TAG,"impossible here ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity("Fatal Error","Close samchat!");
	    		}
	    		break;
	    	case MSG_ANSWER_BACK:
			ReceivedAnswer ra = (ReceivedAnswer)msg.obj;
			update_answers(ra);
			break;

		case MSG_CANCEL_QUESTION_CALLBACK:
		  	if(msg.arg1 == SamService.R_CANCEL_QUESTION_OK){
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				mQuestionAction.setText(R.string.question_publish);
				current_type = SamServiceType.TOP_SEARCH;
				mSearch.setInputType(InputType.TYPE_CLASS_TEXT);
				mSearch.setText("");
				back_to_topsearch();
			}else if(msg.arg1 == SamService.R_CANCEL_QUESTION_FAILED){
				SamLog.i(TAG,"cancel question failed ...");
				/*cancel question failed due to server error*/
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity(getString(R.string.question_cancel_failed),getString(R.string.question_action_server_error_statement));	
			}else if(msg.arg1 == SamService.R_CANCEL_QUESTION_ERROR){
				SamLog.i(TAG,"question send error ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity(getString(R.string.question_cancel_failed),getString(R.string.question_action_error_statement));;
			}else{
				SamLog.i(TAG,"impossible here ...");
				if(mDialog!=null){
	    				mDialog.dismissPrgoressDiglog();
	    			}
				launchDialogActivity("Fatal Error","Close samchat!");
			}
	      }
	    }
	};


	private void launchDialogActivity(String title,String msg){
		Intent newIntent = new Intent(getActivity(),DialogActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		startActivity(newIntent);
	}

	private void launchDialogActivityNeedConfirm(String title,String msg){
		Intent newIntent = new Intent(CANCEL_QUESTION_CONFIRM);		
		//int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK;// | Intent.FLAG_ACTIVITY_CLEAR_TOP;
		//newIntent.setFlags(intentFlags);
		newIntent.putExtra("title", title);
		newIntent.putExtra("message", msg);
		this.startActivityForResult(newIntent, 1);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(requestCode == 1){
			//SamLog.e(TAG,"result code:"+ resultCode);
			if(resultCode == 1){ //OK
				SamLog.e(TAG,"onActivityResult = 0");
				cancel_question_confirm_ok();
			}else{
				SamLog.e(TAG,"onActivityResult != 0");
				cancel_question_confirm_not_ok();
			}
    	   
		}else if(requestCode == 2){
			SamLog.e(TAG,"onActivityResult,requestCode:2");
			mAdpater.notifyDataSetChanged();
		}
	} 
	
	private void cancel_question_confirm_ok(){
		if(mDialog!=null){
    			mDialog.launchProcessDialog(getActivity(),getString(R.string.question_cancel_now));
    		}
		cancel_question();
		
		return;
	}

	private void cancel_question_confirm_not_ok(){
		return;
	}


	private boolean isTopActivity()  
	{  
		boolean isTop = false;  
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);  
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
		SamLog.e(TAG, "isTopActivity = " + cn.getClassName());  
		if (cn.getClassName().contains(MainActivity.ACTIVITY_NAME))  
		{  
			 isTop = true;  
		}  
		SamLog.e(TAG, "isTopActivity = " + isTop);  
		return isTop;  
    }
	
	private boolean isTopFragment(){
		boolean isTop = false;
		if(MainActivity.getCurrentTab() == 0){
			isTop = true;
		}
		SamLog.e(TAG, "isTopFragment = " + isTop);  
		return isTop;  
		
	}

}
