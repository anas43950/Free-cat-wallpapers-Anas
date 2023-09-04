package com.freecatwallpapers.models;

import java.io.Serializable;

public class Wallpaper implements Serializable {
    String name;
    String url;

    public Wallpaper(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
