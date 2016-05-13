package com.netease.nim.demo.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.BlackListActivity;
import com.netease.nim.demo.main.activity.SystemMessageActivity;
import com.netease.nim.demo.main.activity.TeamListActivity;
import com.netease.nim.demo.main.helper.SystemMessageUnreadManager;
import com.netease.nim.demo.main.model.MainTab;
import com.netease.nim.demo.main.reminder.ReminderId;
import com.netease.nim.demo.main.reminder.ReminderItem;
import com.netease.nim.demo.main.reminder.ReminderManager;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.uikit.NimConstants;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.contact.ContactsCustomization;
import com.netease.nim.uikit.contact.ContactsFragment;
import com.netease.nim.uikit.contact.core.item.AbsContactItem;
import com.netease.nim.uikit.contact.core.item.ItemTypes;
import com.netease.nim.uikit.contact.core.model.ContactDataAdapter;
import com.netease.nim.uikit.contact.core.viewholder.AbsContactViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * ����ͨѶ¼�б�
 * <p/>
 * Created by huangjun on 2015/9/7.
 */
public class ContactListFragment extends MainTabFragment {

    private ContactsFragment fragment;

    public ContactListFragment() {
        setContainerId(MainTab.CONTACT.fragmentId);
    }

    /**
     * ******************************** ������� ***********************************
     */
    public final static class FuncItem extends AbsContactItem {
        static final FuncItem VERIFY = new FuncItem();
        static final FuncItem NORMAL_TEAM = new FuncItem();
        static final FuncItem ADVANCED_TEAM = new FuncItem();
        static final FuncItem BLACK_LIST = new FuncItem();
        static final FuncItem MY_COMPUTER = new FuncItem();

        @Override
        public int getItemType() {
            return ItemTypes.FUNC;
        }

        @Override
        public String belongsGroup() {
            return null;
        }

        public static final class FuncViewHolder extends AbsContactViewHolder<FuncItem> {
            private ImageView image;
            private TextView funcName;
            private TextView unreadNum;

            @Override
            public View inflate(LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.func_contacts_item, null);
                this.image = (ImageView) view.findViewById(R.id.img_head);
                this.funcName = (TextView) view.findViewById(R.id.tv_func_name);
                this.unreadNum = (TextView) view.findViewById(R.id.tab_new_msg_label);
                return view;
            }

            @Override
            public void refresh(ContactDataAdapter contactAdapter, int position, FuncItem item) {
                if (item == VERIFY) {
                    funcName.setText("��֤����");
                    image.setImageResource(R.drawable.icon_verify_remind);
                    image.setScaleType(ScaleType.FIT_XY);
                    int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
                    updateUnreadNum(unreadCount);

                    ReminderManager.getInstance().registerUnreadNumChangedCallback(new ReminderManager.UnreadNumChangedCallback() {
                        @Override
                        public void onUnreadNumChanged(ReminderItem item) {
                            if (item.getId() != ReminderId.CONTACT) {
                                return;
                            }

                            updateUnreadNum(item.getUnread());
                        }
                    });
                } else if (item == NORMAL_TEAM) {
                    funcName.setText("������");
                    image.setImageResource(R.drawable.ic_secretary);
                } else if (item == ADVANCED_TEAM) {
                    funcName.setText("�߼�Ⱥ");
                    image.setImageResource(R.drawable.ic_advanced_team);
                } else if (item == BLACK_LIST) {
                    funcName.setText("������");
                    image.setImageResource(R.drawable.ic_black_list);
                } else if (item == MY_COMPUTER) {
                    funcName.setText("�ҵĵ���");
                    image.setImageResource(R.drawable.ic_my_computer);
                }

                if (item != VERIFY) {
                    image.setScaleType(ScaleType.FIT_XY);
                    unreadNum.setVisibility(View.GONE);
                }
            }

            private void updateUnreadNum(int unreadCount) {
                // 2.*�汾viewholder��������
                if (unreadCount > 0 && funcName.getText().toString().equals("��֤����")) {
                    unreadNum.setVisibility(View.VISIBLE);
                    unreadNum.setText("" + unreadCount);
                } else {
                    unreadNum.setVisibility(View.GONE);
                }
            }
        }

        static List<AbsContactItem> provide() {
            List<AbsContactItem> items = new ArrayList<AbsContactItem>();
            items.add(VERIFY);
            items.add(NORMAL_TEAM);
            items.add(ADVANCED_TEAM);
            items.add(BLACK_LIST);
            items.add(MY_COMPUTER);

            return items;
        }

        static void handle(Context context, AbsContactItem item) {
            if (item == VERIFY) {
                SystemMessageActivity.start(context);
            } else if (item == NORMAL_TEAM) {
                TeamListActivity.start(context, ItemTypes.TEAMS.NORMAL_TEAM);
            } else if (item == ADVANCED_TEAM) {
                TeamListActivity.start(context, ItemTypes.TEAMS.ADVANCED_TEAM);
            } else if (item == MY_COMPUTER) {
                /*SAMC_BEGIN()*/
                SessionHelper.startP2PSession(context, DemoCache.getAccount(),NimConstants.MSG_FROM_CHAT);
		   /*SAMC_END()*/
            } else if (item == BLACK_LIST) {
                BlackListActivity.start(context);
            }
        }
    }


    /**
     * ******************************** �������� ***********************************
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onCurrent(); // ����onInit����ǰ����
    }

    @Override
    protected void onInit() {
        addContactFragment();  // ����ͨѶ¼ҳ��
    }

    // ��ͨѶ¼�б�fragment��̬���ɽ����� ������Ҳ����ʹ����xml�����õķ�ʽ��̬���ɡ�
    private void addContactFragment() {
        fragment = new ContactsFragment();
        fragment.setContainerId(R.id.contact_fragment);

        TActionBarActivity activity = (TActionBarActivity) getActivity();

        // �����activity�Ӷ�ջ�ָ���FM���Ѿ����ڻָ�������fragment����ʱ��ʹ�ûָ����ģ���new��������ᱻ������
        fragment = (ContactsFragment) activity.addFragment(fragment);

        // �������
        fragment.setContactsCustomization(new ContactsCustomization() {
            @Override
            public Class<? extends AbsContactViewHolder<? extends AbsContactItem>> onGetFuncViewHolderClass() {
                return FuncItem.FuncViewHolder.class;
            }

            @Override
            public List<AbsContactItem> onGetFuncItems() {
                return FuncItem.provide();
            }

            @Override
            public void onFuncItemClick(AbsContactItem item) {
                FuncItem.handle(getActivity(), item);
            }
        });
    }

    @Override
    public void onCurrentTabClicked() {
        // ����л�����ǰTAB
        if (fragment != null) {
            fragment.scrollToTop();
        }
    }
}
