package com.learn.agg.msg.act;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import com.codebear.keyboard.CBEmoticonsKeyBoard;
import com.learn.agg.R;
import com.learn.agg.net.bean.LoginBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

public class ChatActivity extends BaseChatActivity {


    public static void startActivity(Context context,LoginBean fromBean, LoginBean toBean){
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
        RecyclerView rv_chat = findViewById(R.id.rv_chat);
        CBEmoticonsKeyBoard cb_kb = findViewById(R.id.cb_kb);
        SmartRefreshLayout smart_refresh = findViewById(R.id.smart_refresh);
        initRecyclerView(rv_chat);
        initKeyBoard(cb_kb);
        initOptions();
        initSmartRefresh(smart_refresh);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
