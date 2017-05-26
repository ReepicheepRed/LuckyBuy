package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/5/19.
 */
public class BannerModel implements Serializable {

    private int fdi;

    private String img;

    private String link;

    private long goodid;

    public int getFdi() {
        return fdi;
    }

    public void setFdi(int fdi) {
        this.fdi = fdi;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
    }
}
