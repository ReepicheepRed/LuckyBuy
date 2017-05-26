package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/20.
 */
public class BulletinModel implements Serializable {

    private long timesid;

    private long goodid;

    private String title;

    private String headpic;

    private String lucktime;

    private long luckid;

    private long luckuidx;

    private String nickname;

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getLucktime() {
        return lucktime;
    }

    public void setLucktime(String lucktime) {
        this.lucktime = lucktime;
    }

    public long getLuckid() {
        return luckid;
    }

    public void setLuckid(long luckid) {
        this.luckid = luckid;
    }

    public long getLuckuidx() {
        return luckuidx;
    }

    public void setLuckuidx(long luckuidx) {
        this.luckuidx = luckuidx;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
