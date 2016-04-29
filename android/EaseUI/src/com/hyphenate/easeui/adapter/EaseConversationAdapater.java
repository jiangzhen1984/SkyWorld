package com.hyphenate.easeui.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.DateUtils;

/**
 * �Ự�б�adapter
 *
 */
public class EaseConversationAdapater extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;
    
    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;

     /*SAMC_BEGIN(which fragment this conversation list in)*/
    private int conversation_list_in=0;

    public void setConversationListIn(int fragment_id){
        conversation_list_in = fragment_id;
    }
    /*SAMC_END(which fragment this conversation list in)*/

    /*SAMC_BEGIN(caculate unread msg for specific fragment)*/
    private int caculateUnReadMsgCount(EMConversation conversation){
        int pagesize=20;
	 int total_count=conversation.getAllMsgCount();
	 int unReadCount=0;
	 boolean exited=false;

        if(total_count == 0) return 0;

        List<EMMessage> msgs = null;
	 while(!exited){
	      if(msgs == null){
                 msgs = conversation.getAllMessages();
	      }
		  
	      for(int i=msgs.size()-1;i>=0;i--){
		    if(msgs.get(i).direct() != EMMessage.Direct.RECEIVE){
                      continue;
		    }
			
                  if(!msgs.get(i).isUnread()){
			  return unReadCount;
		    }else{
                       String attr=msgs.get(i).getStringAttribute(EaseConstant.CHAT_ACTIVITY_TYPE, EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_CHAT);
		         if((attr.equals(EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_SERVICE) && conversation_list_in == EaseConstant.CONVERSATION_LIST_IN_VENDOR)
                           ||(attr.equals(EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_CHAT) && conversation_list_in == EaseConstant.CONVERSATION_LIST_IN_CHAT)
			      ||(attr.equals(EaseConstant.CHAT_ACTIVITY_TYPE_VIEW_VENDOR) && conversation_list_in == EaseConstant.CONVERSATION_LIST_IN_SERIVCE)){

				  unReadCount++;
			  }
		    }
	      }

	      if(conversation.getAllMessages().size()>=total_count){
		  	exited=true;
	      }else{
			msgs = conversation.loadMoreMsgFromDB(msgs.get(0).getMsgId(), pagesize);
	      }

	}
	 
	 return unReadCount;


    }
	 
        
    

    /*SAMC_END(caculate unread msg for specific fragment)*/

    public EaseConversationAdapater(Context context, int resource,
            List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
            convertView.setTag(holder);
        }
        holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);

        // ��ȡ����û�/Ⱥ��ĻỰ
        EMConversation conversation = getItem(position);
        // ��ȡ�û�username����Ⱥ��groupid
        String username = conversation.getUserName();
        
        if (conversation.getType() == EMConversationType.GroupChat) {
            // Ⱥ����Ϣ����ʾȺ��ͷ��
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
            holder.name.setText(group != null ? group.getGroupName() : username);
        } else if(conversation.getType() == EMConversationType.ChatRoom){
            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
        }else {
            EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);
            EaseUserUtils.setUserNick(username, holder.name);
        }

        if (conversation.getUnreadMsgCount() > 0) {
            // ��ʾ����û�����Ϣδ����
            /*SAMC_BEGIN(caculate unread msg for specific fragment)*/
	     if(conversation.getType() == EMConversationType.Chat){
		 	int UnReadMsgCount = caculateUnReadMsgCount(conversation);
			if(UnReadMsgCount>0){
                         holder.unreadLabel.setText(String.valueOf(UnReadMsgCount));
                         holder.unreadLabel.setVisibility(View.VISIBLE);
			}else{
                         holder.unreadLabel.setVisibility(View.INVISIBLE);
			}

	     }else{
                holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
                holder.unreadLabel.setVisibility(View.VISIBLE);
	     }
	     /*SAMC_END(caculate unread msg for specific fragment)*/
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMsgCount() != 0) {
            // �����һ����Ϣ��������Ϊitem��message����
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                    BufferType.SPANNABLE);

            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }
        
        //�����Զ�������
        holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if(primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if(timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }
    
    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }
    

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }





    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();
                    
                    EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
                    if(group != null){
                        username = group.getGroupName();
                    }else{
                        EaseUser user = EaseUserUtils.getUserInfo(username);
                        // TODO: not support Nick anymore
//                        if(user != null && user.getNick() != null)
//                            username = user.getNick();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else{
                          final String[] words = username.split(" ");
                            final int wordCount = words.length;

                            // Start at index 0, in case valueText starts with space(s)
                            for (int k = 0; k < wordCount; k++) {
                                if (words[k].startsWith(prefixString)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EMConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    }
    
    
    private static class ViewHolder {
        /** ��˭�������¼ */
        TextView name;
        /** ��Ϣδ���� */
        TextView unreadLabel;
        /** ���һ����Ϣ������ */
        TextView message;
        /** ���һ����Ϣ��ʱ�� */
        TextView time;
        /** �û�ͷ�� */
        ImageView avatar;
        /** ���һ����Ϣ�ķ���״̬ */
        View msgState;
        /** ����list��ÿһ���ܲ��� */
        RelativeLayout list_itease_layout;

    }
}

