package com.example.mypc.viewpagerfrist.Model;

import java.io.Serializable;

public class Music implements Serializable {
    private String nameSong;
    private String nameSinger;
    private String path;
    private boolean local;
    private String id;
    private boolean check_fav;
    private boolean check_playlist;

    public Music(String nameSong, String nameSinger, boolean idLocal, String path, String id, boolean check_fav, boolean check_playlist) {
        this.nameSong = nameSong;
        this.nameSinger = nameSinger;
        this.local = idLocal;
        this.path=path;
        this.id=id;
        this.check_fav=check_fav;
        this.check_playlist=check_playlist;
    }

    public String getNameSong() {
        return nameSong;
    }

    public String getPath() {
        return path;
    }

    public String getNameSinger() {
        return nameSinger;
    }

    public boolean getIdLocal() {
        return local;
    }

    public String getId() {
        return id;
    }

    public boolean isLocal() {
        return local;
    }

    public boolean isCheck_fav() {
        return check_fav;
    }

    public boolean isCheck_playlist() {
        return check_playlist;
    }

    public void setCheck_fav(boolean check_fav) {
        this.check_fav = check_fav;
    }

    public void setCheck_playlist(boolean check_playlist) {
        this.check_playlist = check_playlist;
    }
}
