package com.netease.nim.demo.main.fragment;

import com.netease.nim.demo.R;
import com.netease.nim.demo.chatroom.fragment.ChatRoomsFragment;
import com.netease.nim.demo.main.model.MainTab;

/**
 * ��������TABҳ
 * Created by huangjun on 2015/12/11.
 */
public class ChatRoomListFragment extends MainTabFragment {
    private ChatRoomsFragment fragment;
    public ChatRoomListFragment() {
    	/*SAMC_BEGIN()*/
        setContainerId(MainTab.SAM_PROS.fragmentId);
        /*SAMC_END()*/
    }

    @Override
    protected void onInit() {
        // ���þ�̬���ɣ����ﲻ��Ҫ��ʲô��
        fragment = (ChatRoomsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.chat_rooms_fragment);
    }

    @Override
    public void onCurrent() {
        super.onCurrent();
        if (fragment != null) {
            fragment.onCurrent();
        }
    }
}
