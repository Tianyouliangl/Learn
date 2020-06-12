package com.learn.commonalitylibrary.util;

import android.media.MediaPlayer;

import java.io.IOException;

public class MediaManager {

    private static MediaPlayer mMediaPlayer;

    private static boolean isPause;
    private static onPlayListener mListener;

    public interface onPlayListener {
        void onStart();

        void onPause();

        void OnCompletion(MediaPlayer mp);

        void onRelease();
    }

    public static void playSound(String filePath, final onPlayListener listener) {
        mListener = listener;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.OnCompletion(mp);
                }
            });
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            if (mListener != null){
                mListener.onStart();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
            if (mListener != null){
                mListener.onPause();
            }
        }
    }

    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
            if (mListener != null){
                mListener.onStart();
            }
        }
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (mListener != null){
                mListener.onRelease();
            }
        }
    }
}
