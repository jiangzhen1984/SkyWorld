package com.netease.nim.demo.main.model;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.fragment.ChatRoomListFragment;
import com.netease.nim.demo.main.fragment.ContactListFragment;
import com.netease.nim.demo.main.fragment.MainTabFragment;
import com.netease.nim.demo.main.fragment.SamChatFragment;
import com.netease.nim.demo.main.fragment.SamProsFragment;
import com.netease.nim.demo.main.fragment.SamSearchFragment;
import com.netease.nim.demo.main.fragment.SessionListFragment;
import com.netease.nim.demo.main.reminder.ReminderId;

public enum MainTab {
    /*SAMC_BEGIN()*/
    SAM_SEARCH(0, ReminderId.SEARCH, SamSearchFragment.class, R.string.sam_search, R.layout.search_list),
    SAM_CHAT(1, ReminderId.SESSION, SamChatFragment.class, R.string.main_tab_session, R.layout.chat_list),
    SAM_PROS(2, ReminderId.PROS, SamProsFragment.class, R.string.sam_pro, R.layout.pros_list),
    CONTACT(3, ReminderId.CONTACT, ContactListFragment.class, R.string.main_tab_contact, R.layout.contacts_list);
    //CHAT_ROOM(4, ReminderId.INVALID, ChatRoomListFragment.class, R.string.chat_room, R.layout.chat_room_tab),
    /*SAMC_END()*/
	
    public final int tabIndex;

    public final int reminderId;

    public final Class<? extends MainTabFragment> clazz;

    public final int resId;

    public final int fragmentId;

    public final int layoutId;

    MainTab(int index, int reminderId, Class<? extends MainTabFragment> clazz, int resId, int layoutId) {
        this.tabIndex = index;
        this.reminderId = reminderId;
        this.clazz = clazz;
        this.resId = resId;
        this.fragmentId = index;
        this.layoutId = layoutId;
    }

    public static final MainTab fromReminderId(int reminderId) {
        for (MainTab value : MainTab.values()) {
            if (value.reminderId == reminderId) {
                return value;
            }
        }

        return null;
    }

    public static final MainTab fromTabIndex(int tabIndex) {
        for (MainTab value : MainTab.values()) {
            if (value.tabIndex == tabIndex) {
                return value;
            }
        }

        return null;
    }
}
