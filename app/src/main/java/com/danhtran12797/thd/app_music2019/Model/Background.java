package com.danhtran12797.thd.app_music2019.Model;

public class Background {
    private String id;
    private String name;
    private String color;
    private String url;

    public Background() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Background(String color, String url) {
        this.color = color;
        this.url = url;
    }

    public String getColor() {
        return color;
    }

    public String getUrl() {
        return url;
    }
}
