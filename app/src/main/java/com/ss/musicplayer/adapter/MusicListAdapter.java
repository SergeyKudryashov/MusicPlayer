package com.ss.musicplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ss.musicplayer.R;
import com.ss.musicplayer.model.Song;
import com.ss.musicplayer.service.MusicPlayerService;
import com.ss.musicplayer.viewmodel.MusicViewModel;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private List<Song> mSongList;
    private MusicPlayerService.LocalBinder mBinder;
    private MusicViewModel mMusicViewModel;

    public MusicListAdapter(Context context, MusicPlayerService.LocalBinder binder, MusicViewModel viewModel) {
        mContext = context;
        mSongList = new ArrayList<>();
        mBinder = binder;
        mMusicViewModel = viewModel;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(mSongList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public void setList(List<Song> list) {
        mSongList.clear();
        mSongList.addAll(list);
        notifyDataSetChanged();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {

        private ImageView mPlayPauseImageView;
        private TextView mSongNameImageView;
        private TextView mArtistNameImageView;

        MusicViewHolder(View itemView) {
            super(itemView);
            mPlayPauseImageView = itemView.findViewById(R.id.play_pause_image_view);
            mSongNameImageView = itemView.findViewById(R.id.song_name_text_view);
            mArtistNameImageView = itemView.findViewById(R.id.artist_name_text_view);
        }

        void bind(Song song, final Integer position) {
            mSongNameImageView.setText(song.getTitle());
            mArtistNameImageView.setText(song.getArtist());

            if (position.equals(mMusicViewModel.getPlayingSongId().getValue()) && mBinder.isPlaying()) {
                mPlayPauseImageView.setImageDrawable(mContext.getDrawable(R.drawable.round_pause_white_36));
            } else {
                mPlayPauseImageView.setImageDrawable(mContext.getDrawable(R.drawable.round_play_arrow_white_36));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener == null)
                        return;

                    mListener.onClickItem(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClickItem(int position);
    }
}