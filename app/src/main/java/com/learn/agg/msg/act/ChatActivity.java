package com.learn.agg.msg.act;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.codebear.keyboard.CBEmoticonsKeyBoard;
import com.learn.agg.R;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.util.NetState;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

public class ChatActivity extends BaseChatActivity {


    private ImageView iv_back;
    private TextView tv_name;
    private TextView tv_net_state;

    public static void startActivity(Context context, LoginBean fromBean, LoginBean toBean){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(key.KEY_FROM,fromBean);
        intent.putExtra(key.KEY_TO,toBean);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        super.initView();
        RecyclerView rv_chat = findViewById(R.id.rv_chat);
        CBEmoticonsKeyBoard cb_kb = findViewById(R.id.cb_kb);
        iv_back = findViewById(R.id.iv_back);
        tv_name = findViewById(R.id.tv_name);
        tv_net_state = findViewById(R.id.tv_net_state);
        SmartRefreshLayout smart_refresh = findViewById(R.id.smart_refresh);
        initRecyclerView(rv_chat);
        initKeyBoard(cb_kb);
        initOptions();
        initSmartRefresh(smart_refresh);
    }

    @Override
    protected void initData() {
        super.initData();
        iv_back.setOnClickListener(this);
        String username = to_bean.getUsername();
        int online = to_bean.getOnline();
        tv_name.setText(username);
        tv_net_state.setText(NetState.getNetState(online));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.iv_back){
            mKbView.reset();
            finish();
        }
    }
}
