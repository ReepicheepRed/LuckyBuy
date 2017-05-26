package com.luckybuy.model;

import com.luckybuy.adapter.AwardUnveilAdapter;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class UnveilAwardModel implements Serializable{


    private long idx;

    private long goodid;

    private long timesid;

    private String title;

    private String headpic;

    private String lucktime;

    private long luckuid;

    private long luckuidx;

    private String nickname;

    private long copies;

    private long surplussecond;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
    }

    public long getTimeid() {
        return timesid;
    }

    public void setTimeid(long timeid) {
        this.timesid = timeid;
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

    public long getLuckuid() {
        return luckuid;
    }

    public void setLuckuid(long luckuid) {
        this.luckuid = luckuid;
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

    public long getCopies() {
        return copies;
    }

    public void setCopies(long copies) {
        this.copies = copies;
    }

    public long getSurplussecond() {
        return surplussecond;
    }

    public void setSurplussecond(long surplussecond) {
        this.surplussecond = surplussecond;
    }

}
