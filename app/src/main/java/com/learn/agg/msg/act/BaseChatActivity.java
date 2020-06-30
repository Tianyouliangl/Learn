package com.learn.agg.msg.act;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.agg.widgets.FileUpLoadManager;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.body.ImageBody;
import com.learn.commonalitylibrary.body.VoiceBody;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.OfTenUtils;
import com.lib.xiangxiang.im.ImSocketClient;
import com.lib.xiangxiang.im.SocketManager;
import com.location.com.LocationBody;
import com.location.com.PoiltemBean;
import com.location.com.SendLocationActivity;
import com.location.com.ShowLocationActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseChatActivity extends BaseMvpActivity<BaseChatContract.IPresenter> implements BaseChatContract.IView, View.OnLayoutChangeListener, FuncLayout.OnFuncKeyBoardListener, View.OnClickListener, RecordIndicator.OnRecordListener, CBEmoticonsView.OnEmoticonClickListener, CBAppFuncView.OnAppFuncClickListener, View.OnTouchListener, OnRefreshListener, SocketManager.SendMsgCallBack, CBVoice.VoiceStateListener, ChatAdapter.itemClickListener {


    private static final int REQUEST_CODE_IMAGE = 1231;
    private static final int REQUEST_CODE_LOCATION = 1232;
    private RecyclerView mRecyclerView;
    private ChatAdapter chatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
    public CBEmoticonsKeyBoard mKbView;
    private LoginBean from_bean;
    private int OPTION_TYPE_CARD = 1;
    private int OPTION_TYPE_IMAGE = 2;
    private int OPTION_TYPE_LOCATION = 3;
    private int pageNo = 1;
    private int pageSize = 30;
    public LoginBean to_bean;
    private String conviction;
    private SmartRefreshLayout mSmart;
    private LinearLayoutManager layoutManager;
    private String send_json = null;
    private Boolean isLastItem = false;
    private TextView tv_new_msg;
    private int new_msg_count = 0;

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
        getPresenter().getHistory(pageNo, pageSize);
    }

    // socket发送消息回调
    @Override
    public void call(String msg) {
        ChatMessage bean = GsonUtil.GsonToBean(msg, ChatMessage.class);
        DataBaseHelp.getInstance(this).updateChatMessage(bean);
        if (chatAdapter != null) {
            chatAdapter.notifyChatMessage(bean);
        }
    }

    // 语音回调
    @Override
    public void onStartVoice(String pid) {

    }

    @Override
    public void onCancelVoice(String pid) {
        showToast("取消");
    }

    @Override
    public void onEndVoice(String filePath, String fileAbsPath, long time, String pid) {
        if (time < 1000) {
            showToast("时间过短");
            return;
        }
        Log.i("TAG", "filePath:" + filePath + "\n" + "fileAbsPath:" + fileAbsPath + "\n" + "time:" + time);
        send_json = ImSendMessageUtils.getChatMessageVoice(filePath, fileAbsPath, time, from_bean.getUid(), to_bean.getUid(), conviction, chatAdapter.getLastItemDisplayTime());
        ChatMessage message = GsonUtil.GsonToBean(send_json, ChatMessage.class);
        chatAdapter.setData(message);
        toLastItem();
        upLoadFile(fileAbsPath,2,null);
        new FileUpLoadManager().upLoadFile(fileAbsPath, new FileUpLoadManager.FileUpLoadCallBack() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(String url) {
                if (send_json != null) {
                    ChatMessage chatMessage = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                    VoiceBody voiceBody = GsonUtil.GsonToBean(chatMessage.getBody(), VoiceBody.class);
                    String toJson = GsonUtil.BeanToJson(new VoiceBody(voiceBody.getFileName(), voiceBody.getFileAbsPath(), url, voiceBody.getTime(), voiceBody.getState(), voiceBody.getVoice_content()));
                    chatMessage.setBody(toJson);
                    chatAdapter.notifyChatMessage(chatMessage);
                    String json = GsonUtil.BeanToJson(chatMessage);
                    SocketSendJson(json);
                    send_json = null;
                }
            }

            @Override
            public void onProgress(int pro) {

            }
        });
    }

    @Override
    public void onClickItemImage(String image_url) {
        List<ChatMessage> data = chatAdapter.getData();
        List<LocalMedia> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < data.size(); i++) {
            int bodyType = data.get(i).getBodyType();
            if (bodyType == ChatMessage.MSG_BODY_TYPE_IMAGE) {
                LocalMedia media = new LocalMedia();
                String body = data.get(i).getBody();
                ImageBody imageBody = GsonUtil.GsonToBean(body, ImageBody.class);
                media.setPath(imageBody.getImage());
                list.add(media);
            }
        }

        for (int j = 0; j < list.size(); j++) {
            LocalMedia url = list.get(j);
            if (image_url.equals(url.getPath())) {
                index = j;
                break;
            }
        }
        PictureSelector.create(this).themeStyle(R.style.picture_default_style).openExternalPreview(index, list);
    }

    @Override
    public void onClickLocation(LocationBody body) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean",body);
        if (EasyPermission
                .build()
                .hasPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            goActivity(ShowLocationActivity.class,bundle);
        } else {
            arvixe(2,bundle);
        }
    }

    @Override
    public void onVoiceRead(ChatMessage message) {
        getPresenter().updateHistory(message.getPid());
    }

    public interface key {
        String KEY_FROM = "key_from_bean";
        String KEY_TO = "key_to_bean";
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

    protected void initRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnLayoutChangeListener(this);
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

    @Override
    protected void initView() {
        tv_new_msg = findViewById(R.id.tv_new_msg);
    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        from_bean = (LoginBean) intent.getSerializableExtra(key.KEY_FROM);
        to_bean = (LoginBean) intent.getSerializableExtra(key.KEY_TO);
        if (null == from_bean || null == to_bean.getUid()) {
            finish();
        }
        chatAdapter.setFromUserBean(from_bean);
        chatAdapter.setToUserBean(to_bean);
        conviction = OfTenUtils.getConviction(from_bean.getUid(), to_bean.getUid());
        DataBaseHelp.getInstance(this).setSessionNumber(conviction, 0);
        showLoadingDialog();
        getPresenter().getHistory(pageNo, pageSize);
        tv_new_msg.setOnClickListener(this);
    }

    @Override
    public String getUid() {
        return from_bean.getUid();
    }

    @Override
    public String getConversation() {
        return conviction;
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
        }
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


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom < oldBottom) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (chatAdapter.getItemCount() > 0) {
                        mRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                        new_msg_count = 0;
                        isLastItem = true;
                    }
                }
            });
        }
    }


    @Override
    public void onFuncPop(int height) {

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventBus(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessage.MSG_SEND_CHAT) {
            if (chatMessage.getToId().equals(from_bean.getUid())){
                chatAdapter.setData(chatMessage);
                chatAdapter.notifyItemChanged(chatAdapter.getItemCount() - 1);
                if (isLastItem) {
                    toLastItem();
                } else {
                    new_msg_count += 1;
                    newMsg();
                }
                DataBaseHelp.getInstance(this).setSessionNumber(chatMessage.getConversation(), 0);
            }
        }
    }

    private final void sendText(String text) {
        if (!to_bean.getUid().isEmpty() && !from_bean.getUid().isEmpty()) {
            String json = ImSendMessageUtils.getChatMessageText(text, from_bean.getUid(), to_bean.getUid(), conviction, chatAdapter.getLastItemDisplayTime());
            ChatMessage message = GsonUtil.GsonToBean(json, ChatMessage.class);
            chatAdapter.setData(message);
            toLastItem();
            SocketSendJson(json);
        }
    }

    private final void SocketSendJson(String json) {
        ChatMessage chatMessage = GsonUtil.GsonToBean(json, ChatMessage.class);
        SocketManager.sendMsgSocket(this, json, this);
        DataBaseHelp.getInstance(this).addChatMessage(chatMessage);
        SessionMessage sessionMessage = new SessionMessage();
        sessionMessage.setConversation(chatMessage.getConversation());
        sessionMessage.setTo_id(to_bean.getUid());
        sessionMessage.setBody(chatMessage.getBody());
        sessionMessage.setMsg_status(chatMessage.getMsgStatus());
        sessionMessage.setTime(chatMessage.getTime());
        sessionMessage.setBody_type(chatMessage.getBodyType());
        sessionMessage.setNumber(0);
        DataBaseHelp.getInstance(this).addOrUpdateSession(sessionMessage);
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
        if (!to_bean.getUid().isEmpty() && !from_bean.getUid().isEmpty()) {
            String json = ImSendMessageUtils.getChatMessageEmoji(uri, from_bean.getUid(), to_bean.getUid(), conviction, chatAdapter.getLastItemDisplayTime());
            ChatMessage message = GsonUtil.GsonToBean(json, ChatMessage.class);
            chatAdapter.setData(message);
            toLastItem();
            SocketSendJson(json);
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
                arvixe(1,null);
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
                        if (code == 1){
                            goActivityForResult(SendLocationActivity.class, REQUEST_CODE_LOCATION);
                        }
                       if (code == 2){
                           goActivity(ShowLocationActivity.class,bundle);
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
                        if (!to_bean.getUid().isEmpty() && !from_bean.getUid().isEmpty()) {
                            send_json = ImSendMessageUtils.getChatMessageImage(path, from_bean.getUid(), to_bean.getUid(), conviction, chatAdapter.getLastItemDisplayTime());
                            ChatMessage message = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                            chatAdapter.setData(message);
                            toLastItem();
                            upLoadFile(path,1,null);
                        }
                    }
                    break;
                case REQUEST_CODE_LOCATION:
                    Bundle bundle = data.getBundleExtra("bundle");
                    LocationBody locationBean = (LocationBody) bundle.getSerializable("bean");
                    String location_json = GsonUtil.BeanToJson(locationBean);
                    String location_url = locationBean.getLocation_url();
                    if (!to_bean.getUid().isEmpty() && !from_bean.getUid().isEmpty()) {
                        send_json = ImSendMessageUtils.getChatMessageLocation(location_json, from_bean.getUid(), to_bean.getUid(), conviction, chatAdapter.getLastItemDisplayTime());
                        ChatMessage chatMessage = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                        chatAdapter.setData(chatMessage);
                        toLastItem();
                        upLoadFile(location_url,3,locationBean);
                    }
                    break;
                default:
                    break;

            }
        }
    }

    private final void upLoadFile(String path, final int code, final LocationBody body) {
        new FileUpLoadManager().upLoadFile(path, new FileUpLoadManager.FileUpLoadCallBack() {

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(String url) {
                if (send_json != null) {
                    ChatMessage chatMessage = GsonUtil.GsonToBean(send_json, ChatMessage.class);
                    if (code == 1){
                        chatMessage.setBody(GsonUtil.BeanToJson(new ImageBody(url)));
                    }
                    if (code == 2){
                        VoiceBody voiceBody = GsonUtil.GsonToBean(chatMessage.getBody(), VoiceBody.class);
                        String toJson = GsonUtil.BeanToJson(new VoiceBody(voiceBody.getFileName(), voiceBody.getFileAbsPath(), url, voiceBody.getTime(), voiceBody.getState(), voiceBody.getVoice_content()));
                        chatMessage.setBody(toJson);
                    }
                    if (code == 3){
                        if (body != null){
                            body.setUrl(url);
                            chatMessage.setBody(GsonUtil.BeanToJson(body));
                        }
                    }
                    chatAdapter.notifyChatMessage(chatMessage);
                    String json = GsonUtil.BeanToJson(chatMessage);
                    SocketSendJson(json);
                    send_json = null;
                }
            }

            @Override
            public void onProgress(int pro) {

            }
        });
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
}
