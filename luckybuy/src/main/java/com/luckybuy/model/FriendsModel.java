package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/28.
 */
public class FriendsModel implements Serializable {

    private long uidx;
    private String fbuserid;
    private String nickname;
    private String headpic;
    private long luckcoin;
    private long ishot;
    private long pos;
    private long timesid;
    private long luckcount;
    private long income;
    private long rankpos;

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public String getFbuserid() {
        return fbuserid;
    }

    public void setFbuserid(String fbuserid) {
        this.fbuserid = fbuserid;
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

    public long getLuckcoin() {
        return luckcoin;
    }

    public void setLuckcoin(long luckcoin) {
        this.luckcoin = luckcoin;
    }

    public long getIshot() {
        return ishot;
    }

    public void setIshot(long ishot) {
        this.ishot = ishot;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
    }

    public long getRankpos() {
        return rankpos;
    }

    public void setRankpos(long rankpos) {
        this.rankpos = rankpos;
    }

    @Override
    public String toString() {
        return "FriendsModel{" +
                "uidx=" + uidx +
                ", fbuserid='" + fbuserid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", headpic='" + headpic + '\'' +
                ", luckcoin=" + luckcoin +
                ", ishot=" + ishot +
                ", pos=" + pos +
                ", timesid=" + timesid +
                ", luckcount=" + luckcount +
                ", income=" + income +
                ", rankpos=" + rankpos +
                '}';
    }

    public long getLuckcount() {
        return luckcount;
    }

    public void setLuckcount(long luckcount) {
        this.luckcount = luckcount;
    }

    public long getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }
}
