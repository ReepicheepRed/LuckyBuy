package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/28.
 */
public class SnatchAwardModel implements Serializable {

    private String title;

    private String subtitle;

    private String headpic;

    private long saled;

    private long total;

    private long goodid;

    private long timesid;

    private long luckcopies;

    private long copies;

    private long luckuidx;

    private String nickname;

    private String luckcopieS1;

    private long persize;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public long getSaled() {
        return saled;
    }

    public void setSaled(long saled) {
        this.saled = saled;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
    }

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
    }

    public long getLuckcopies() {
        return luckcopies;
    }

    public void setLuckcopies(long luckcopies) {
        this.luckcopies = luckcopies;
    }

    public long getCopies() {
        return copies;
    }

    public void setCopies(long copies) {
        this.copies = copies;
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

    public String getLuckcopieS1() {
        return luckcopieS1;
    }

    public void setLuckcopieS1(String luckcopieS1) {
        this.luckcopieS1 = luckcopieS1;
    }

    public long getPersize() {
        return persize;
    }

    public void setPersize(long persize) {
        this.persize = persize;
    }
}
