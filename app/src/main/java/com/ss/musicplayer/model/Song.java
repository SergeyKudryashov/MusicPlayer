package com.ss.musicplayer.model;

import android.net.Uri;

public class Song {

    private Long mId;
    private String mArtist;
    private String mTitle;
    private String mData;
    private String mDisplayName;

    public Song(Long id, String artist, String title, String data, String displayName) {
        mId = id;
        mArtist = artist;
        mTitle = title;
        mData = data;
        mDisplayName = displayName;
    }

    public Long getId() {
        return mId;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getData() {
        return mData;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((mId == null) ? 0 : mId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Song other = (Song) obj;
        if (mId == null) {
            return other.mId == null;
        } else {
            return mId.equals(other.mId);
        }
    }

    @Override
    public String toString() {
        return "[" + mId + "]:"
                + mArtist + "|"
                + mTitle + "|"
                + mData + "|"
                + mDisplayName;
    }
}
