/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.easeui.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import android.util.Log;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.easeui.R;
import com.easemob.easeui.domain.EaseUser;
import com.easemob.easeui.utils.EaseCommonUtils;
import com.easemob.easeui.widget.EaseContactList;
import com.easemob.exceptions.EaseMobException;

/**
 * ��ϵ���б�ҳ
 * 
 */
public class EaseContactListFragment extends EaseBaseFragment {
    private static final String TAG = "EaseContactListFragment";
    protected List<EaseUser> contactList;
    protected ListView listView;
    protected boolean hidden;
    protected List<String> blackList;
    protected ImageButton clearSearch;
    protected EditText query;
    protected Handler handler = new Handler();
    protected EaseUser toBeProcessUser;
    protected String toBeProcessUsername;
    protected EaseContactList contactListLayout;
    protected boolean isConflict;
    protected FrameLayout contentContainer;
    
    private Map<String, EaseUser> contactsMap;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_contact_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //��ֹ��T��û��ȷ����ťȻ����home���������ں�̨�ֽ�app���µ�crash
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        contentContainer = (FrameLayout) getView().findViewById(R.id.content_container);
        
        contactListLayout = (EaseContactList) getView().findViewById(R.id.contact_list);        
        listView = contactListLayout.getListView();
        
        //������
        query = (EditText) getView().findViewById(R.id.query);
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
    }

    @Override
    protected void setUpView() {
        EMChatManager.getInstance().addConnectionListener(connectionListener);
        
        //�������б�
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<EaseUser>();
        // ��ȡ����contactlist
        getContactList();
        //init list
        contactListLayout.init(contactList);
        
        if(listItemClickListener != null){
            listView.setOnItemClickListener(new OnItemClickListener() {
    
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EaseUser user = (EaseUser)listView.getItemAtPosition(position);
                    listItemClickListener.onListItemClicked(user);
//                    itemClickLaunchIntent.putExtra(EaseConstant.USER_ID, username);
                }
            });
        }
        
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactListLayout.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                    
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
        
        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ���������
                hideSoftKeyboard();
                return false;
            }
        });
        
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
    	Log.e(TAG,"EaseContactList Fragment onResume");
        super.onResume();
        //if (!hidden) {
            refresh();
        //}
    }


    /**
     * ��user���뵽������
     */
    protected void moveToBlacklist(final String username){
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //���뵽������
                    EMContactManager.getInstance().addUserToBlackList(username,false);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2, 0).show();
                            refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st3, 0).show();
                        }
                    });
                }
            }
        }).start();
        
    }
    
    // ˢ��ui
    public void refresh() {
        getContactList();
        contactListLayout.refresh();
    }
    

    @Override
    public void onDestroy() {
        
        EMChatManager.getInstance().removeConnectionListener(connectionListener);
        
        super.onDestroy();
    }
    

    /**
     * ��ȡ��ϵ���б������˵�������������
     */
    protected void getContactList() {
        contactList.clear();
        synchronized (contactList) {
            //��ȡ��ϵ���б�
            if(contactsMap == null){
                return;
            }
            Iterator<Entry<String, EaseUser>> iterator = contactsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, EaseUser> entry = iterator.next();
                //������ǰ��ͨѶ¼������е�������ʾ�����ϴ��жϣ�������¼��ɵĿ���ȥ�����ж�
                if (!entry.getKey().equals("item_new_friends")
                        && !entry.getKey().equals("item_groups")
                        && !entry.getKey().equals("item_chatroom")
                        && !entry.getKey().equals("item_robots")){
                    if(!blackList.contains(entry.getKey())){
                        //����ʾ�������е��û�
                        EaseUser user = entry.getValue();
                        EaseCommonUtils.setUserInitialLetter(user);
                        contactList.add(user);
                    }
                }
            }
            // ����
            Collections.sort(contactList, new Comparator<EaseUser>() {

                @Override
                public int compare(EaseUser lhs, EaseUser rhs) {
                    if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                        return lhs.getNick().compareTo(rhs.getNick());
                    }else{
                        if("#".equals(lhs.getInitialLetter())){
                            return 1;
                        }else if("#".equals(rhs.getInitialLetter())){
                            return -1;
                        }
                        return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                    }
                    
                }
            });
        }

    }
    
    
    
    protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
                isConflict = true;
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        onConnectionDisconnected();
                    }

                });
            }
        }
        
        @Override
        public void onConnected() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    onConnectionConnected();
                }

            });
        }
    };
    private EaseContactListItemClickListener listItemClickListener;
    
    
    protected void onConnectionDisconnected() {
        
    }
    
    protected void onConnectionConnected() {
        
    }
    
    /**
     * ������Ҫ��ʾ������map��keyΪ�����û�id
     * @param contactsMap
     */
    public void setContactsMap(Map<String, EaseUser> contactsMap){
        this.contactsMap = contactsMap;
    }
    
    public interface EaseContactListItemClickListener {
        /**
         * ��ϵ��listview item����¼�
         * @param user �����item����Ӧ��user����
         */
        void onListItemClicked(EaseUser user);
    }
    
    /**
     * ����listview item����¼�
     * @param listItemClickListener
     */
    public void setContactListItemClickListener(EaseContactListItemClickListener listItemClickListener){
        this.listItemClickListener = listItemClickListener;
    }
    
}
