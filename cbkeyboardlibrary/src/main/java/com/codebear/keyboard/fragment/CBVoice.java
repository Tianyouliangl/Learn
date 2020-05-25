package com.codebear.keyboard.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codebear.keyboard.R;

/**
 * author : fengzhangwei
 * date : 2020/1/14
 */
public class CBVoice extends RelativeLayout implements View.OnTouchListener {


    private View mRootView;
    private Context mContext;
    private TextView tv_hint;
    private ImageView iv_voice;
    private ImageView iv_delete;
    private ImageView iv_audio_note;
    private TextView tv_bot_hint;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 0:
                   show();
                   break;
           }
        }
    };


    public CBVoice(Context context) {
        this(context,null);
        initView(context);
    }

    public CBVoice(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        initView(context);
    }

    public CBVoice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData();
    }

    private void initData() {
        tv_hint.setText("按住说话");
        iv_voice.setOnTouchListener(this);
    }

    private void initView(Context context) {
        mContext = context;
        mRootView = View.inflate(context,R.layout.item_key_board_voice, this);
        tv_hint = mRootView.findViewById(R.id.tv_hint);
        tv_bot_hint = mRootView.findViewById(R.id.tv_bot_hint);
        iv_voice = mRootView.findViewById(R.id.iv_voice);
        iv_delete = mRootView.findViewById(R.id.iv_delete);
        iv_audio_note = mRootView.findViewById(R.id.iv_audio_note);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mHandler.postDelayed(mRunnable,300);
                break;
            case MotionEvent.ACTION_MOVE:
                float xEnd = event.getX();
                int xD = 500;
                float xR = xD + iv_delete.getWidth();
                if (xEnd >= xD && xEnd <= xR){
                    iv_delete.setImageResource(R.mipmap.icon_delete_to);
                    tv_hint.setText("松手取消发送");
                }else {
                    iv_delete.setImageResource(R.mipmap.icon_delete);
                    tv_hint.setText("00:01");
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mRunnable);
                hint();
                break;
            default:
                break;
        }
        return true;
    }

    private void hint() {
        if (iv_delete.getVisibility() == View.VISIBLE){
            iv_delete.setVisibility(GONE);
        }
        if (iv_audio_note.getVisibility() == View.VISIBLE ){
            iv_audio_note.setVisibility(GONE);
        }
        tv_hint.setText("按住说话");
    }

    private void show() {
        if (iv_delete.getVisibility() != View.VISIBLE){
            iv_delete.setVisibility(VISIBLE);
        }
        if (iv_audio_note.getVisibility() != View.VISIBLE ){
            iv_audio_note.setVisibility(VISIBLE);
        }
        tv_hint.setText("00:01");
    }

  Runnable  mRunnable = new Runnable() {
        @Override
        public void run() {
            show();
        }
    };

}
