package com.ss.musicplayer.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ss.musicplayer.R;
import com.ss.musicplayer.model.Song;
import com.ss.musicplayer.service.MusicPlayerService;
import com.ss.musicplayer.viewmodel.MusicViewModel;

import java.util.Locale;

public class MusicControllerFragment extends Fragment {

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MusicPlayerService.LocalBinder) service;
            prepareMusicController(mMusicViewModel.getSongList().getValue().get(0));
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            mIsBound = false;
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            playCycle();
        }
    };


    private TextView mSongNameTextView;
    private TextView mArtistNameTextName;
    private TextView mTimeProgressTextView;
    private TextView mDurationTextView;
    private ImageButton mPlayPauseButton;
    private ImageButton mRewindButton;
    private ImageButton mForwardButton;
    private SeekBar mProgressSeekBar;


    private MusicPlayerService.LocalBinder mBinder;
    private boolean mIsBound;
    private boolean mStoppedByTouchingSeekBar;
    private Handler mHandler = new Handler();

    private MusicViewModel mMusicViewModel;

    public MusicControllerFragment() {
    }

    public static MusicControllerFragment newInstance() {
        MusicControllerFragment fragment = new MusicControllerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        startMusicPlayerService();
        init(view);
    }

    private void init(View view) {
        mSongNameTextView = view.findViewById(R.id.song_name_text_view);
        mArtistNameTextName = view.findViewById(R.id.artist_name_text_view);
        mTimeProgressTextView = view.findViewById(R.id.time_progress_text_view);
        mDurationTextView = view.findViewById(R.id.duration_text_view);
        mPlayPauseButton = view.findViewById(R.id.play_pause_button);
        mRewindButton = view.findViewById(R.id.rewind_button);
        mForwardButton = view.findViewById(R.id.forward_button);
        mProgressSeekBar = view.findViewById(R.id.progress_seek_bar);

        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBinder.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        });

        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mBinder.seekTo(progress);
                mTimeProgressTextView.setText(getTimeFormattedString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mBinder.isPlaying()) {
                    mBinder.pause();
                    mStoppedByTouchingSeekBar = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mStoppedByTouchingSeekBar) {
                    mBinder.play();
                    mStoppedByTouchingSeekBar = false;
                }
            }
        });

        mRewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mProgressSeekBar.setProgress(0, true);
                } else {
                    mProgressSeekBar.setProgress(0);
                }
                mBinder.seekTo(0);
            }
        });

        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMusicViewModel = ViewModelProviders.of(getActivity()).get(MusicViewModel.class);
        mMusicViewModel.getPlayingSong().observe(this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                prepareMusicController(song);
                play();
            }
        });
    }

    private void startMusicPlayerService() {
        Intent intent = new Intent(getActivity().getApplicationContext(), MusicPlayerService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void prepareMusicController(Song song) {
        mProgressSeekBar.setProgress(0);
        mSongNameTextView.setText(song.getTitle());
        mArtistNameTextName.setText(song.getArtist());

        mBinder.set(song.getData());
        mProgressSeekBar.setMax(mBinder.getDuration());
        mDurationTextView.setText(getTimeFormattedString(mBinder.getDuration()));
    }

    private void play() {
        mBinder.play();
        playCycle();
        mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.round_pause_white_36));
    }

    private void pause() {
        mBinder.pause();
        mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.round_play_arrow_white_36));
    }

    private void stop() {
        mProgressSeekBar.setProgress(0);
        mBinder.stop();
        mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.round_play_arrow_white_36));
    }

    private void playCycle() {
        if (mProgressSeekBar.getProgress() == mProgressSeekBar.getMax()) {
            stop();
            mHandler.removeCallbacks(mRunnable);
        } else {
            if (mBinder.isPlaying()) {
                mProgressSeekBar.setProgress(mBinder.getCurrentPosition());
                mHandler.postDelayed(mRunnable, 1000);
            }
        }
    }

    private String getTimeFormattedString(int time) {
        return String.format(Locale.getDefault(), "%d:%02d", time / 60, time % 60);
    }
}