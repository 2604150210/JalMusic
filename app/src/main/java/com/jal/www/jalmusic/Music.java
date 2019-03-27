package com.jal.www.jalmusic;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Objects;

public  class Music implements Parcelable {
    private String title;
    private String singer;
    private String album;
    private String url;
    private long size;
    private long time;
    private String name;
    public Music(){};
    protected Music(Parcel in) {
        title = in.readString();
        singer = in.readString();
        album = in.readString();
        url = in.readString();
        size = in.readLong();
        time = in.readLong();
        name = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public String getName()
    {
    return name;
    }
    public void setName(String name)
    {
    this.name = name;
    }
    public String getTitle()
    {
    return title;
    }
    public void setTitle(String title)
    {
    this.title = title;
    }
    public String getSinger()
    {
    return singer;
    }
    public void setSinger(String singer)
    {
    this.singer = singer;
    }
    public String getAlbum()
    {
    return album;
    }
    public void setAlbum(String album)
    {
    this.album = album;
    }
    public String getUrl()
    {
    return url;
    }
    public void setUrl(String url)
    {
    this.url = url;
    }
    public long getSize()
    {
    return size;
    }
    public void setSize(long size)
    {
    this.size =size;
    }
    public long getTime()
    {
    return time;
    }
    public void setTime(long time)
    {
    this.time = time;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", singer='" + singer + '\'' +
                ", album='" + album + '\'' +
                ", url='" + url + '\'' +
                ", size=" + size +
                ", time=" + time +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return size == music.size &&
                time == music.time &&
                title .equals( music.title)&&
                album.equals(music.album)&&
                url.equals(music.url)&&
                name.equals(music.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(singer);
        dest.writeString(album);
        dest.writeString(url);
        dest.writeLong(size);
        dest.writeLong(time);
        dest.writeString(name);
    }
}
