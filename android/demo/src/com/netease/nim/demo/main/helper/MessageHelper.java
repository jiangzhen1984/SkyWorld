package com.netease.nim.demo.main.helper;

import android.text.TextUtils;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by huangjun on 2015/4/9.
 */
public class MessageHelper {
    public static String getName(String account, SessionTypeEnum sessionType) {
        if (sessionType == SessionTypeEnum.P2P) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        } else if (sessionType == SessionTypeEnum.Team) {
            return TeamDataCache.getInstance().getTeamName(account);
        }
        return account;
    }

    public static String getVerifyNotificationText(SystemMessage message) {
        StringBuilder sb = new StringBuilder();
        String fromAccount = NimUserInfoCache.getInstance().getUserDisplayNameYou(message.getFromAccount());
        Team team = TeamDataCache.getInstance().getTeamById(message.getTargetId());
        if (team == null && message.getAttachObject() instanceof Team) {
            team = (Team) message.getAttachObject();
        }
        String teamName = team == null ? message.getTargetId() : team.getName();

        if (message.getType() == SystemMessageType.TeamInvite) {
            sb.append("����").append("��").append("����Ⱥ ").append(teamName);
        } else if (message.getType() == SystemMessageType.DeclineTeamInvite) {
            sb.append(fromAccount).append("�ܾ���Ⱥ ").append(teamName).append(" ����");
        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
            sb.append("�������Ⱥ ").append(teamName);
        } else if (message.getType() == SystemMessageType.RejectTeamApply) {
            sb.append(fromAccount).append("�ܾ��������Ⱥ ").append(teamName).append("������");
        } else if (message.getType() == SystemMessageType.AddFriend) {
            AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
            if (attachData != null) {
                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                    sb.append("�������Ϊ����");
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                    sb.append("ͨ������ĺ�������");
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                    sb.append("�ܾ�����ĺ�������");
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                    sb.append("������Ӻ���" + (TextUtils.isEmpty(message.getContent()) ? "" : "��" + message.getContent()));
                }
            }
        }

        return sb.toString();
    }

    /**
     * �Ƿ���֤��Ϣ��Ҫ������Ҫ��ͬ��ܾ��Ĳ�������
     */
    public static boolean isVerifyMessageNeedDeal(SystemMessage message) {
        if (message.getType() == SystemMessageType.AddFriend) {
            if (message.getAttachObject() != null) {
                AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT ||
                        attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND ||
                        attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                    return false; // �Է�ֱ�Ӽ���Ϊ���ѣ��Է�ͨ����ĺ������󣬶Է��ܾ���ĺ�������
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                    return true; // ������֤����
                }
            }
            return false;
        } else if (message.getType() == SystemMessageType.TeamInvite || message.getType() == SystemMessageType.ApplyJoinTeam) {
            return true;
        } else {
            return false;
        }
    }

    public static String getVerifyNotificationDealResult(SystemMessage message) {
        if (message.getStatus() == SystemMessageStatus.passed) {
            return "��ͬ��";
        } else if (message.getStatus() == SystemMessageStatus.declined) {
            return "�Ѿܾ�";
        } else if (message.getStatus() == SystemMessageStatus.ignored) {
            return "�Ѻ���";
        } else if (message.getStatus() == SystemMessageStatus.expired) {
            return "�ѹ���";
        } else {
            return "δ����";
        }
    }
}
