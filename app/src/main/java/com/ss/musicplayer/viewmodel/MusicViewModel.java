package com.ss.musicplayer.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ss.musicplayer.model.Song;

import java.util.List;

public class MusicViewModel extends AndroidViewModel {

    private MutableLiveData<List<Song>> mSongList;
    private MutableLiveData<Integer> mPlayingSongId;
    private MutableLiveData<Boolean> mIsSongPlaying;

    public MusicViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Song>> getSongList() {
        if (mSongList == null) {
            mSongList = new MutableLiveData<>();
        }
        return mSongList;
    }

    public MutableLiveData<Integer> getPlayingSongId() {
        if (mPlayingSongId == null) {
            mPlayingSongId = new MutableLiveData<>();
        }
        return mPlayingSongId;
    }

    public MutableLiveData<Boolean> getIsSongPlaying() {
        if (mIsSongPlaying == null) {
            mIsSongPlaying = new MutableLiveData<>();
        }
        return mIsSongPlaying;
    }
}