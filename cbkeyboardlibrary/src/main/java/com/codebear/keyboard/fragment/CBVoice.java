package com.codebear.keyboard.fragment;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codebear.keyboard.R;
import com.learn.commonalitylibrary.util.AudioManager;
import com.learn.commonalitylibrary.util.ImSendMessageUtils;
import com.learn.commonalitylibrary.util.TimeUtil;

/**
 * author : fengzhangwei
 * date : 2020/1/14
 */
public class CBVoice extends RelativeLayout implements View.OnTouchListener, AudioManager.AudioStateListener {


    public static final String DEF_FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/learn/voice/";
    private View mRootView;
    private TextView tv_hint;
    private ImageView iv_voice;
    private ImageView iv_delete;
    private ImageView iv_audio_note;
    private long time = 0;
    private VoiceStateListener mVoiceListener;
    private Boolean whereShowTime = true;
    private Context mContext;
    private String pid;


    public interface VoiceStateListener {
        void onStartVoice(String pid);

        void onCancelVoice(String pid);

        void onEndVoice(String filePath, String fileAbsPath, long time,String pid);
    }

    public void setOnVoiceChangeListener(VoiceStateListener listener) {
        mVoiceListener = listener;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    time += 1000;
                    if (whereShowTime) {
                        String times = TimeUtil.getTime(time, TimeUtil.FORMAT_VOICE_TIME);
                        tv_hint.setText(times);
                    }
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                    break;
                case 1:
                    tv_hint.setText("准备中...");
                    showVibrator();
                    audioManager.prepareAudio();
                    break;
            }
        }
    };
    private AudioManager audioManager;


    public CBVoice(Context context) {
        this(context, null);
        initView(context);
    }

    public CBVoice(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView(context);
    }

    public CBVoice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData();
    }

    private void initData() {
        audioManager = AudioManager.getInstance(DEF_FILEPATH);
        tv_hint.setText("按住说话");
        iv_voice.setOnTouchListener(this);
        audioManager.setOnAudioStateListener(this);
    }

    private void initView(Context context) {
        mContext = context;
        mRootView = View.inflate(context, R.layout.item_key_board_voice, this);
        tv_hint = mRootView.findViewById(R.id.tv_hint);
        iv_voice = mRootView.findViewById(R.id.iv_voice);
        iv_delete = mRootView.findViewById(R.id.iv_delete);
        iv_audio_note = mRootView.findViewById(R.id.iv_audio_note);
        iv_audio_note.setVisibility(GONE);
        iv_delete.setVisibility(GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int mDeLeft = iv_delete.getLeft() + iv_delete.getWidth();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHandler.sendEmptyMessageDelayed(1,400);
                break;
            case MotionEvent.ACTION_MOVE:
                float mX = event.getX();
                float mY = event.getY();
                if (mX > 0) {
                    if (mX >= (iv_delete.getLeft() - iv_voice.getLeft()) && mX <= mDeLeft
                            && mY >= (iv_delete.getTop() - iv_delete.getHeight()) && mY <= iv_delete.getTop()) {
                        iv_delete.setImageResource(R.mipmap.icon_delete_to);
                        tv_hint.setText("松手取消发送");
                        whereShowTime = false;
                    } else {
                        whereShowTime = true;
                        iv_audio_note.setImageResource(R.mipmap.icon_wenzi);
                        iv_delete.setImageResource(R.mipmap.icon_delete);
                        showTime();
                    }
                } else {
                    mX = Math.abs(mX);
                    if (mX >= iv_audio_note.getRight() && mX < (iv_audio_note.getRight() + iv_audio_note.getWidth())
                            && mY >= (iv_audio_note.getTop() - iv_audio_note.getHeight()) && mY <= (iv_audio_note.getTop())) {
                        iv_audio_note.setImageResource(R.mipmap.icon_wenzito);
                        tv_hint.setText("松手转文字");
                        whereShowTime = false;
                    } else {
                        whereShowTime = true;
                        iv_audio_note.setImageResource(R.mipmap.icon_wenzi);
                        iv_delete.setImageResource(R.mipmap.icon_delete);
                        showTime();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeMessages(1);
                float x = event.getX();
                float y = event.getY();
                tv_hint.setText("按住说话");
                if (iv_delete.getVisibility() == GONE || iv_audio_note.getVisibility() == GONE){
                    hint();
                    removeHandle();
                    break;
                }else {
                    if (x > 0) {
                        if (x >= (iv_delete.getLeft() - iv_voice.getLeft()) && x <= mDeLeft
                                && y >= (iv_delete.getTop() - iv_delete.getHeight()) && y <= iv_delete.getTop()) {
                            audioManager.cancel();
                            if (mVoiceListener != null) {
                                mVoiceListener.onCancelVoice(pid);
                            }
                            hint();
                            removeHandle();
                            break;
                        }
                    } else {
                        x = Math.abs(x);
                        if (x >= iv_audio_note.getRight() && x < (iv_audio_note.getRight() + iv_audio_note.getWidth())
                                && y >= (iv_audio_note.getTop() - iv_audio_note.getHeight()) && y <= iv_audio_note.getTop()) {
                            audioManager.cancel();
                            hint();
                            removeHandle();
                            break;
                        }
                    }
                    audioManager.release();
                    if (mVoiceListener != null) {
                        mVoiceListener.onEndVoice(audioManager.getFileName(), audioManager.getCurrentFilePath(), time,pid);
                    }
                }
                hint();
                removeHandle();
                break;
            default:
                break;
        }
        return true;
    }

    private void removeHandle() {
        time = 0;
        mHandler.removeMessages(0);
    }

    private void hint() {
        iv_delete.setVisibility(GONE);
        iv_audio_note.setVisibility(GONE);
        whereShowTime = true;
        iv_delete.setImageResource(R.mipmap.icon_delete);
        iv_audio_note.setImageResource(R.mipmap.icon_wenzi);
    }

    private void show() {
        iv_delete.setVisibility(VISIBLE);
        iv_audio_note.setVisibility(VISIBLE);
    }

    private void showTime() {
        if (whereShowTime) {
            String times = TimeUtil.getTime(time, TimeUtil.FORMAT_VOICE_TIME);
            tv_hint.setText(times);
        }
    }


    @Override
    public void onStart() {
        Log.i("TAG", "开始---" + whereShowTime);
        show();
        showTime();
        pid = ImSendMessageUtils.getPid();
        if (mVoiceListener != null) {
            mVoiceListener.onStartVoice(pid);
        }
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void onCancel() {
        if (mVoiceListener != null) {
            mVoiceListener.onCancelVoice(pid);
        }
    }

    private void showVibrator() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
        long[] patter = {100, 100};
        vibrator.vibrate(patter, -1);
    }
}
