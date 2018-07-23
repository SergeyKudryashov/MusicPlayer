package com.ss.musicplayer.service;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.ss.musicplayer.R;

import java.io.IOException;
import java.io.Serializable;

public class MusicPlayerService extends IntentService {

    private LocalBinder mLocalBinder;
    private MediaPlayer mPlayer;

    public MusicPlayerService() {
        super(MusicPlayerService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mLocalBinder == null)
            mLocalBinder = new LocalBinder();
        return mLocalBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.release();
        return super.onUnbind(intent);
    }

    private void setMusic(String path) {
        try {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic() {
        mPlayer.start();
    }

    private void pauseMusic() {
        mPlayer.pause();
    }

    private void stopMusic() {
        mPlayer.pause();
        mPlayer.seekTo(0);
    }

    private int getMusicDuration() {
        return mPlayer.getDuration() / 1000;
    }

    private int getMusicCurrentPosition() {
        return mPlayer.getCurrentPosition() / 1000;
    }

    private void seekMusicTo(int i) {
        mPlayer.seekTo(i * 1000);
    }

    private boolean isMusicPlaying() {
        return mPlayer.isPlaying();
    }

    public class LocalBinder extends Binder implements Serializable {

        public void play() {
            playMusic();
        }

        public void pause() {
            pauseMusic();
        }

        public void stop() {
            stopMusic();
        }

        public int getDuration() {
            return getMusicDuration();
        }

        public int getCurrentPosition() {
            return getMusicCurrentPosition();
        }

        public void seekTo(int i) {
            seekMusicTo(i);
        }

        public boolean isPlaying() {
            return isMusicPlaying();
        }

        public void set(String path) {
            setMusic(path);
        }
    }
}