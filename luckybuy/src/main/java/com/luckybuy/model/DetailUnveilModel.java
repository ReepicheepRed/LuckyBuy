package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/22.
 */
public class DetailUnveilModel implements Serializable {

    private String status;

    private long timesid;

    private String lucktime;

    private long luckid;

    private long uidx;

    private String nickname;

    private String headpic;

    private long copies;

    private long buycopies;

    private String buyid;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeid() {
        return timesid;
    }

    public void setTimeid(long timesid) {
        this.timesid = timesid;
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

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public long getCopies() {
        return copies;
    }

    public void setCopies(long copies) {
        this.copies = copies;
    }

    public long getBuycopies() {
        return buycopies;
    }

    public void setBuycopies(long buycopies) {
        this.buycopies = buycopies;
    }

    public String getBuyid() {
        return buyid;
    }

    public void setBuyid(String buyid) {
        this.buyid = buyid;
    }
}
