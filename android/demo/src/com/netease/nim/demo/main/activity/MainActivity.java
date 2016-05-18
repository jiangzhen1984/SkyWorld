package com.netease.nim.demo.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.samchat.activity.MineActivity;
import com.android.samchat.activity.SignInActivity;
import com.android.samchat.activity.VendorSettingActivity;
import com.android.samservice.SamService;
import com.android.samservice.info.RespQuest;
import com.netease.nim.demo.chatroom.helper.ChatRoomHelper;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimConstants;
import com.netease.nim.uikit.NimMobHelper;
import com.netease.nim.demo.R;
import com.netease.nim.demo.avchat.AVChatProfile;
import com.netease.nim.demo.avchat.activity.AVChatActivity;
import com.netease.nim.demo.config.preference.Preferences;
import com.netease.nim.demo.contact.activity.AddFriendActivity;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.main.fragment.HomeFragment;
import com.netease.nim.demo.login.LogoutHelper;
import com.netease.nim.demo.session.SessionHelper;
import com.netease.nim.demo.team.TeamCreateHelper;
import com.netease.nim.demo.team.activity.AdvancedTeamSearchActivity;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 主界面
 * <p/>
 * Created by huangjun on 2015/3/25.
 */
public class MainActivity extends TActionBarActivity {

    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int REQUEST_CODE_NORMAL = 1;
    private static final int REQUEST_CODE_ADVANCED = 2;
    private static final String TAG = MainActivity.class.getSimpleName();

