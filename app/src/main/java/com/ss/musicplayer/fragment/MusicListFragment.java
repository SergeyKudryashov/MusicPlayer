package com.ss.musicplayer.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ss.musicplayer.R;
import com.ss.musicplayer.adapter.MusicListAdapter;
import com.ss.musicplayer.model.Song;
import com.ss.musicplayer.service.MusicPlayerService;
import com.ss.musicplayer.viewmodel.MusicViewModel;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {

    private static final Uri CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 101;

    private MusicListAdapter mMusicListAdapter;

    private MusicViewModel mMusicViewModel;
    private MusicPlayerService.LocalBinder mBinder;

    private MusicListAdapter.OnItemClickListener mOnClickListener = new MusicListAdapter.OnItemClickListener() {
        @Override
        public void onClickItem(int position) {
            mMusicViewModel.getPlayingSongId().setValue(position);
        }
    };


    public MusicListFragment() {
    }

    public static MusicListFragment newInstance(MusicPlayerService.LocalBinder binder) {
        MusicListFragment fragment = new MusicListFragment();
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
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        loadSongs();
    }

    private void init(View view) {
        mMusicViewModel = ViewModelProviders.of(getActivity()).get(MusicViewModel.class);
        mMusicViewModel.getSongList().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                mMusicListAdapter.setList(songs);
            }
        });
        mMusicViewModel.getIsSongPlaying().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mMusicListAdapter.notifyDataSetChanged();
            }
        });

        mMusicListAdapter = new MusicListAdapter(getActivity(), mBinder, mMusicViewModel);
        mMusicListAdapter.setOnItemClickListener(mOnClickListener);

        RecyclerView recyclerView = view.findViewById(R.id.music_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mMusicListAdapter);
    }

    private boolean hasReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            }
        }
    }

    private void loadSongs() {
        if (!hasReadExternalStoragePermission())
            return;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = getActivity().getContentResolver().query(CONTENT_URI,
                null,
                selection,
                null,
                sortOrder);

        if (cursor == null)
            throw new IllegalStateException("Query failed");
        if (!cursor.moveToFirst())
            return;

        List<Song> list = new ArrayList<>();
        do {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            Song song = new Song(id, artist, title, data, displayName);
            list.add(song);
        } while (cursor.moveToNext());
        cursor.close();

        mMusicViewModel.getSongList().setValue(list);
        mMusicViewModel.getPlayingSongId().setValue(0);
    }
}