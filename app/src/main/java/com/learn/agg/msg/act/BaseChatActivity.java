package com.learn.agg.msg.act;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codebear.keyboard.CBEmoticonsKeyBoard;
import com.codebear.keyboard.data.AppFuncBean;
import com.codebear.keyboard.data.EmoticonsBean;
import com.codebear.keyboard.fragment.CBVoice;
import com.codebear.keyboard.widget.CBAppFuncView;
import com.codebear.keyboard.widget.CBEmoticonsView;
import com.codebear.keyboard.widget.FuncLayout;
import com.codebear.keyboard.widget.RecordIndicator;
import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.adapter.ChatAdapter;
import com.learn.agg.msg.contract.BaseChatContract;
import com.learn.agg.msg.presenter.BaseChatPresenter;
import com.learn.agg.widgets.CustomDialog;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.VoiceBody;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.TimeUtil;
import com.lib.xiangxiang.im.ImSocketClient;
import com.lib.xiangxiang.im.SocketManager;
import com.learn.commonalitylibrary.body.LocationBody;
import com.location.com.SendLocationActivity;
import com.location.com.ShowLocationActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.white.easysp.EasySP;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public abstract class BaseChatActivity extends BaseMvpActivity<BaseChatContract.IPresenter> implements BaseChatContract.IView, FuncLayout.OnFuncKeyBoardListener, View.OnClickListener, RecordIndicator.OnRecordListener, CBEmoticonsView.OnEmoticonClickListener, CBAppFuncView.OnAppFuncClickListener, View.OnTouchListener, OnRefreshListener, SocketManager.SendMsgCallBack, CBVoice.VoiceStateListener, ChatAdapter.itemClickListener {


    private static final int REQUEST_CODE_IMAGE = 1231;
    private static final int REQUEST_CODE_LOCATION = 1232;
    private RecyclerView mRecyclerView;
    private ChatAdapter chatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
    public CBEmoticonsKeyBoard mKbView;
    private int OPTION_TYPE_CARD = 1;
    private int OPTION_TYPE_IMAGE = 2;
    private int OPTION_TYPE_LOCATION = 3;
    private int pageNo = 1;
    private int pageSize = 30;
    private String conversation;
    private SmartRefreshLayout mSmart;
    private LinearLayoutManager layoutManager;
    private Boolean isLastItem = true;
    private TextView tv_new_msg;
    private int new_msg_count = 0;
    private String to_id;
    public LoginBean to_bean;

    public interface key {
        String KEY_TO = "key_to_bean";
        String KEY_ID = "key_to_id";
        String KEY_CONVERSATION = "key_conversation";
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mKbView != null) {
            mKbView.reset();
        }
        return false;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (chatAdapter.getData().size() > 0) {
            pageNo++;
        }
        getPresenter().getHistory(pageNo, pageSize, to_id);
    }

    // socket发送消息回调
    @Override
    public void call(String msg) {
        ChatMessage chatMessage = GsonUtil.GsonToBean(msg, ChatMessage.class);
        if (chatAdapter != null) {
            chatAdapter.notifyChatMessage(chatMessage);
        }
    }

    // 语音回调
    @Override
    public void onStartVoice(String pid) {
        String send_json = ImSendMessageUtils.getChatMessageVoice("", "", 0, getUid(), getTo_Id(), pid, conversation, chatAdapter.getLastItemDisplayTime());
        ChatMessage message = GsonUtil.GsonToBean(send_json, ChatMessage.class);
        chatAdapter.setData(message);
        toLastItem();
    }

    @Override
    public void onCancelVoice(String pid) {
        showToast("取消");
        chatAdapter.removeItem(pid);
        toLastItem();
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEndVoice(String filePath, String fileAbsPath, long time, String pid) {
        if (time < 1000) {
            showToast("时间过短");
            chatAdapter.removeItem(pid);
            toLastItem();
            chatAdapter.notifyDataSetChanged();
            return;
        }
        Log.i("TAG", "filePath:" + filePath + "\n" + "fileAbsPath:" + fileAbsPath + "\n" + "time:" + time);
        String send_json = ImSendMessageUtils.getChatMessageVoice(filePath, fileAbsPath, time, getUid(), getTo_Id(), pid, conversation, chatAdapter.getLastItemDisplayTime());
        ChatMessage message = GsonUtil.GsonToBean(send_json, ChatMessage.class);
        chatAdapter.setData(message);
        toLastItem();
        getPresenter().SocketSendJson(send_json, true);
    }

    @Override
    public void onClickItemImage(String pid) {
        ArrayList<ChatMessage> arrayList = new ArrayList<>();
        List<ChatMessage> data = chatAdapter.getData();
        List<LocalMedia> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < data.size(); i++) {
            int bodyType = data.get(i).getBodyType();
            if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                arrayList.add(data.get(i));
            }
        }
        for (int j = 0; j < arrayList.size(); j++) {
            LocalMedia media = new LocalMedia();
            ChatMessage chatMessage = arrayList.get(j);
            String body = arrayList.get(j).getBody();
            ImageBody imageBody = GsonUtil.GsonToBean(body, ImageBody.class);
            media.setPath(imageBody.getImage());
            list.add(media);
            if (chatMessage.getPid().equals(pid)) {
                index = j;
            }
        }
        PictureSelector.create(this).themeStyle(R.style.picture_default_style).openExternalPreview(index, list);
    }

    @Override
    public void onClickLocation(LocationBody body) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", body);
        if (EasyPermission
                .build()
                .hasPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            goActivity(ShowLocationActivity.class, bundle);
        } else {
            arvixe(2, bundle);
        }
    }

    @Override
    public void onLongClick(final ChatMessage message) {
        if (message.getBodyType() != ChatMessage.MSG_BODY_TYPE_CANCEL && message.getBodyType() != ChatMessage.MSG_BODY_TYPE_VOICE) {
            String fromId = message.getFromId();
            final List<ChatMessage> data = chatAdapter.getData();
            String uid = getUid();
            final CustomDialog dialog = new CustomDialog(this, true, R.layout.dialog_chat_long);
            View view = dialog.getView();
            final Button btn_cancel = view.findViewById(R.id.btn_cancel);
            Button btn_forwarding_item = view.findViewById(R.id.btn_forwarding_item);
            btn_cancel.setVisibility(fromId.equals(uid) ? View.VISIBLE : View.GONE);
            if (TimeUtil.getTimeExpend3(System.currentTimeMillis(), message.getTime())) {
                btn_cancel.setText("撤回");
                btn_cancel.setClickable(true);
            } else {
                btn_cancel.setText("撤回(超出3分钟)");
                btn_cancel.setClickable(false);
            }
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn_cancel.getText().toString().contains("超出")) {
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        message.setMsgStatus(ChatMessage.MSG_SEND_LOADING);
                        chatAdapter.setData(message);
                        message.setBodyType(ChatMessage.MSG_BODY_TYPE_CANCEL);
                        String json = GsonUtil.BeanToJson(message);
                        DataBaseHelp.getInstance(BaseChatActivity.this).addChatMessage(message);
                        if (message.getPid().equals(data.get(data.size() - 1).getPid())) {
                            getPresenter().SocketSendJson(json, true);
                        } else {
                            getPresenter().SocketSendJson(json, false);
                        }

                    }
                }
            });
            btn_forwarding_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("msg", message);
                    goActivity(ForWardingActivity.class, bundle);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }

    @Override
    public void onVoiceRead(ChatMessage message) {
        getPresenter().updateHistory(message.getPid());
    }

    @Override
    public void onClickPb(final ChatMessage message) {
        final CustomDialog dialog = new CustomDialog(this, false, R.layout.dialog_resend);
        View view = dialog.getView();
        if (view != null) {
            view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            view.findViewById(R.id.tv_resend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    chatAdapter.removeItem(message.getPid());
                    DataBaseHelp.getInstance(BaseChatActivity.this).deleteChatMessage(message);
                    toLastItem();
                    chatAdapter.notifyDataSetChanged();
                    message.setMsgStatus(ChatMessage.MSG_SEND_LOADING);
                    message.setDisplaytime(chatAdapter.getLastItemDisplayTime());
                    message.setTime(System.currentTimeMillis());
                    message.setPid(ImSendMessageUtils.getPid());
                    chatAdapter.setData(message);
                    toLastItem();
                    chatAdapter.notifyDataSetChanged();
                    getPresenter().SocketSendJson(GsonUtil.BeanToJson(message), true);

                }
            });
            dialog.show();
        }
    }

    @Override
    protected Boolean isRequestMission() {
        needPermissions = new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        return true;
    }


    @NotNull
    @Override
    public Class<? extends BaseChatContract.IPresenter> registerPresenter() {
        return BaseChatPresenter.class;
    }

    @Override
    public SocketManager.SendMsgCallBack callBack() {
        return this;
    }

    @Override
    protected void initView() {
        tv_new_msg = findViewById(R.id.tv_new_msg);
        socketConnectState(ImSocketClient.checkSocket());
    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        to_id = intent.getStringExtra(key.KEY_ID);
        conversation = intent.getStringExtra(key.KEY_CONVERSATION);
        to_bean = (LoginBean) intent.getSerializableExtra(key.KEY_TO);
        String json = EasySP.init(this).getString(Constant.SPKey_info(this));
        LoginBean from_bean = GsonUtil.GsonToBean(json, LoginBean.class);
        chatAdapter.setFromUserBean(from_bean);
        chatAdapter.setToUserBean(to_bean);
        showLoadingDialog();
        if (conversation == null || conversation.isEmpty()) {
            getPresenter().getConversation();
        } else {
            Logger.t("socket").i("chat conversation :" + conversation);
            DataBaseHelp.getInstance(this).setSessionNumber(conversation, 0);
            getPresenter().getHistory(pageNo, pageSize, getTo_Id());
        }
        tv_new_msg.setOnClickListener(this);
        getUser();
    }

    public abstract void getUser();

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public String getTo_Id() {
        return to_id;
    }

    @Override
    public String getConversation() {
        return conversation;
    }

    @Override
    public Context getContent() {
        return this;
    }

    @Override
    public void onSuccess(List<ChatMessage> list) {
        dismissDialog();
        mSmart.finishRefresh();
        if (list.size() > 0) {
            chatAdapter.setData(list);
            layoutManager.scrollToPositionWithOffset(list.size(), 0);
        }
        if (pageNo == 1) {
            toLastItem();
            for (int i = 0; i < list.size(); i++) {
                ChatMessage chatMessage = list.get(i);
                String json = GsonUtil.BeanToJson(chatMessage);
                if (chatMessage.getMsgStatus() == ChatMessage.MSG_SEND_LOADING) {
                    Log.i(ImSocketClient.TAG, "发送上次未成功的消息: " + json);
                    Log.i(ImSocketClient.TAG, "未发送的消息类型: " + chatMessage.getBodyType());
                    if (chatMessage.getBodyType() == ChatMessage.MSG_BODY_TYPE_VOICE) {
                        VoiceBody voiceBody = GsonUtil.GsonToBean(chatMessage.getBody(), VoiceBody.class);
                        String send_json = ImSendMessageUtils.getChatMessageVoice(voiceBody.getFileName(), voiceBody.getFileAbsPath(), voiceBody.getTime(), chatMessage.getFromId(), chatMessage.getToId(), chatMessage.getPid(), chatMessage.getConversation(), ChatMessage.MSG_TIME_TRUE);
                        getPresenter().SocketSendJson(send_json, true);
                    } else if (chatMessage.getBodyType() == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                        ImageBody body = GsonUtil.GsonToBean(chatMessage.getBody(), ImageBody.class);
                        String send_json = ImSendMessageUtils.getChatMessageImage(body.getImage(), chatMessage.getFromId(), chatMessage.getToId(), chatMessage.getPid(), chatMessage.getConversation(), 1);
                        ChatMessage message = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                        chatAdapter.setData(message);
                        toLastItem();
                        getPresenter().SocketSendJson(send_json, true);
                    } else if (chatMessage.getBodyType() == ChatMessage.MSG_BODY_TYPE_LOCATION) {
                        String send_json = ImSendMessageUtils.getChatMessageLocation(chatMessage.getBody(), chatMessage.getFromId(), chatMessage.getToId(), chatMessage.getPid(), chatMessage.getConversation(), 1);
                        ChatMessage msg = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                        chatAdapter.setData(msg);
                        toLastItem();
                        getPresenter().SocketSendJson(send_json, true);
                    } else {
                        getPresenter().SocketSendJson(json, true);
                    }

                }
            }
        }

    }

    @Override
    public void onSuccessConversation(String con) {
        conversation = con;
        getPresenter().getHistory(pageNo, pageSize, getTo_Id());
        Logger.i("chat conversation :" + con);
        DataBaseHelp.getInstance(this).setSessionNumber(con, 0);
    }

    @Override
    public void onSuccess() {
        dismissDialog();
        mSmart.finishRefresh();
        if (pageNo > 1) {
            pageNo--;
        }
    }

    @Override
    public void onError(String msg) {
        dismissDialog();
        mSmart.finishRefresh();
        showToast(msg);
    }

    @Override
    public void onFuncPop(int height) {
        if (isLastItem) {
            toLastItem();
        }
    }

    @Override
    public void onFuncClose() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String text = mKbView.getEtChat().getText().toString().trim();
                mKbView.getEtChat().setText("");
                sendText(text);
                break;
            case R.id.tv_new_msg:
                toLastItem();
                new_msg_count = 0;
                tv_new_msg.setVisibility(View.GONE);
                break;
        }
    }

    // 语音
    @Override
    public void recordStart() {

    }

    @Override
    public void recordFinish() {

    }

    @Override
    public void recordCancel() {

    }

    @Override
    public long getRecordTime() {
        return 0;
    }

    @Override
    public int getRecordDecibel() {
        return 0;
    }

    // 表情点击事件
    @Override
    public void onEmoticonClick(EmoticonsBean emoticon, boolean isDel) {
        String uri = (String) emoticon.getIconUri();
        String name = emoticon.getName();
        Log.i(ImSocketClient.TAG, "uil:" + uri + "-----name:" + name);
        sendEmoji(uri);
    }

    private final void sendEmoji(String uri) {
        if (!getTo_Id().isEmpty() && !getUid().isEmpty()) {
            String json = ImSendMessageUtils.getChatMessageEmoji(uri, getUid(), getTo_Id(), conversation, chatAdapter.getLastItemDisplayTime());
            ChatMessage message = GsonUtil.GsonToBean(json, ChatMessage.class);
            chatAdapter.setData(message);
            toLastItem();
            getPresenter().SocketSendJson(json, true);
        }
    }

    // 发送图片 等等
    @Override
    public void onAppFunClick(AppFuncBean emoticon) {
        int emoticonId = emoticon.getId();
        if (emoticonId == OPTION_TYPE_IMAGE) {
            showLoadingDialog();
            PictureSelector.create(BaseChatActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(1)
                    .minSelectNum(0)
                    .imageSpanCount(6)
                    .previewImage(true)
                    .isCamera(true)
                    .forResult(REQUEST_CODE_IMAGE);
        }
        if (emoticonId == OPTION_TYPE_LOCATION) {
            if (EasyPermission
                    .build()
                    .hasPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                goActivityForResult(SendLocationActivity.class, REQUEST_CODE_LOCATION);
            } else {
                arvixe(1, null);
            }
        }
        if (emoticonId == OPTION_TYPE_CARD) {

        }
    }

    /**
     * 动态申请权限
     */
    private final void arvixe(final int code, final Bundle bundle) {
        EasyPermission.build()
                .mRequestCode(10010)
                .mContext(this)
                .mPerms(Manifest.permission.ACCESS_FINE_LOCATION)
                .mResult(new EasyPermissionResult() {
                    @Override
                    public void onPermissionsAccess(int requestCode) {
                        super.onPermissionsAccess(requestCode);
                        if (code == 1) {
                            goActivityForResult(SendLocationActivity.class, REQUEST_CODE_LOCATION);
                        }
                        if (code == 2) {
                            goActivity(ShowLocationActivity.class, bundle);
                        }
                    }

                    @Override
                    public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                        super.onPermissionsDismiss(requestCode, permissions);

                    }
                }).requestPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dismissDialog();
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_IMAGE:
                    images = PictureSelector.obtainMultipleResult(data);
                    Log.i("TAG", "size:" + images.size());
                    if (images.size() > 0) {
                        String path = images.get(0).getPath();
                        Log.i("TAG", "PATH:" + path);
                        if (!getTo_Id().isEmpty() && !getUid().isEmpty()) {
                            String send_json = ImSendMessageUtils.getChatMessageImage(path, getUid(), getTo_Id(), conversation, chatAdapter.getLastItemDisplayTime());
                            ChatMessage message = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                            chatAdapter.setData(message);
                            toLastItem();
                            getPresenter().SocketSendJson(send_json, true);
                        }
                    }
                    break;
                case REQUEST_CODE_LOCATION:
                    Bundle bundle = data.getBundleExtra("bundle");
                    LocationBody locationBean = (LocationBody) bundle.getSerializable("bean");
                    String location_json = GsonUtil.BeanToJson(locationBean);
                    if (!getTo_Id().isEmpty() && !getUid().isEmpty()) {
                        String send_json = ImSendMessageUtils.getChatMessageLocation(location_json, getUid(), getTo_Id(), conversation, chatAdapter.getLastItemDisplayTime());
                        ChatMessage chatMessage = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                        chatAdapter.setData(chatMessage);
                        toLastItem();
                        getPresenter().SocketSendJson(send_json, true);
                    }
                    break;
                default:
                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        chatAdapter.pauseMedia();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            chatAdapter.pauseMedia();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatAdapter = null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventBus(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MSG_SEND_CHAT) {
            if (chatMessage.getConversation().equals(getConversation())) {
                chatAdapter.setData(chatMessage);
                if (isLastItem) {
                    toLastItem();
                    chatAdapter.notifyItemChanged(chatAdapter.getItemCount() - 1);
                } else {
                    if (!chatMessage.getFromId().equals(getUid())) {
                        new_msg_count += 1;
                        newMsg();
                    }
                }
                DataBaseHelp.getInstance(this).setSessionNumber(chatMessage.getConversation(), 0);
            }
        } else if (chatMessage.getType() == ChatMessage.MSG_SEND_SYS) {
            LoginBean bean = GsonUtil.GsonToBean(chatMessage.getBody(), LoginBean.class);
            if (bean.getUid().equals(getTo_Id())) {
                getPresenter().getUserInfo(bean.getUid());
                to_bean = bean;
                getUser();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void EventBusSocket(String connect) {
        if (connect.equals("connect")) {
            socketConnectState(ImSocketClient.checkSocket());
            EventBus.getDefault().removeStickyEvent(connect);
        }
    }

    private final void sendText(String text) {
        if (!getTo_Id().isEmpty() && !getUid().isEmpty()) {
            String json = ImSendMessageUtils.getChatMessageText(text, getUid(), getTo_Id(), conversation, chatAdapter.getLastItemDisplayTime());
            ChatMessage message = GsonUtil.GsonToBean(json, ChatMessage.class);
            chatAdapter.setData(message);
            toLastItem();
            getPresenter().SocketSendJson(json, true);
        }
    }

    protected void initRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setOnTouchListener(this);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem >= (totalItemCount - new_msg_count)) {
                        new_msg_count = 0;
                        newMsg();
                    } else if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        isLastItem = true;
                    } else {
                        isLastItem = false;
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });
        chatAdapter.setOnItemClickListener(this);
    }

    protected void initKeyBoard(CBEmoticonsKeyBoard keyBoard) {
        mKbView = keyBoard;
        mKbView.addOnFuncKeyBoardListener(this);
        mKbView.addVoiceChangeListener(this);
        mKbView.getBtnSend().setOnClickListener(this);
        RecordIndicator recordIndicator = new RecordIndicator(this);
        mKbView.setRecordIndicator(recordIndicator);
        recordIndicator.setOnRecordListener(this);
        recordIndicator.setMaxRecordTime(60);
        CBEmoticonsView cbEmoticonsView = new CBEmoticonsView(this);
        cbEmoticonsView.init(getSupportFragmentManager());
        mKbView.setEmoticonFuncView(cbEmoticonsView);
        cbEmoticonsView.addEmoticonsWithName(new String[]{"emoji"});
        cbEmoticonsView.setOnEmoticonClickListener(this);
    }

    protected void initOptions() {
        List<AppFuncBean> appFuncBeanList = new ArrayList<>();
        appFuncBeanList.add(new AppFuncBean(OPTION_TYPE_IMAGE, R.mipmap.icon_tuku, "图片"));
        appFuncBeanList.add(new AppFuncBean(OPTION_TYPE_LOCATION, R.mipmap.icon_weizhi, "位置"));
        appFuncBeanList.add(new AppFuncBean(OPTION_TYPE_CARD, R.mipmap.icon_wodemingpian, "名片"));
        CBAppFuncView appFuncView = new CBAppFuncView(this);
        appFuncView.setRol(3);
        appFuncView.setAppFuncBeanList(appFuncBeanList);
        mKbView.setAppFuncView(appFuncView);
        appFuncView.setOnAppFuncClickListener(this);
    }

    protected void initSmartRefresh(SmartRefreshLayout smartRefreshLayout) {
        mSmart = smartRefreshLayout;
        smartRefreshLayout.setOnRefreshListener(this);
    }

    private final void toLastItem() {
        mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private final void newMsg() {
        if (new_msg_count > 0) {
            tv_new_msg.setVisibility(View.VISIBLE);
            tv_new_msg.setText(new_msg_count + "条新消息");
        } else {
            tv_new_msg.setVisibility(View.GONE);
        }
    }
}
