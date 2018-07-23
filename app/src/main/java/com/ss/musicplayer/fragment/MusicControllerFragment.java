package com.ss.musicplayer.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Handler;
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

    private Handler mHandler = new Handler();
    private MusicViewModel mMusicViewModel;
    private MusicPlayerService.LocalBinder mBinder;

    private boolean mStoppedByTouchingSeekBar;
    private Integer mPlayingSongId;

    public MusicControllerFragment() {
    }

    public static MusicControllerFragment newInstance(MusicPlayerService.LocalBinder binder) {
        MusicControllerFragment fragment = new MusicControllerFragment();
        Bundle args = new Bundle();
        args.putSerializable("binder", binder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = (MusicPlayerService.LocalBinder) getArguments().getSerializable("binder");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
                if (mBinder.getCurrentPosition() <= 5) {
                    int i = mMusicViewModel.getPlayingSongId().getValue() - 1;
                    if (i < 0) {
                        i = i + mMusicViewModel.getSongList().getValue().size();
                    }
                    mMusicViewModel.getPlayingSongId().setValue(i);
                }
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
                int i = mMusicViewModel.getPlayingSongId().getValue() + 1;
                int s = mMusicViewModel.getSongList().getValue().size();
                mMusicViewModel.getPlayingSongId().setValue(i % s);
            }
        });

        mMusicViewModel = ViewModelProviders.of(getActivity()).get(MusicViewModel.class);
        mMusicViewModel.getPlayingSongId().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (!integer.equals(mPlayingSongId)) {
                    prepareMusicController(integer, mPlayingSongId != null);
                } else {
                    if (mBinder.isPlaying()) {
                        pause();
                    } else {
                        play();
                    }
                }
            }
        });
    }

    private void prepareMusicController(Integer position, boolean play) {
        Song song = mMusicViewModel.getSongList().getValue().get(position);

        mProgressSeekBar.setProgress(0);
        mSongNameTextView.setText(song.getTitle());
        mArtistNameTextName.setText(song.getArtist());

        mBinder.set(song.getData());

        mProgressSeekBar.setMax(mBinder.getDuration());
        mDurationTextView.setText(getTimeFormattedString(mBinder.getDuration()));
        mPlayingSongId = position;
        if (play) {
            play();
        }
    }

    private void play() {
        mBinder.play();
        playCycle();
        mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.round_pause_white_36));
        mMusicViewModel.getIsSongPlaying().setValue(true);
    }

    private void pause() {
        mBinder.pause();
        mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.round_play_arrow_white_36));
        mMusicViewModel.getIsSongPlaying().setValue(false);
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