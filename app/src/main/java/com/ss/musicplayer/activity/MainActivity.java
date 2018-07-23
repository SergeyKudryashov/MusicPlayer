package com.ss.musicplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.ss.musicplayer.R;
import com.ss.musicplayer.fragment.MusicControllerFragment;
import com.ss.musicplayer.fragment.MusicListFragment;
import com.ss.musicplayer.service.MusicPlayerService;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MusicPlayerService.LocalBinder) service;
            mIsBound = true;

            createFragments();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            mIsBound = false;
        }
    };

    private MusicPlayerService.LocalBinder mBinder;
    private boolean mIsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        startMusicPlayerService();
    }

    private void startMusicPlayerService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void createFragments() {
        FragmentManager mFragmentManager = getSupportFragmentManager();

        if (mFragmentManager.findFragmentById(R.id.music_list_fragment_container) == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.music_list_fragment_container, MusicListFragment.newInstance(mBinder), "tag1")
                    .commit();
        }

        if (mFragmentManager.findFragmentById(R.id.music_controller_fragment_container) == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.music_controller_fragment_container, MusicControllerFragment.newInstance(mBinder), "tag2")
                    .commit();
        }
    }
}