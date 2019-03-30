package com.danhtran12797.thd.app_music2019.Model;

import java.io.Serializable;

public class MusicOn implements Serializable {
    private String id;
    private String id_category;
    private String name_song;
    private String name_author;
    private String song_url;
    private String download_url;

    public MusicOn() {
    }

    public MusicOn(String id, String id_category, String name_song, String name_author, String song_url, String download_url) {
        this.id = id;
        this.id_category = id_category;
        this.name_song = name_song;
        this.name_author = name_author;
        this.song_url = song_url;
        this.download_url = download_url;
    }

    public String getDownload_url() {
        return download_url;
    }

    public String getId_category() {
        return id_category;
    }

    public String getName_author() {
        return name_author;
    }

    public String getId() {
        return id;
    }

    public String getName_song() {
        return name_song;
    }

    public String getSong_url() {
        return song_url;
    }
}
