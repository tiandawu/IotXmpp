package com.cqupt.xmpp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cqupt.xmpp.R;

/**
 * Created by tiandawu on 2016/5/21.
 */
public class PlayAlarmSoundService extends Service {

    private final int PLAY_FINISH = 1;
    private IBinder mBinder = new MyBind();
    private MediaPlayer mMediaPlayer;
    public static boolean isLoop = false;
    public static PlayAlarmSoundService mService = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_FINISH:
                    stopSelf();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBind extends Binder {
        public PlayAlarmSoundService getService() {
            return PlayAlarmSoundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        mMediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        if (isLoop) {
            mMediaPlayer.setLooping(true);
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("tt", "播放完毕");
                if (isLoop) {
                    return;
                }
                mHandler.sendEmptyMessage(PLAY_FINISH);
            }
        });
    }

    public static PlayAlarmSoundService getInstance() {
        return mService;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playAlarmSound();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        mMediaPlayer = null;
//        Log.e("tt", "tt");
        super.onDestroy();
    }


    private void playAlarmSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaPlayer.start();
            }
        }).start();

    }

}