    private HomeFragment mainFragment;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    // 注销
    public static void logout(Context context, boolean quit) {
        Intent extra = new Intent();
        extra.putExtra(EXTRA_APP_QUIT, quit);
        start(context, extra);
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        setTitle(R.string.app_name);
        onParseIntent();

        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
            }
        });

        Log.i(TAG, "sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        }

        onInit();
    }

    private void onInit() {
        // 加载主页面
        showMainFragment();

        // 聊天室初始化
        ChatRoomHelper.init();

        /*SAMC_BEGIN()*/
        registerObservers(true);
        /*SAMC_END()*/
    }

    /*SAMC_BEGIN()*/
    @Override
    protected void onDestroy() {
        registerObservers(false);

        super.onDestroy();
    }
    /*SAMC_END()*/

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        onParseIntent();
    }

    @Override
    public void onBackPressed() {
        if (mainFragment != null) {
            if (mainFragment.onBackPressed()) {
                return;
            } else {
                moveTaskToBack(true);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.create_normal_team:
                ContactSelectActivity.Option option = TeamHelper.getCreateContactSelectOption(null, 50);
                NimUIKit.startContactSelect(MainActivity.this, option, REQUEST_CODE_NORMAL);
                break;
            case R.id.create_regular_team:
                ContactSelectActivity.Option advancedOption = TeamHelper.getCreateContactSelectOption(null, 50);
                NimUIKit.startContactSelect(MainActivity.this, advancedOption, REQUEST_CODE_ADVANCED);
                break;
            case R.id.search_advanced_team:
                AdvancedTeamSearchActivity.start(MainActivity.this);
                break;
            case R.id.add_buddy:
                AddFriendActivity.start(MainActivity.this);
                break;
            case R.id.search_btn:
                GlobalSearchActivity.start(MainActivity.this);
                break;
            case R.id.pro_setting:
                launchVendorSettingActivity();
                break;
            case R.id.my_space:
                launchMySpaceActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onParseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
            IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            switch (message.getSessionType()) {
                case P2P:
                    SessionHelper.startP2PSession(this, message.getSessionId());
                    break;
                case Team:
                    SessionHelper.startTeamSession(this, message.getSessionId());
                    break;
                default:
                    break;
            }
        } else if (intent.hasExtra(EXTRA_APP_QUIT)) {
            onLogout();
            return;
        } else if (intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
            if (AVChatProfile.getInstance().isAVChatting()) {
                Intent localIntent = new Intent();
                localIntent.setClass(this, AVChatActivity.class);
                startActivity(localIntent);
            }
        } else if (intent.hasExtra(com.netease.nim.demo.main.model.Extras.EXTRA_JUMP_P2P)) {
            Intent data = intent.getParcelableExtra(com.netease.nim.demo.main.model.Extras.EXTRA_DATA);
            String account = data.getStringExtra(com.netease.nim.demo.main.model.Extras.EXTRA_ACCOUNT);
            if (!TextUtils.isEmpty(account)) {
                SessionHelper.startP2PSession(this, account);
            }
        }
    }

    private void showMainFragment() {
        if (mainFragment == null && !isDestroyedCompatible()) {
            mainFragment = new HomeFragment();
            switchFragmentContent(mainFragment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createNormalTeam(MainActivity.this, selected, false, null);
                } else {
                    Toast.makeText(MainActivity.this, "请选择至少一个联系人！", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_CODE_ADVANCED) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                TeamCreateHelper.createAdvancedTeam(MainActivity.this, selected);
            }
        }

    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听&清除状态
        LogoutHelper.logout();

        /*SAMC_BEGIN()*/
        //LoginActivity.start(getActivity(), true);
        SamService.getInstance().getDao().clear_LoginUser_db();
        Preferences.saveUserToken("");
        Preferences.saveUserAccount("");
        SamService.getInstance().stopSamService();
        launchSignInActivity();
        /*SAMC_END()*/
        finish();
    }

    /*SAMC_BEGIN()*/
    private void launchSignInActivity()
    {
         Intent newIntent = new Intent(this,SignInActivity.class);
         int intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
         newIntent.setFlags(intentFlags);
         startActivity(newIntent);
    }
    /*SAMC_END()*/

    /*SAMC_BEGIN(add immessage receiver observer to update tag for RecentContact)*/

    private void addTag(RecentContact recent, long tag) {
        tag = recent.getTag() | tag;
        recent.setTag(tag);
    }

    private void removeTag(RecentContact recent, long tag) {
        tag = recent.getTag() & ~tag;
        recent.setTag(tag);
    }

    private boolean isTagSet(RecentContact recent, long tag) {
        return (recent.getTag() & tag) == tag;
    }
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver,register);
    }

    private void parseRespQuestExt(Map<String, Object> content,String sender){
        String link_questions = (String)content.get(NimConstants.MSG_REMOTE_RESP_QUEST_KEY);
        LogUtil.i("test","link_quest:"+link_questions);
        String[] quest_ids=null; 
        if(link_questions!=null){
            quest_ids = link_questions.split(" ");
        }

        if(quest_ids!=null && quest_ids.length>0){
             /*add into db(resp_quest_tab): id|receiver_name|sender_name|question_id*/
             String receiver = SamService.getInstance().get_current_user().getusername();
             for(String id:quest_ids){
                 if(SamService.getInstance().getDao().query_RespQuest_db(receiver, sender,id)==null){
                    SamService.getInstance().getDao().add_RespQuest_db(new RespQuest(receiver,sender,id));
                 }
             }
        }
    }

    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            int msg_from_which_window = 0;
            for(IMMessage msg : messages){
                if(msg.getSessionType() == SessionTypeEnum.P2P && msg.getDirect() == MsgDirectionEnum.In){
			 Map<String, Object> content = msg.getRemoteExtension();
			 if(content == null){
                        msg_from_which_window |= NimConstants.MSG_FROM_CHAT;
			    continue;
			 }
			 int key_value = (Integer)content.get(NimConstants.MSG_REMOTE_KEY);
                     if((key_value & NimConstants.MSG_FROM_SEARCH)!=0){
                         msg_from_which_window |= NimConstants.MSG_FROM_SEARCH; 
			 }
					 
                     if((key_value & NimConstants.MSG_FROM_CHAT)!=0){
                         msg_from_which_window |= NimConstants.MSG_FROM_CHAT; 
			 }

                     if((key_value & NimConstants.MSG_FROM_PROS)!=0){
                         msg_from_which_window |= NimConstants.MSG_FROM_PROS;
                         parseRespQuestExt(content,msg.getSessionId());
			 }

                     
                }
            }

            if(msg_from_which_window == 0){
                  return;
            }

            String sessionId = messages.get(0).getSessionId();
            LogUtil.i("test", sessionId+":"+msg_from_which_window);
            
            List<RecentContact> recents = NIMClient.getService(MsgService.class).queryRecentContactsBlock();

            RecentContact find = null;
            for(RecentContact result:recents){
                if(result.getContactId().equals(sessionId) && result.getSessionType()==SessionTypeEnum.P2P){
                       find = result;
                       break;
                }
            }

            if(find!=null){
		    LogUtil.i("test", "find recent contact");
                boolean needUpdateToDB=false;
                boolean needUpdatePros=false;
                boolean needUpdateChat=false;
                boolean needUpdateSearch=false;
				
                if((msg_from_which_window & NimConstants.MSG_FROM_SEARCH)!=0
			&& !isTagSet(find,NimConstants.RECENT_TAG_SHOW_IN_PROS)){

			addTag(find,NimConstants.RECENT_TAG_SHOW_IN_PROS);
			needUpdateToDB = true;
			needUpdatePros = true;
                }

                if((msg_from_which_window & NimConstants.MSG_FROM_CHAT)!=0
			&& !isTagSet(find,NimConstants.RECENT_TAG_SHOW_IN_CHAT)){

			addTag(find,NimConstants.RECENT_TAG_SHOW_IN_CHAT);
			needUpdateToDB = true;
			needUpdateChat = true;
                }

                if((msg_from_which_window & NimConstants.MSG_FROM_PROS)!=0
			&& !isTagSet(find,NimConstants.RECENT_TAG_SHOW_IN_SEARCH)){

			addTag(find,NimConstants.RECENT_TAG_SHOW_IN_SEARCH);
                    needUpdateToDB = true;
                    needUpdateSearch = true;
                }

                if(needUpdateToDB)
                	NIMClient.getService(MsgService.class).updateRecent(find);

                if(needUpdatePros){
                    //send broadcast to sam-pros
                    NimMobHelper.getInstance().sendProsUpdateBroadcast(find);
                }

                if(needUpdateChat){
                    //send broadcast to sam-chat
                    NimMobHelper.getInstance().sendChatUpdateBroadcast(find);
                }

                if(needUpdateSearch){
                    //send broadcast to sam-search
                    NimMobHelper.getInstance().sendSearchUpdateBroadcast(find);
                }

            }
        }
    };

    /*SAMC_END(add immessage receiver observer to update tag for RecentContact)*/	

    /*SAMC_BEGIN()*/
    private void launchMySpaceActivity(){
		Intent newIntent = new Intent(this,MineActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}

    private void launchVendorSettingActivity(){
		Intent newIntent = new Intent(this,VendorSettingActivity.class);
		int intentFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP;
		newIntent.setFlags(intentFlags);
		startActivity(newIntent);
	}
    /*SAMC_END()*/
	

}
