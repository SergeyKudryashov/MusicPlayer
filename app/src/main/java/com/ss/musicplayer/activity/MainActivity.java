package com.ss.musicplayer.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ss.musicplayer.R;
import com.ss.musicplayer.fragment.MusicControllerFragment;
import com.ss.musicplayer.fragment.MusicListFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        mFragmentManager = getSupportFragmentManager();


        if (mFragmentManager.findFragmentById(R.id.music_list_fragment_container) == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.music_list_fragment_container, MusicListFragment.newInstance(), "tag1")
                    .commit();
        }

        if (mFragmentManager.findFragmentById(R.id.music_controller_fragment_container) == null) {
            mFragmentManager.beginTransaction()
                    .add(R.id.music_controller_fragment_container, MusicControllerFragment.newInstance(), "tag2")
                    .commit();
        }
    }
}