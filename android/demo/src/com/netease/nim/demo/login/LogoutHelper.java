package com.netease.nim.demo.login;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.chatroom.helper.ChatRoomHelper;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;

/**
 * ע��������
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // ������&ע������&���״̬
        NimUIKit.clearCache();
        ChatRoomHelper.logout();
        DemoCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
    }
}
